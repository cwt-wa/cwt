import {
    ElementRef,
    EventEmitter,
    Component,
    Output,
    Input,
    ViewChild,
} from '@angular/core';
import {MessageCreationDto, UserMinimalDto, MessageDto, JwtUser, ZemkeTagboxElement} from "../custom";
import {AuthService} from "../_services/auth.service";
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-chat-input',
    template: require('./chat-input.component.html'),
})
export class ChatInputComponent {

    @Output()
    message: EventEmitter<[MessageCreationDto, (success: boolean) => void]> = new EventEmitter();

    @Input()
    messages: MessageDto[];

    @ViewChild('tagbox') tagbox: ElementRef<ZemkeTagboxElement>;
    @ViewChild('valueEl') valueEl: ElementRef<HTMLInputElement>;

    private users: UserMinimalDto[];

    value = '';
    recipients: number[] = [];
    _disabled = false;
    suggestions: UserMinimalDto[] = null;

    private authUser: JwtUser;

    constructor(private requestService: RequestService,
                private authService: AuthService) {
    }

    get chatInputEl() {
        return this.tagbox.nativeElement.zemkeInput;
    }

    get disabled() {
        return this._disabled;
    }

    set disabled(v) {
        this._disabled = v;
        v
            ? this.chatInputEl.setAttribute('disabled', '')
            : this.chatInputEl.removeAttribute('disabled');
    }

    ngOnInit(): void {
        this.authService.authState.then(user => {
            this.authUser = user;
            const mm = this.messages;
            this.suggestions = [
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

    async lazyLoad(e: KeyboardEvent) {
        if (!(e.target as HTMLInputElement).value.includes('@')) return;
        if (!this.users) {
            this.users = await this.requestService.get<UserMinimalDto[]>("user", {minimal: "true"})
                .toPromise();
            console.log('req users', this.users);
        }
        this.suggestions.push(
            ...this.users.filter(user =>
                !this.suggestions.map(u => u.id).includes(user.id) && user.id !== this.authUser.id));
    }

    submit(valid: boolean) {
        if (!valid) return;
        this.disabled = true;
        const message: MessageCreationDto = {
            body: this.value,
            recipients: this.recipients,
            category: this.recipients?.length ? 'PRIVATE' : 'SHOUTBOX',
        };
        this.message.emit([message, (success: boolean) => {
            this.disabled = false;
            if (success) {
                this.valueEl.nativeElement.value = '';
                this.valueEl.nativeElement.dispatchEvent(new Event('input'));
                this.value = '';
                this.recipients = [];
            }
            setTimeout(() => this.chatInputEl.focus());
        }]);
    }
}

