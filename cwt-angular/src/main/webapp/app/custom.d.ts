export interface JwtUser {
    id: number;
    username: string;
    email: string;
    roles: ("ROLE_USER" | "ROLE_ADMIN")[];
    enabled: boolean;
}

export interface JwtTokenPayload {
    sub: string;
    audience: string;
    created: number;
    context: {
        user: JwtUser
    };
    exp: number;
}


interface Application {
    id: number;
    created: Date;
    revoked: boolean;
    tournament: any;
    applicant: any;
}
