import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Message, MessageCreationDto, MessageDto, PageDto} from "../custom";

@Component({
    selector: 'cwt-chat',
    template: require('./chat.component.html')
})
export class ChatComponent implements OnInit {

    messages: MessageDto[] = [];
    messagePagingStart: number = 0;
    messageTotalElements: number;
    private readonly messagesSize = 15;

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
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
