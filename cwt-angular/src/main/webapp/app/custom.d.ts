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

export type ConfigurationKey = "RULES" | "NUMBER_OF_GROUP_MEMBERS_ADVANCING" | "GROUP_GAMES_BEST_OF" | "PLAYOFF_GAMES_BEST_OF" | "FINALE_GAME_BEST_OF" | "NEWS";

export interface Configuration<T> {
    key: ConfigurationKey;
    value: T;
    modified: Date;
    author?: User;
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
    games: Game[]
}

export interface GroupDto {
    label: GroupLabel;
    users: number[]
}

export interface ReportDto {
    user: number;
    opponent: number;
    scoreOfUser: number;
    scoreOfOpponent: number;
}

export interface Comment {
    id: number;
    body: string;
    deleted: boolean;
    created: Date;
    modified: Date;
    author: User;
}

export type RatingType = "DARKSIDE" | "LIGHTSIDE" | "LIKE" | "DISLIKE";

export interface Rating {
    type: RatingType
    user: User;
}

export interface Game {
    id: number;
    homeUser: User;
    awayUser: User;
    playoff: {
        round: number;
        spot: number;
    },
    scoreHome?: number;
    scoreAway?: number;
    group?: Group;
    comments?: Comment[];
    reporter?: User;
    ratings?: Rating[];
    tournament: Tournament;
}

export interface GameDto {
    id: number;
    homeUser: number;
    awayUser: number;
    playoff: {
        round: number;
        spot: number;
    }
}

export interface RatingDto {
    type: RatingType;
    user: number
}

export interface CommentDto {
    user: number;
    body: string;
}

export type TournamentStatus = "OPEN" | "GROUP" | "PLAYOFFS" | "FINISHED" | "ARCHIVED";

export interface Tournament {
    id: number;
    status: TournamentStatus;
    review: String;
    open: Date;
    created: Date;
    host: User;
    bronzeWinner: User;
    silverWinner: User;
    goldWinner: User;
}


export interface ConfigurationDto {
    value: string;
    key: ConfigurationKey;
}

export type TimeUnit = "YEAR" | "MONTH" | "DAY" | "HOUR" | "MINUTE" | "SECOND";

export interface TimeAgo {
    value: number;
    unit: TimeUnit;
    original: Date;
}

export type MessageCategory = "SHOUTBOX" | "PRIVATE" | "NEWS";

export interface Message {
    body: string;
    user: User;
    created: number;
    recipients: User[];
    category: MessageCategory;
}


export interface MessageDto {
    body: string;
    category: MessageCategory;
    recipients: number[];
}
