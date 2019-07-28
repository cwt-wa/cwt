import {HttpErrorResponse} from "@angular/common/http";

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

export type ConfigurationKey = "RULES" | "NUMBER_OF_GROUP_MEMBERS_ADVANCING" | "GROUP_GAMES_BEST_OF" | "PLAYOFF_GAMES_BEST_OF" | "FINALE_GAME_BEST_OF" | "NEWS" | "USERS_PER_GROUP" | "NUMBER_OF_GROUPS";

export interface Configuration {
    key: ConfigurationKey;
    value: string;
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

export interface CountryDto {
    id: number,
    flag: string;
    name: string;
}

export interface UserOverviewDto {
    id: number;
    username: string;
    country: CountryDto;
    participations: number;
    userStats: UserStatsDto[];
}

export interface UserStatsDto {
    participated: boolean;
    year: number;
    tournamentId: number;
    tournamentMaxRound: number;
    round: number;
    locRound: string;
}

export interface UserDetailDto {
    id: number;
    username: string;
    country: CountryDto;
    about: string;
    hasPic: boolean;
    userStats: UserStatsDto[];
}

export interface StreamDto {
    id: string;
    channel: string;
    userId: string;
    userName: string;
    title: string;
    description: string;
    createdAt: string;
    publishedAt: string;
    url: string;
    thumbnailUrl: string;
    viewable: string;
    viewCount: number;
    language: string;
    type: string;
    duration: string;
}

export interface UserMinimalDto {
    id: number;
    username: string;
}

export interface UserChangeDto {
    username: string;
    country: number;
    about: string;
}

export interface PasswordChangeDto {
    currentPassword: string;
    newPassword: string;
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
    games: GameCreationDto[]
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

export interface GameDetailDto {
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
    replayExists: boolean;
    playoffRoundLocalized: string;
}

export interface PlayoffGameDto {
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
    replayExists: boolean;
    bets: PlayoffTreeBetDto[];
    playoffRoundLocalized: string;
}

export interface PlayoffTreeBetDto {
    id: number;
    user: UserMinimalDto;
    betOnHome: Boolean;
}

export interface BetDto {
    id: number;
    user: UserMinimalDto;
    game: GameDetailDto;
    betOnHome: Boolean;
}

export interface GameCreationDto {
    id: number;
    homeUser: number;
    awayUser: number;
    playoff: {
        round: number;
        spot: number;
    }
}

export interface GameTechWinDto {
    winner: number;
    loser: number;
}

export interface ReplacePlayerDto {
    toBeReplaced: number;
    replacement: number;
}

export interface RatingDto {
    type: RatingType;
    user: number
}

export interface CommentDto {
    user: number;
    body: string;
}

export type TournamentStatus = "OPEN" | "GROUP" | "PLAYOFFS" | "FINISHED";

export interface Tournament {
    id: number;
    status: TournamentStatus;
    review: String;
    open: string;
    created: string;
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
    author: User;
    created: number;
    recipients: User[];
    newsType: MessageNewsType;
    category: MessageCategory;
}

export type MessageNewsType = "REPORT" | "RATING" | "COMMENT"


export interface MessageDto {
    body: string;
    category: MessageCategory;
    recipients: number[];
}


export interface ServerError extends HttpErrorResponse {
    error: {
        message: string;
        path: string;
        status: number;
        timestamp: Date;
    }
}

export interface ValueLabel {
    value: string;
    label: string;
}

export interface PageDto<T, S = string | ValueLabel> {
    content: T[];
    size: number;
    start: number;
    sortBy: string;
    sortAscending: boolean;
    totalPages: number;
    totalElements: number;
    sortables: S[];
}
