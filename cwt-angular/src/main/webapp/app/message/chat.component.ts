import {Component, Inject, Input, OnDestroy, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {JwtUser, Message, MessageCategory, MessageCreationDto, MessageDto, UserMinimaDto} from "../custom";
import {AuthService} from "../_services/auth.service";
import {Toastr} from "../_services/toastr";
import {APP_CONFIG, AppConfig} from "../app.config";
import {finalize} from "rxjs/operators";

@Component({
    selector: 'cwt-chat',
    template: require('./chat.component.html')
})
export class ChatComponent implements OnInit, OnDestroy {

    @Input() hideInput: boolean = false;
    @Input() admin: boolean = false;

    filter: MessageCategory | null = null;
    authUser: JwtUser;
    suggestions: UserMinimalDto[]? = null;
    private allSuggestions: UserMinimalDto[] = [];
    private readonly messagesSize = 30;
    private oldestMessage: number;
    private endpoint: string;
    private eventSource: EventSource;

    private static readonly DELIMITER = /^[a-z0-9-_]*$/i;

    constructor(private requestService: RequestService,
                private authService: AuthService,
                private toastr: Toastr,
                @Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    _messages: MessageDto[] = [];

    get messages(): MessageDto[] {
        return (this.filter === null ? this._messages : this._messages.filter(m => m.category === this.filter))
            .sort((o1, o2) => new Date(o2.created).getTime() - new Date(o1.created).getTime());
    }

    set messages(messages: MessageDto[]) {
        this._messages = messages
            .reduce((acc, curr) => {
                if (acc.find(x => x.id === curr.id) == null) {
                    acc.push(curr);
                }
                return acc;
            }, <MessageDto[]>[]);
        let oldestMessage;
        if (this.messages.length === 1) {
          oldestMessage = this.messages[0];
        } else if (this.messages.length > 1) {
          oldestMessage = this.messages[this.messages.length - 1];
        }
        if (oldestMessage != null) {
          this.oldestMessage = new Date(oldestMessage.created).getTime();
        }
    }

    ngOnInit(): void {
        this.endpoint = this.admin ? 'message/admin' : 'message';
        this.authService.authState.then(user => {
            this.authUser = user

            this.requestService.get<UserMinimalDto[]>("message/suggestions")
                .subscribe(res => this.allSuggestions = res);

            document.addEventListener('click', e => {
                if (e.target.id === 'chat-input') {
                    this.suggest();
                } else {
                    this.suggestions = null;
                }
            });
        });
        this.requestService
            .get<MessageDto[]>(`${this.endpoint}`, {after: "0", size: this.messagesSize.toString()})
            .subscribe(res => {
                this.messages = res;
                this.setupEventSource();
            });
    }

    ngOnDestroy(): void {
        if (this.eventSource != null && this.eventSource.readyState !== 2) {
            this.eventSource.close();
        }
    }

    public complete(user, fromClick=false) {
        const inpElem = document.getElementById('chat-input');
        const [q, v, caret] = this.getProc();
        inpElem.value =
            v.substring(0, caret - q.length)
            + user.username
            + inpElem.value.substring(caret);
        this.suggestions = null;
        fromClick && inpElem.focus();
        inpElem.selectionStart = caret - q.length + user.username.length;
        inpElem.selectionEnd = inpElem.selectionStart;
    }

    public onInput(e) {
        if (this.authUser == null) return;
        this.suggest();
    }

    public onKeydown(e) {
        const key = e.key === 'Unidentified' ? String.fromCharCode(e.which) : e.key;
        if (!(this.suggestions?.length && ['ArrowDown', 'ArrowUp', 'Tab', 'Enter'].includes(key))) {
            return;
        }
        e.preventDefault();
        const buttons = Array.from(document.querySelectorAll('#chat-suggestions button'));
        let active;
        for (let i = 0; i < buttons.length; i++) {
            if (buttons[i].classList.contains('active')) {
                active = i;
                break;
            }
        }
        if (key === 'Enter') {
            const user = this.suggestions.find(x => x.id == buttons[active].value);
            if (user == null) return;
            this.complete(user);
        } else {
            if (active == null) {
                buttons[0].classList.add('active');
            } else {
                const up = key === 'ArrowUp' || (e.shiftKey && key === 'Tab')
                buttons[active].classList.remove('active');
                if (up && active == 0) {
                    buttons[buttons.length-1].classList.add('active');
                } else {
                    buttons[(active + (up ? -1 : +1)) % buttons.length].classList.add('active');
                }
            }
        }
    }

    public onKeyup(e) {
        const key = e.key === 'Unidentified' ? String.fromCharCode(e.which) : e.key;
        if (key.length > 1 && !['ArrowDown', 'ArrowUp', 'Tab', 'Enter'].includes(key)) {
            this.suggest();
        }
    }

    private suggest() {
        const [q, v, caret] = this.getProc();
        if (q == null) {
            this.suggestions = null;
            return;
        }

        // suggestions box positioning
        const inpElem = document.getElementById('chat-input');
        inpElem.parentElement.insertAdjacentHTML(
            'beforebegin',
            `<span id='dummy'>${v.substring(0, v.length-q.length)}</span>`);
        const dummyElem = document.getElementById('dummy');
        const {fontSize, fontFamily} = window.getComputedStyle(inpElem);
        dummyElem.style.fontSize = fontSize;
        dummyElem.style.fontFamily = fontFamily;
        dummyElem.style.paddingLeft = '17px';
        const offset = 80;
	    this.suggOffset = Math.min(
            inpElem.getBoundingClientRect().width - offset,
            dummyElem.getBoundingClientRect().width + offset) - offset;
        dummy.remove();

        this.suggestions = (this.suggestions || [])
            .filter(({username}) => username.toLowerCase().startsWith(q.toLowerCase()))

        if (this.suggestions.length < 4) {
            this.loadingSuggestions = true;
            this.requestService.get<UserMinimalDto[]>("message/suggestions", {q})
                .pipe(finalize(() => this.loadingSuggestions = false))
                .subscribe(res => {
                    this.allSuggestions.push(...res);
                    this.suggestions = res.slice(0, 5);
                });
        }

    }

    /**
     * Get current input typeahead process information.
     *
     * @param {string} Char just typed that's not yet part of the inputs
     *   value at the moment of calling this method.
     * @returns {Array} The current typeahead string and the string
     *   until associated @ sign and the caret index.
     */
    private getProc() {
        const {selectionStart, value} = document.getElementById('chat-input');
        const v = value.substring(0, selectionStart);
        if (v.indexOf('@') === -1) return [null, v, selectionStart];
        const rev = v.split("").reverse()
        const qRev = rev.slice(0, rev.indexOf("@"))
        const charBefAt = rev.slice(0, rev.indexOf("@")+2).pop();
        if (charBefAt != null && charBefAt.match(ChatComponent.DELIMITER) != null) {
            return [null, v, selectionStart];
        }
        const q = qRev.reverse().join('')
        console.log(q);
        if (!q.match(ChatComponent.DELIMITER)) return [null, v, selectionStart]
        return [q, v, selectionStart];
    }

    private setupEventSource() {
        this.eventSource = new EventSource(this.appConfig.apiEndpoint + 'message/listen');
        this.eventSource.onerror = (err: any) =>
            err?.originalTarget?.readyState === 2 && setTimeout(() => this.setupEventSource(), 1000);
        this.eventSource.onopen = e => console.log('listening for messages', e);
        this.eventSource.addEventListener("EVENT", e => {
            // @ts-ignore
            console.log('EVENT', e.data);
            // @ts-ignore
            if (e.data === 'START') return;
            // @ts-ignore
            this.messages = [JSON.parse(e.data), ...this._messages];
        });
    }

    submit(message: Message, cb: (success: boolean) => void): void {
        const messageDto: MessageCreationDto = {
            body: message.body,
            category: message.category,
            recipients: message.recipients?.map(u => u.id) || [],
        };
        this.requestService.post<MessageCreationDto>('message', messageDto)
            .subscribe(res => {
                this.messages = [res as unknown as MessageDto, ...this._messages];
                cb(true);
            }, () => cb(false));
    }

    deleteMessage(message: MessageDto) {
        const text = `Are you sure to delete this message?
${message.author.username}: ${message.body}`;
        if (!confirm(text)) return;
        this.requestService.delete(`message/${message.id}`)
            .subscribe(() => {
                this.toastr.success("Message has been deleted.");
                this._messages.splice(this.messages.findIndex(m => m.id === message.id), 1);
            })
    }

    filterBy(category: MessageCategory | null) {
        this.filter = category;
        this.oldestMessage = null;
        const queryParams = {
            after: "0",
            size: this.messagesSize.toString(),
            ...(category != null && {category})
        };
        this.requestService
            .get<MessageDto[]>(`${this.endpoint}`, queryParams)
            .subscribe(res => this.messages = res);
    }

    public fetchPastMessages(category?: MessageCategory) {
        const queryParams: { category?: string, before: string, size: string } = {
            ...(category != null && {category}),
            size: this.messagesSize.toString(),
            before: this.oldestMessage.toString()
        };
        this.requestService
            .get<MessageDto[]>(`${this.endpoint}`, queryParams)
            .subscribe(res => res.length
                    ? this.messages = [...this._messages, ...res]
                    : this.toastr.info('There are no more messages.'));
    }
}
