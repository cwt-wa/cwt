import {Injectable} from "@angular/core";
import {JwtTokenPayload, JwtUser} from "../user/model/jwt-token-payload";

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

    public getTokenPayload(): JwtTokenPayload {
        const token: string = this.getToken();
        return token ? JSON.parse(atob(token.split('.')[1])) : null;
    }

    public getUserFromTokenPayload(): JwtUser {
        const tokenPayload: JwtTokenPayload = this.getTokenPayload();

        return tokenPayload && tokenPayload.context
            ? tokenPayload.context.user
            : null;
    }
}
