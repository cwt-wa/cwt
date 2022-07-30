import {
    ElementRef,
    EventEmitter,
    Component,
    OnInit,
    Output,
    Input,
    OnDestroy,
    ViewChild,
    ViewChildren,
    AfterViewInit,
    QueryList
} from '@angular/core';
import {Message, UserMinimalDto, MessageDto, JwtUser} from "../custom";
import {AuthService} from "../_services/auth.service";
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-chat-input',
    template: require('./chat-input.component.html'),
})
export class ChatInputComponent implements OnInit, AfterViewInit, OnDestroy {

    private static readonly DELIMITER = /^[a-z0-9-_]*$/i;

    @Output()
    message: EventEmitter<[Message, (success: boolean) => void]> = new EventEmitter();

    @Input()
    messages: MessageDto[];

    suggestions: UserMinimalDto[] = null;

    private authUser: JwtUser;
    private allSuggestions: UserMinimalDto[] = [];

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

    public submit() {
        this.disabled = true;
        const message = {
            body: this.chatInputEl.nativeElement.value,
            recipients: this.recipients,
            category: this.recipients?.length ? 'PRIVATE' : 'SHOUTBOX',
        } as Message;
        this.message.emit([message, (success: boolean) => {
            this.disabled = false;
            if (success) {
                //this.recipients = [];
                //this.tags = [];
                //this.suggestions = null;
                //this.chatInputEl.nativeElement.value = '';
            }
            //setTimeout(() => this.chatInputEl.nativeElement.focus());
        }]);
    }
}

