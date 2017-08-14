export interface JwtTokenPayload {
    sub: string;
    audience: string;
    created: number;
    context: {
        user: {
            id: number;
            username: string;
            email: string;
            authorities: ("ROLE_USER" | "ROLE_ADMIN")[];
            enabled: boolean;
        }
    };
    exp: number;
}
