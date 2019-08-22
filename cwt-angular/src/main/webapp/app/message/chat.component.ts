import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {JwtUser, Message, MessageCreationDto, MessageDto, PageDto} from "../custom";
import {AuthService} from "../_services/auth.service";

@Component({
    selector: 'cwt-chat',
    template: require('./chat.component.html')
})
export class ChatComponent implements OnInit {

    messages: MessageDto[] = [];
    messagePagingStart: number = 0;
    messageTotalElements: number;
    private readonly messagesSize = 15;
    authUser: JwtUser;

    constructor(private requestService: RequestService,
                private authService: AuthService) {
    }

    ngOnInit(): void {
        this.authUser = this.authService.getUserFromTokenPayload();
        this.fetchMessages();
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

    private fetchMessages() {
        this.requestService.getPaged<MessageDto>('message', {size: this.messagesSize, start: this.messagePagingStart} as PageDto<MessageDto>)
            .subscribe(res => {
                this.messageTotalElements = res.totalElements;
                this.messagePagingStart = res.start;
                this.messages.push(...res.content);
            });
    }
}
