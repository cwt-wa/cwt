import {
    ApplicationRef,
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    ComponentRef,
    ElementRef,
    EventEmitter,
    Injector,
    OnInit,
    Output,
    Input,
    ViewChild,
    ViewChildren,
    AfterViewInit,
    QueryList
} from '@angular/core';
import {MentionComponent} from './mention.component';
import {Message, User} from "../custom";
import {AuthService} from "../_services/auth.service";
import {RequestService} from "../_services/request.service";
import {Utils} from "../_util/utils";

@Component({
    selector: 'cwt-chat-input',
    template: require('./chat-input.component.html'),
    styles: [`
        .chat-container {
            position: relative;
        }
        .chat-suggestions {
            position: absolute;
        }
        .offsets {
            border: 1px solid red;
            position: absolute;
            pointer-events: none;
            overflow: hidden;
            top: 0;
        }
        .offset {
            background-color: rgba(99, 1, 45, 0.35);
            pointer-events: none;
            position: absolute;
            top: .5rem;
            bottom: .5rem;
            z-index: 99;
            border-top-left-radius: 1rem;
            border-bottom-left-radius: 1rem;
            border-top-right-radius: .5rem;
            border-bottom-right-radius: .5rem;
        }
    `]
})
export class ChatInputComponent implements OnInit, AfterViewInit {

    private static readonly DELIMITER = /^[a-z0-9-_]*$/i;

    @Output()
    message: EventEmitter<[Message, (success: boolean) => void]> = new EventEmitter();

    @Input()
    messages: MessageDto[];

    @ViewChild("chatInput")
    private chatInputEl: ElementRef<HTMLInputElement>;

    @ViewChild("dummy")
    private dummyEl: ElementRef<HTMLDivElement>;

    @ViewChild("offsets")
    private offsetsEl: ElementRef<HTMLDivElement>;

    @ViewChildren("suggestions")
    private suggestionsEl: QueryList<ElementRef<HTMLDivElement>>;

    @ViewChildren("recipients")
    private recipientsEl: QueryList<ElementRef<HTMLDivElement>>;

    suggestions: UserMinimalDto[]? = null;
    recipients: UserMinimalDto[] = [];
    tags = [];

    private authUser: JwtUser;
    private allSuggestions: UserMinimalDto[] = [];
    private lazyLoadedSuggestions: boolean = false;
    private paddingLeft: number = 0;

    constructor(private requestService: RequestService,
                private authService: AuthService) {
    }

    ngOnInit(): void {
        this.authService.authState.then(user => {
            this.authUser = user;
            const mm = this.messages;
            this.allSuggestions = [
                mm.find(m => m.recipients.map(u => u.id).includes(this.authUser.id))?.author,
                ...(mm.find(m => m.author.id === this.authUser.id && m.recipients.length)?.recipients || []),
                ...(mm.map(m => m.author) || []),
                ...(mm.flatMap(m => m.recipients) || [])
            ].reduce((acc, curr) => {
                if (curr != null
                    && curr.id !== this.authUser.id
                    && !acc.map(u => u.id).includes(curr.id)) {
                    acc.push(curr);
                }
                return acc;
            }, []);
        });
    }

    ngAfterViewInit() {
        // TODO uninstall in OnDestroy
        document.addEventListener('click', e => {
            if (e.target.id === 'chat-input') {
                this.suggest();
            } else {
                this.suggestions = null;
            }
        });

        // TODO uninstall in OnDestroy
        new ResizeObserver(([entry, ..._]) => this.updateRecipients())
            .observe(this.chatInputEl.nativeElement);

        const {fontSize, fontFamily, paddingLeft, paddingRight, width, height} =
            window.getComputedStyle(this.chatInputEl.nativeElement);

        // TODO make part of ResizeObserver, maybe
        this.dummyEl.nativeElement.style.fontSize = fontSize;
        this.dummyEl.nativeElement.style.fontFamily = fontFamily;
        this.dummyEl.nativeElement.style.paddingLeft = paddingLeft;
        this.dummyEl.nativeElement.style.whiteSpace = 'pre';
        this.dummyEl.nativeElement.style.marginLeft = `-${this.chatInputEl.nativeElement.scrollLeft}px`;

        // TODO make part of ResizeObserver
        this.paddingLeft = parseFloat(paddingLeft);
        this.offsetsEl.nativeElement.style.width =
            parseFloat(width) - this.paddingLeft - parseFloat(paddingRight) + 'px';
        this.offsetsEl.nativeElement.style.marginLeft = paddingLeft;
        this.offsetsEl.nativeElement.style.marginRight = paddingRight;
        this.offsetsEl.nativeElement.style.height = height;
    }

