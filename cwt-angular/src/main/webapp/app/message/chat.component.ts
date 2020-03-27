import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {JwtUser, Message, MessageCreationDto, MessageDto, PageDto} from "../custom";
import {AuthService} from "../_services/auth.service";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-chat',
    template: require('./chat.component.html')
})
export class    ChatComponent implements OnInit, OnDestroy {

    @Input() hideInput: boolean = false;
    @Input() admin: boolean = false;

    messages: MessageDto[] = [];
    messagePagingStart: number = 0;
    messageTotalElements: number;
    private readonly messagesSize = 15;
    authUser: JwtUser;

    private readonly fetchIntervalMillis = 10000;
    private fetchInterval: number;

    constructor(private requestService: RequestService,
                private authService: AuthService,
                private toastr: Toastr) {
    }

    ngOnInit(): void {
        this.authUser = this.authService.getUserFromTokenPayload();
        this.fetchMessages();
    }

    ngOnDestroy(): void {
        window.clearInterval(this.fetchInterval);
    }

    more(): void {
        this.messagePagingStart += 1;
        this.fetchMessages();
    }

    submit(message: Message, cb: (success: boolean) => void): void {
        const messageDto: MessageCreationDto = {
            body: message.body,
            category: message.category,
            recipients: message.recipients.map(u => u.id),
        };

        this.requestService.post<MessageCreationDto>('message', messageDto)
            .subscribe(res => {
                this.messages.unshift(res as unknown as MessageDto);
                this.messageTotalElements += 1;
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
                this.messages.splice(this.messages.findIndex(m => m.id === message.id), 1);
            })
    }

    filterPrivateOnly(): void {
        this.messages = this.messages.filter(m => m.category === "PRIVATE");
    }

    private fetchMessages() {
        const relativePath = this.admin ? 'message/admin' : 'message';
        this.requestService.getPaged<MessageDto>(relativePath, {size: this.messagesSize, start: this.messagePagingStart} as PageDto<MessageDto>)
            .subscribe(res => {
                this.messageTotalElements = res.totalElements;
                this.messagePagingStart = res.start;
                this.messages.push(...res.content);
                if (this.fetchInterval == null) this.fetchNewMessages();
            });
    }

    private fetchNewMessages() {
        const relativePath = this.admin ? 'message/admin/new' : 'message/new';

        const newestMessageCreated = this.messages
            .sort((a, b) => new Date(b.created).getTime() - new Date(a.created).getTime())[0].created;

        this.requestService.get<MessageDto[]>(relativePath, {after: Date.parse(newestMessageCreated).toString()})
            .subscribe(res => {
                this.messageTotalElements += res.length;
                this.messages.unshift(...res);
                if (this.fetchInterval == null) this.fetchInterval = window.setInterval(this.fetchNewMessages.bind(this), this.fetchIntervalMillis);
            });
    }
}
