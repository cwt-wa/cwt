-- Application

alter table application alter column tournament_id set not null;
alter table application alter column applicant_id set not null;

create index fk_application_tournament on application (tournament_id);
create index fk_application_applicant on application (applicant_id);

-- Comment

alter table comment alter column author_id set not null;
alter table comment alter column game_id set not null;

create index fk_comment_author on comment (author_id);
create index fk_comment_game on comment (game_id);

-- Configuration

create index fk_configuration_author on configuration (author_id);

-- Game

create unique index fk_game_replay on game (replay_id);
create unique index fk_game_playoff on game (playoff_id);
create index fk_game_group on game (group_id);

alter table game alter column tournament_id set not null;
create index fk_game_tournament on game (tournament_id);

-- Group

alter table "group" alter column tournament_id set not null;
create index fk_group_tournament on "group" (tournament_id);

-- GroupStanding

alter table group_standing alter column user_id set not null;
create index fk_standing_user on group_standing (user_id);

alter table group_standing alter column points set not null;
alter table group_standing alter column points set default 0;
alter table group_standing alter column games set not null;
alter table group_standing alter column games set default 0;
alter table group_standing alter column game_ratio set not null;
alter table group_standing alter column game_ratio set default 0;
alter table group_standing alter column round_ratio set not null;
alter table group_standing alter column round_ratio set default 0;

-- Message

alter table message alter column author_id set not null;
create index fk_message_author on message (author_id);

alter table message alter column body set not null;
alter table message alter column category set not null;
alter table message alter column category set default 'SHOUTBOX';

-- Rating

alter table rating alter column user_id set not null;
create index fk_rating_user on rating (user_id);

alter table rating alter column game_id set not null;
create index fk_rating_game on rating (game_id);

-- RatingResult

alter table rating_result alter column game_id set not null;
create unique index fk_ratingresult_game on rating_result (game_id);

-- Replay

alter table replay alter column file set not null;
alter table replay alter column media_type set not null;
alter table replay alter column extension set not null;

-- Tournament

create index fk_tournament_bronze_id on tournament (bronze_winner_id);
create index fk_tournament_gold_id on tournament (gold_winner_id);
create index fk_tournament_silver_id on tournament (silver_winner_id);

--
-- General
--

alter table comment drop column deleted;
alter table game drop column downloads;
drop table page;