    public complete(user, fromClick=false) {
        const inpElem = this.chatInputEl.nativeElement;
        const [q, v, caret] = this.getProc();
        inpElem.value =
            v.substring(0, caret - q.length)
            + user.username
            + inpElem.value.substring(caret);
        this.suggestions = null;
        fromClick && inpElem.focus();
        inpElem.selectionStart = caret - q.length + user.username.length;
        inpElem.selectionEnd = inpElem.selectionStart;
        this.updateRecipients();
    }

    public onKeydown(e) {
        const key = e.key === 'Unidentified' ? String.fromCharCode(e.which) : e.key;
        if (this.suggestions?.length && ['ArrowDown', 'ArrowUp', 'Tab', 'Enter'].includes(key)) {
            e.preventDefault();
            const buttons = Array.from(this.suggestionsEl).map(el => el.nativeElement);

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
        this.updateRecipients();
    }

    public onKeyup(e) {
        const key = e.key === 'Unidentified' ? String.fromCharCode(e.which) : e.key;
        if (key.length > 1 && !['ArrowDown', 'ArrowUp', 'Tab', 'Enter', 'Backspace', 'Delete'].includes(key)) {
            this.suggest();
        }
        this.updateRecipients();
    }

    public onInput(e) {
        if (this.authUser == null) return;
        this.suggest();
        this.updateRecipients();
    }

    private async updateRecipients() {
        if (this.authUser == null) return;

        const {value, scrollLeft} = this.chatInputEl.nativeElement;
        const matchAll = Array.from(value.matchAll(/(?:^|[^a-z0-9-_])@([a-z0-9-_]+)/ig));
        const matches = [];
        for (const m of matchAll) {
             let user;
             for (let i = 0; i < 2; i++) {
                 user = this.allSuggestions.find(u => u.username.toLowerCase() === m[1].toLowerCase());
                 if (user == null && !this.lazyLoadedSuggestions) {
                    await this.lazyLoadAllSuggestions();
                 }
             }
             if (user != null) {
                 matches.push({ index: m.index + m[0].indexOf('@'), user });
             }
        }

        this.recipients = matches.map(({user}) => user).filter((v,i,a) => a.indexOf(v) === i);
        this.tags = matches.map(m => ({
            user: m.user,
            style: {
                width: `${this.getOffset(m.user.username)+4}px`, // 4 is arbitrary width increase
                left: `${this.getOffset(value.substring(0, m.index))-this.paddingLeft-scrollLeft}px`,
            }
        }));
    }

    private getOffset(v: string) {
        this.dummyEl.nativeElement.textContent = v;
        const res = this.dummyEl.nativeElement.getBoundingClientRect().width;
        // TODO remove dummy
        return res;
    }

    private suggest() {
        if (this.authUser == null) return;

        const [q, v, caret] = this.getProc();
        if (q == null) {
            this.suggestions = null;
            return;
        }

        this.suggestions = this.allSuggestions
            .filter(({username}) => username.toLowerCase().startsWith(q.toLowerCase()))
            .slice(0, 4);

        this.lazyLoadAllSuggestions();
    }

    private async lazyLoadAllSuggestions() {
        if (this.lazyLoadedSuggestions) {
            return;
        }
        await this.requestService.get<UserMinimalDto[]>("user", {minimal: true}).toPromise()
            .then(users => this.allSuggestions.push(
                ...users.filter(user =>
                    !this.allSuggestions.map(u => u.id).includes(user.id) && user.id !== this.authUser.id)));
        this.lazyLoadedSuggestions = true;
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
        const {selectionStart, value} = this.chatInputEl.nativeElement;
        const v = value.substring(0, selectionStart);
        if (v.indexOf('@') === -1) return [null, v, selectionStart];
        const rev = v.split("").reverse()
        const qRev = rev.slice(0, rev.indexOf("@"))
        const charBefAt = rev.slice(0, rev.indexOf("@")+2).pop();
        if (charBefAt != null && ChatInputComponent.DELIMITER.test(charBefAt)) {
            return [null, v, selectionStart];
        }
        const q = qRev.reverse().join('')
        if (!ChatInputComponent.DELIMITER.test(q)) return [null, v, selectionStart]
        return [q, v, selectionStart];
    }
}
