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
    ViewChild
} from '@angular/core';
import {MentionComponent} from './mention.component';
import {Message, User} from "../custom";
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'chatInput',
    template: require('./chat-input.component.html'),
    styles: [
            `
            .chat-input {
                display: flex;
                flex-direction: row;
                flex-wrap: nowrap;
                justify-content: space-between;
                align-items: stretch;
            }`,

            `
            .chat-input button.submit {
                border: 1px solid #40352a;
            }`,

            `
            .chat-input > .form-control[contenteditable="true"] {
                flex-grow: 5;
                font-size: 16px;
            }`,

            `
            .chat-input > .form-control[contenteditable="true"]:empty::before {
                content: "Type message...";
                color: gray;
            }`,

            `
            .chat-input > .form-control[contenteditable="true"].single-line {
                white-space: nowrap;
                height: 48px;
                overflow: hidden;
            }`,

            `
            .chat-input > .form-control[contenteditable="true"].single-line br {
                display: none;
            }`,

            `
            .chat-input > .form-control[contenteditable="true"].single-line * {
                display: inline;
                white-space: nowrap;
            }`
    ]
})
export class ChatInputComponent implements OnInit {

    @Output()
    message: EventEmitter<Message> = new EventEmitter();

    @ViewChild("chatInput")
    private chatInput: ElementRef;

    private mentions: ComponentRef<MentionComponent>[] = [];
    private users: User[];

    constructor(private resolver: ComponentFactoryResolver,
                private injector: Injector,
                private app: ApplicationRef,
                private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.requestService.get<User[]>("user").subscribe(res => this.users = res);
    }

    public sendMessage(): void {
        this.mentions = this.mentions.filter(m => m.location.nativeElement.parentElement);
        const message: Message = {
            body: this.convertContentEditableToRawTextContent(),
            recipients: this.mentions.map(ref => ref.instance.mentionedUser),
            category: this.mentions.length === 0 ? 'SHOUTBOX' : 'PRIVATE'
        } as Message;
        this.message.emit(message);
    }

    public keyDown(event: KeyboardEvent): void {
        if (event.key !== '@') {
            return;
        }

        // Don't turn email addresses into mentions.
        const precedingChar = (event.target as HTMLDivElement).textContent.substring(window.getSelection().anchorOffset - 1);
        if (precedingChar !== "" && precedingChar !== "—" /* em-dash */ && precedingChar.match(/\s/) == null) {
            return;
        }

        event.preventDefault();
        this.instantiateMention();
    }

    private convertContentEditableToRawTextContent(): string {
        let body: string = this.chatInput.nativeElement.textContent;

        body = body.trim();
        body = body.replace(/(\[m.*?m])\n/g, "$1"); // Remove carriage return after mention.
        body = body.replace(/\s+/g, " "); // Multiple whitespaces into one.
        body = body.replace(/(@\[m.*?m])([^\s?!.…—-])/g, "$1 $2"); // I guess it's likely people forgot a space after the mention.
        body = body.replace(/@\[m(.*?)m]/g, "@$1"); // Convert mention which is wrapped in `[m` and `m]` to simply `@Mention`.

        return body;
    }

    private instantiateMention(): void {
        const selection: Selection = window.getSelection();
        const range: Range = selection.getRangeAt(0);
        range.deleteContents();

        const node: HTMLDivElement = document.createElement('div');
        node.style.display = 'inline-block';
        range.insertNode(node);

        const factory: ComponentFactory<MentionComponent> = this.resolver.resolveComponentFactory(MentionComponent);
        const ref: ComponentRef<MentionComponent> = factory.create(this.injector, [], node);

        ref.instance.removeMention
            .subscribe(() => {
                ref.destroy();
                this.chatInput.nativeElement.focus();
            });
        ref.instance.suggestedUsers = this.users;

        this.mentions.push(ref);
        this.app.attachView(ref.hostView);
    }
}
