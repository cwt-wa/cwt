export type Role = "ROLE_USER" | "ROLE_ADMIN";

export interface JwtUser {
    id: number;
    username: string;
    email: string;
    roles: Role[];
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
    applicant: User;
}

export type ConfigurationKey = "RULES" | "NUMBER_OF_GROUP_MEMBERS_ADVANCING" | "GROUP_GAMES_BEST_OF" | "PLAYOFF_GAMES_BEST_OF" | "LITTLE_FINALE_GAME_BEST_OF" | "FINALE_GAME_BEST_OF";

export interface Configuration<T> {
    key: ConfigurationKey;
    value: T;
    created: Date;
    author: User;
}


export interface User {
    activated: boolean;
    authorities: Role[];
    email: string;
    id: number;
    resetDate: Date;
    resetKey: string;
    userProfile: any;
    userSetting: any;
    username: string;
}

export type GroupLabel = 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' | 'N' | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' | 'W' | 'X' | 'Y' | 'Z'

export interface GroupStanding {
    id: number;
    points: number;
    games: number;
    gameRatio: number;
    roundRatio: number;
    user: User;
}

export interface Group {
    id: number;
    label: GroupLabel;
    tournament: any;
    standings: GroupStanding[]
}

export interface GroupDto {
    label: GroupLabel;
    users: number[]
}
