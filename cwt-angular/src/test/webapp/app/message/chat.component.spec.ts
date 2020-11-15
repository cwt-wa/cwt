import {ChatComponent} from "../../../../main/webapp/app/message/chat.component";
import {RequestService} from "../../../../main/webapp/app/_services/request.service";
import {AuthService} from "../../../../main/webapp/app/_services/auth.service";
import {Toastr} from "../../../../main/webapp/app/_services/toastr";
import {AppConfig} from "../../../../main/webapp/app/app.config";
import {MessageCategory, MessageDto} from "../../../../main/webapp/app/custom";

describe('Chat', () => {
    let component: ChatComponent;
    let requestService: RequestService;
    let authService: AuthService;
    let toastr: Toastr;
    let appConfig: AppConfig;
    const noop = () => {};

    function message(id: number, created: string, category: MessageCategory) {
        return {id, created, category};
    }

    const messages = [
        message(2, "2020-11-15T18:38:34.822Z", 'SHOUTBOX'),
        message(1, "2020-11-15T19:38:34.822Z", 'NEWS'),
        message(4, "2020-11-15T16:38:34.822Z", 'SHOUTBOX'),
    ];

    beforeEach(() => {
        requestService = jasmine.createSpyObj('requestService', ['get', 'post']);
        authService = {
            authState: Promise.resolve(null)
        } as unknown as AuthService;
        (requestService.get as any).and.returnValue({
            subscribe(fn: (msg: MessageDto[]) => void) {
                fn(messages as any);
            }
        });
        spyOn(authService, 'authState');
        toastr = {} as unknown as Toastr;
        appConfig = {} as unknown as AppConfig;
        component = new ChatComponent(requestService, authService, toastr, appConfig);
        component.admin = false;
    });

    it('calls admin service if and only if needed', () => {
        const queryParams = {after: '0', size: '30'};
        component.admin = true;
        component.ngOnInit();
        expect(requestService.get).toHaveBeenCalledWith('message/admin', queryParams);
        component.admin = false;
        component.ngOnInit();
        expect(requestService.get).toHaveBeenCalledWith('message', queryParams);
    });

    it('sorts messages', () => {
        component.ngOnInit();
        expect(component.messages.length).toBe(3);
        expect(component.messages[0].id).toBe(1);
        expect(component.messages[1].id).toBe(2);
        expect(component.messages[2].id).toBe(4);
        expect((component as any).oldestMessage).toBe(new Date(messages[2].created).getTime());
    });

    it('submits a new message', () => {
        const newMessage = message(5, "2020-11-16T16:38:34.822Z", 'SHOUTBOX');
        (requestService.post as any).and.returnValue({
            subscribe(fn: any) {
                fn(newMessage);
            }
        });
        component.ngOnInit();
        component.submit(newMessage as any, noop);
        expect(component.messages.length).toBe(4);
        expect(component.messages[0].id).toBe(5);
        expect(component.messages[1].id).toBe(1);
        expect(component.messages[2].id).toBe(2);
        expect(component.messages[3].id).toBe(4);
    });

    it('doesn\'t return duplicates', () => {
        (requestService.post as any).and.returnValue({
            subscribe(fn: (msg: any) => void) {
                fn(message(4, "2020-11-15T16:38:34.822Z", 'SHOUTBOX'));
            }
        });
        component.ngOnInit();
        expect(component.messages.length).toBe(3);
        component.submit(messages[2] as any, noop);
        expect(component.messages.length).toBe(3);
    });

    it('can hop between filter', () => {
        component.ngOnInit();
        expect(component.messages.length).toBe(3);
        component.filterBy('NEWS');
        expect(component.messages.length).toBe(1);
        expect(component.messages[0].id).toBe(1);
        expect(component.messages[0].category).toBe('NEWS');
        component.filterBy(null);
        expect(component.messages.length).toBe(3);
    });
});

