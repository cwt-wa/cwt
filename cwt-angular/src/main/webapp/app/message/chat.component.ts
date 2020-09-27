import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {JwtUser, Message, MessageCategory, MessageCreationDto, MessageDto} from "../custom";
import {AuthService} from "../_services/auth.service";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-chat',
    template: require('./chat.component.html')
})
export class ChatComponent implements OnInit, OnDestroy {

    @Input() hideInput: boolean = false;
    @Input() admin: boolean = false;

    filter: MessageCategory | null = null;
    authUser: JwtUser;
    private readonly messagesSize = 30;
    private readonly fetchIntervalMillis = 10000;
    private fetchInterval: number;
    private oldestMessage: number;
    private newestMessage: number;
    private endpoint: string;

    constructor(private requestService: RequestService,
                private authService: AuthService,
                private toastr: Toastr) {
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
        this.newestMessage = new Date(this.messages[0].created).getTime();
        this.oldestMessage = new Date(this.messages[this.messages.length - 1].created).getTime();
    }

    ngOnInit(): void {
        this.endpoint = this.admin ? 'message/admin' : 'message';
        this.authService.authState.then(user => this.authUser = user);
        this.requestService
            .get<MessageDto[]>(`${this.endpoint}`, {after: "0", size: this.messagesSize.toString()})
            .subscribe(res => {
                this.messages = res;
                this.shortPolling();
            });
    }

    ngOnDestroy(): void {
        window.clearInterval(this.fetchInterval);
    }

    submit(message: Message, cb: (success: boolean) => void): void {
        const messageDto: MessageCreationDto = {
            body: message.body,
            category: message.category,
            recipients: message.recipients.map(u => u.id),
        };

        this.requestService.post<MessageCreationDto>('message', messageDto)
            .subscribe(res => {
                this._messages.unshift(res as unknown as MessageDto);
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
        this.newestMessage = null;
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

    private shortPolling() {
        setTimeout(() => {
            this.requestService
                .get<MessageDto[]>(`${this.endpoint}`, {
                    after: this.newestMessage.toString(),
                    size: this.messagesSize.toString()
                })
                .subscribe(res => {
                    this.messages = [...res, ...this._messages];
                    this.shortPolling();
                })
        }, this.fetchIntervalMillis);
    }
}
