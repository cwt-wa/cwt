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

export interface UserRegistrationDto {
    username: string;
    email: string;
    password: string;
    captchaToken: string;
    wormnetChannel: string;
}

interface Application {
    id: number;
    created: string;
    revoked: boolean;
    tournament: any;
    applicant: User;
}

export type ConfigurationKey = "RULES" | "GROUP_GAMES_BEST_OF" | "PLAYOFF_GAMES_BEST_OF" | "FINALE_GAME_BEST_OF" | "NEWS" | "USERS_PER_GROUP" | "NUMBER_OF_GROUPS" | "EVENT_SOURCE_TWITCH_WEBHOOK" | "WA_3_8_WARNING";

export interface Configuration {
    key: ConfigurationKey;
    value: string;
    modified: string;
    author?: User;
}


export interface User {
    activated: boolean;
    authorities: Role[];
    email: string;
    id: number;
    resetDate: string;
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
    email?: string;
}

export interface ChannelDto {
    id: string;
    title: string;
    user: UserMinimalDto;
    displayName: string;
    type: string;
    profileImageUrl: string;
    viewCount: number;
    broadcasterType: string;
    offlineImageUrl: string;
    login: string;
    description: string;
    botInvited: boolean;
    modified: string;
    created: string;
}

export interface ChannelCreationDto {
    twitchLoginName: string;
    title: string;
    user: number;
}

export interface StreamDto {
    id: string;
    channel: ChannelDto;
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
    game?: GameDetailDto;
}

export interface ScheduleDto {
    id: number;
    homeUser: UserMinimalDto;
    awayUser: UserMinimalDto;
    appointment: Date;
    author: UserMinimalDto;
    streams: ChannelDto[];
    created: Date;
}

export interface ScheduleCreationDto {
    author: number;
    opponent: number;
    appointment: string;
}

export interface UserMinimalDto {
    id: number;
    username: string;
}

export interface UserChangeDto {
    username: string;
    country: number;
    about: string;
    email: string;
}

export interface PasswordChangeDto {
    currentPassword: string;
    newPassword: string;
}

export interface PasswordResetDto {
    password: string;
    resetKey: string;
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
    created: string;
    modified: string;
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
    techWin: boolean;
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
    tournament: TournamentDetailDto;
    voided: boolean;
    replayExists: boolean;
    replayQuantity?: number;
    playoffRoundLocalized: string;
    created: string;
    reportedAt: string;
}

export interface PlayoffGameDto {
    id: number;
    homeUser?: User;
    awayUser?: User;
    techWin: boolean;
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
    tournament: TournamentDetailDto;
    replayExists: boolean;
    bets: PlayoffTreeBetDto[];
    playoffRoundLocalized: string;
    created: string;
    reportedAt: string;
}

export interface GroupWithGamesDto {
    id: number;
    label: GroupLabel;
    tournament: TournamentDetailDto;
    standings: StandingDto[];
    games: GameMinimalDto[];
}

export interface GameMinimalDto {
    id: number;
    scoreHome: number;
    scoreAway: number;
    techWin: boolean;
    created: string;
    reportedAt: string;
    modified: string;
    homeUser: UserMinimalDto;
    awayUser: UserMinimalDto;
    replayExists: Boolean;
}

export interface GameMicroDto {
    id: number;
    homeUsername: string;
    awayUsername: string;
    homeScore: number;
    awayScore: number;
    reportedAt: string;
}

export interface StandingDto {
    id: number;
    points: number;
    games: number;
    gameRatio: number;
    roundRatio: number;
    user: UserMinimalDto;
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

export interface TournamentDetailDto {
    id: number;
    status: TournamentStatus;
    review: String;
    maxRounds: number;
    numOfGroupAdvancing: number;
    threeWay: Boolean;
    created: Date;
    bronzeWinner: UserMinimalDto;
    silverWinner: UserMinimalDto;
    goldWinner: UserMinimalDto;
    moderators: UserMinimalDto[];
}

export interface TournamentDto {
    id: number;
    year: number;
    goldWinner: UserMinimalDto;
    silverWinner: UserMinimalDto;
    bronzeWinner: UserMinimalDto;
    moderators: UserMinimalDto[];
}

export interface TournamentUpdateDto {
    status?: TournamentStatus;
    review?: String;
    maxRounds?: number;
    bronzeWinner?: number;
    silverWinner?: number;
    goldWinner?: number;
    moderators?: number[];
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

export type MessageNewsType = "REPORT" | "RATING" | "COMMENT" | "VOIDED" | "STREAM" | "TWITCH_MESSAGE" | "DISCORD_MESSAGE";

export interface MessageCreationDto {
    body: string;
    category: MessageCategory;
    recipients: number[];
}

export interface MessageDto {
    id: number;
    created: string;
    body: string;
    recipients: UserMinimalDto[];
    author: UserMinimalDto;
    newsType?: MessageNewsType;
    category: MessageCategory;
}

export interface ServerError extends HttpErrorResponse {
    error: {
        message: string;
        path: string;
        status: number;
        timestamp: string;
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

export interface TetrisDto {
    id?: number;
    highscore: number;
    user: UserMinimalDto;
    guestname: String;
    created: string;
}

export interface MapDto {
    mapPath: string;
    texture: string;
    game: GameMinimalDto;
}
