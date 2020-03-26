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
import {Utils} from "../_util/utils";

type ContentEditableKeyboardEvent = KeyboardEvent & {
    target: HTMLDivElement;
}

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
            .chat-input > .form-control[contenteditable] {
                flex-grow: 5;
                font-size: 16px;
            }`,

            `
            .chat-input > .form-control[contenteditable]:empty::before {
                content: "Type message...";
                color: gray;
            }`,

            `
            .chat-input > .form-control[contenteditable].single-line {
                white-space: nowrap;
                height: 48px;
                overflow: hidden;
                padding-top: 12px;
            }`,

            `
            .chat-input > .form-control[contenteditable].single-line br {
                display: none;
            }`,

            `
            .chat-input > .form-control[contenteditable].single-line * {
                display: inline;
                white-space: nowrap;
            }`
    ]
})
export class ChatInputComponent implements OnInit {

    @Output()
    message: EventEmitter<[Message, (success: boolean) => void]> = new EventEmitter();

    @ViewChild("chatInput")
    private chatInput: ElementRef;

    private mentions: ComponentRef<MentionComponent>[] = [];
    private users: User[];
    private readonly isMobileDevice: boolean;

    submitting: boolean = false;

    constructor(private resolver: ComponentFactoryResolver,
                private injector: Injector,
                private app: ApplicationRef,
                private requestService: RequestService,
                utils: Utils) {
        this.isMobileDevice = utils.isMobileDevice();
    }

    public ngOnInit(): void {
        this.requestService.get<User[]>("user").subscribe(res => this.users = res);
    }

    public sendMessage(): void {
        const mentionHasJustBeenAdded = this.mentions.find(m => m.instance.enterKeyDown);
        if (mentionHasJustBeenAdded != null) {
            mentionHasJustBeenAdded.instance.enterKeyDown = false;
            return;
        }

        const body = this.convertContentEditableToRawTextContent();
        if (body === "" || body == null) return;
        this.disable(true);
        this.mentions = this.mentions.filter(m => m.location.nativeElement.parentElement);
        const message: Message = {
            body: body,
            recipients: this.mentions.map(ref => ref.instance.mentionedUser),
            category: this.mentions.length === 0 ? 'SHOUTBOX' : 'PRIVATE'
        } as Message;
        this.message.emit([message, (success: boolean) => {
            this.disable(false);
            success === true && this.reset();
            setTimeout(() => (this.chatInput.nativeElement as HTMLDivElement).focus(), 0);
        }]);
    }

    private disable(disable: boolean) {
        this.submitting = disable;
        this.mentions.forEach(value => value.instance.disabled = disable);
    }

    private reset() {
        this.chatInput.nativeElement.innerHTML = '';
    }

    public async keyDown(e: ContentEditableKeyboardEvent) {
        this.isMobileDevice && await new Promise((resolve, _) => setTimeout(() => resolve(), 100));
        if (this.submitting) return;
        if (e.target !== this.chatInput.nativeElement) return;
        if (!(e.key === '@'
            || (e.key === 'Unidentified'
                && e.target.textContent.substring(e.target.textContent.length - 1) === '@'))) {
            return;
        }

        e.preventDefault();

        const precedingChar = this.retrievePrecedingChar(e);
        if (precedingChar !== "" && precedingChar !== "—" /* em-dash */ && precedingChar.match(/\s/) == null) {
            return;
        }

        this.instantiateMention();

        if (this.isMobileDevice) {
            const orphanedAtSign = Array.from(e.target.childNodes)
                .findIndex(n => n.nodeType === Node.TEXT_NODE && n.textContent.endsWith('@'));

            if (orphanedAtSign + 1 < e.target.childNodes.length
                    && Array.from(e.target.childNodes)[orphanedAtSign + 1].textContent.startsWith("@[m")) {
                if (e.target.childNodes[orphanedAtSign].textContent === '@') {
                    e.target.childNodes[orphanedAtSign].remove();
                } else {
                    e.target.childNodes[orphanedAtSign].textContent =
                        e.target.childNodes[orphanedAtSign].textContent
                            .substring(0, e.target.childNodes[orphanedAtSign].textContent.length - 1);
                }
            }
        }
    }

    private retrievePrecedingChar(e: ContentEditableKeyboardEvent) {
        let textToGoAbout;
        if (this.isMobileDevice) {
            if (e.target.textContent.length === 1) {
                return '';
            } else {
                textToGoAbout = e.target.textContent.substring(e.target.textContent.length - 1);
            }
        } else {
            textToGoAbout = e.target.textContent;
        }
        return textToGoAbout.substring(window.getSelection().anchorOffset - 1);
    }

    public onPaste(e: ClipboardEvent) {
        document.execCommand("insertHTML", false, e.clipboardData.getData('text/plain'));
        e.preventDefault();
    }

    public onCopy(e: ClipboardEvent) {
        e.clipboardData.setData('text/plain', this.selectionToTextContent());
        e.preventDefault();
    }

    public onCut(e: ClipboardEvent) {
        e.clipboardData.setData('text/plain', this.selectionToTextContent(true));
        e.preventDefault();
    }

    private selectionToTextContent(cut: boolean = false) {
        const selection = window.getSelection();
        const range = selection.getRangeAt(0);
        const rememberedStartOffset = range.startOffset;
        const rangeIsOneContainerOnly = range.startContainer === range.endContainer;
        const middleNodes = [];
        let textToCut = '';

        let node: Node = null;
        do {
            node = node === null ? range.startContainer : node.nextSibling;

            if (rangeIsOneContainerOnly) {
                textToCut += node.textContent.split('').splice(range.startOffset, range.endOffset - range.startOffset).join('');
            } else if (node === range.startContainer) {
                textToCut += node.textContent.split('').splice(range.startOffset).join('');
            } else if (node === range.endContainer) {
                textToCut += node.textContent.split('').splice(0, range.endOffset).join('');
            } else {
                textToCut += node.textContent;
                middleNodes.push(node);
            }

        } while (node !== range.endContainer);

        function cutFromNodes(textContent: string, cutStart: number, cutCount?: number) {
            const charArr = textContent.split('');
            cutCount != null ? charArr.splice(cutStart, cutCount): charArr.splice(cutStart);
            return charArr.join('');
        }

        if (cut) {
            middleNodes.forEach(n => n.parentNode.removeChild(n));
            if (rangeIsOneContainerOnly) {
                range.startContainer.textContent = cutFromNodes(
                    range.startContainer.textContent, range.startOffset, range.endOffset - range.startOffset);
            } else {
                range.startContainer.textContent = cutFromNodes(range.startContainer.textContent, range.startOffset);
                range.endContainer.textContent = cutFromNodes(range.endContainer.textContent, 0, range.endOffset);
            }

            const newRange = document.createRange();
            newRange.collapse(true);
            newRange.setStart(range.startContainer, rememberedStartOffset);
            selection.removeAllRanges();
            selection.addRange(newRange);
        }

        return this.convertContentEditableToRawTextContent(textToCut);
    }

    private convertContentEditableToRawTextContent(textContent: string = this.chatInput.nativeElement.textContent): string {
        let body: string = textContent;

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
