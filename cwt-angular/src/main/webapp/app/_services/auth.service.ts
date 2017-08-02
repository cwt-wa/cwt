import {Injectable} from "@angular/core";

@Injectable()
export class AuthService {

    private static readonly AUTH_TOKEN_STORAGE_KEY = 'auth-token';

    constructor() {
    }

    public storeToken(token: string): void {
        return localStorage.setItem(AuthService.AUTH_TOKEN_STORAGE_KEY, token);
    }

    public getToken(): string {
        return localStorage.getItem(AuthService.AUTH_TOKEN_STORAGE_KEY);
    }

    public voidToken(): void {
        return localStorage.removeItem(AuthService.AUTH_TOKEN_STORAGE_KEY);
    }

    public getTokenPayload(): {sub: string, audience: string, created: number, exp: number} {
        return JSON.parse(atob(this.getToken().split('.')[1]));
    }
}
