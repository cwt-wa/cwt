import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Message, MessageDto} from "../custom";

@Component({
    selector: 'cwt-chat',
    template: require('./chat.component.html')
})
export class ChatComponent implements OnInit {
    messages: Message[];

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.requestService.get<Message[]>('message')
            .subscribe(res => this.messages = res);
    }

    submit(message: Message): void {
        const messageDto: MessageDto = {
            body: message.body,
            category: message.category,
            recipients: message.recipients.map(u => u.id),
        };

        this.requestService.post<Message>('message', messageDto).subscribe(res => this.messages.unshift(res));
    }
}
