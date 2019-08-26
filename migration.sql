insert into "user" (id, activated, activation_key, created, email, modified, password_hash, password_legacy_hash, reset_date, reset_key, username, about,
                    country_id, photo_id)
select u.id,
       true,
       null,
       created,
       nullif(p.email, ''),
       p.modified,
       null,
       password,
       null,
       null,
       username,
       p.about,
       coalesce((select id from country c where c.name = p.country), 1),
       null
from users u
         join profiles p on u.id = p.user_id;

insert into tournament (id, created, review, status, bronze_winner_id, gold_winner_id, silver_winner_id, max_rounds, three_way)
select id,
       concat(year, '-01-01 00:00:00')::timestamp,
       review,
       status,
       nullif(bronze_id, 0),
       gold_id,
       silver_id,
       5,
       false
from tournaments;

insert into application (id, created, revoked, applicant_id, tournament_id)
select id, created, false as revoked, user_id as applicant_id, 4 as tournament_id
from applications;

---

INSERT INTO user_authority (user_id, authority_id)
select id, 1
from "user";

INSERT INTO user_authority (user_id, authority_id)
VALUES (2, 2); -- Zemke
INSERT INTO user_authority (user_id, authority_id)
VALUES (10, 2); -- Kayz

insert into playoff_game (id, round, spot)
select id, step, spot
from playoffs;

insert into "group" (id, label, tournament_id)
select id, replace(replace(label::text, '{', ''), '}', ''), tournament_id
from "groups";

insert into game (id, created, modified, score_away, score_home, tech_win, away_user_id, group_id, home_user_id, playoff_id, replay_id, reporter_id,
                  tournament_id, voided)
select id,
       created,
       created,
       score_a,
       score_h,
       techwin,
       away_id,
       nullif(group_id, 0),
       home_id,
       nullif(playoff_id, 0),
       null,
       coalesce(nullif(reporter_id, 0), 1),
       tournament_id,
       false
from games;

insert into group_standing (id, game_ratio, games, points, round_ratio, group_id, user_id)
select id,
       game_ratio,
       games,
       points,
       round_ratio,
       group_id,
       user_id
from standings;

insert into comment (id, body, created, modified, author_id, game_id)
select id, message, created, modified, user_id, game_id
from comments;

create sequence tmp_message_seq;

insert into message (id, body, category, created, author_id, news_type)
select nextval('tmp_message_seq'),
       message,
       'SHOUTBOX',
       created,
       user_id,
       null
from infoboards
where category = 1;

drop sequence tmp_message_seq;

-- traces

create sequence tmp_rating_seq increment by 1 start with 1;

insert into rating (id, type, game_id, user_id, modified)
select nextval('tmp_rating_seq'),
       (case
            when additional = 'likes' then 'LIKE'
            when additional = 'dislikes' then 'DISLIKE'
            when additional = 'lightside' then 'LIGHTSIDE'
            when additional = 'darkside' then 'DARKSIDE'
           end),
       "on",
       user_id,
       created
from traces
where controller = 'Rating';

drop sequence tmp_rating_seq;

create sequence tmp_bet_seq increment by 1 start with 1;

insert into bet (id, user_id, game_id, bet_on_home, modified)
select nextval('tmp_bet_seq'),
       user_id,
       "on",
       case when additional = 'bet_h' then true else false end,
       created
from traces
where controller = 'Bet';

drop sequence tmp_bet_seq;

-- configuration

update configuration
set value     = encode(r.text, 'escape'),
    modified  = r.modified,
    author_id = r.user_id
from (select * from rules) as r
where key = 'RULES';

update configuration
set value     = encode(n.text, 'escape'),
    modified  = n.modified,
    author_id = n.user_id
from (select * from news) as n
where key = 'NEWS';

-- Sequences

create sequence bet_id_seq increment by 50;
create sequence channel_id_seq increment by 50;
create sequence country_id_seq increment by 50;
create sequence message_id_seq increment by 50;
create sequence photo_id_seq increment by 50;
create sequence replay_id_seq increment by 50;

alter sequence applications_id_seq rename to application_id_seq;
alter sequence comments_id_seq rename to comment_id_seq;
alter sequence games_id_seq rename to game_id_seq;
alter sequence groups_id_seq rename to group_id_seq;
alter sequence ratings_id_seq rename to rating_id_seq;
alter sequence schedules_id_seq rename to schedule_id_seq;
alter sequence streams_id_seq rename to stream_id_seq;
alter sequence tournaments_id_seq rename to tournament_id_seq;
alter sequence users_id_seq rename to user_id_seq;
alter sequence playoffs_id_seq rename to playoff_game_id_seq;
alter sequence standings_id_seq rename to group_standing_id_seq;

alter sequence bet_id_seq increment by 50;
alter sequence channel_id_seq increment by 50;
alter sequence country_id_seq increment by 50;
alter sequence group_standing_id_seq increment by 50;
alter sequence message_id_seq increment by 50;
alter sequence photo_id_seq increment by 50;
alter sequence playoff_game_id_seq increment by 50;
alter sequence replay_id_seq increment by 50;
alter sequence application_id_seq increment by 50;
alter sequence comment_id_seq increment by 50;
alter sequence game_id_seq increment by 50;
alter sequence group_id_seq increment by 50;
alter sequence rating_id_seq increment by 50;
alter sequence schedule_id_seq increment by 50;
alter sequence stream_id_seq increment by 50;
alter sequence tournament_id_seq increment by 50;
alter sequence user_id_seq increment by 50;

alter table application alter column id set default nextval('application_id_seq');
alter table bet alter column id set default nextval('bet_id_seq');
alter table channel alter column id set default nextval('channel_id_seq');
alter table comment alter column id set default nextval('comment_id_seq');
alter table country alter column id set default nextval('country_id_seq');
alter table game alter column id set default nextval('game_id_seq');
alter table "group" alter column id set default nextval('group_id_seq');
alter table group_standing alter column id set default nextval('group_standing_id_seq');
alter table message alter column id set default nextval('message_id_seq');
alter table photo alter column id set default nextval('photo_id_seq');
alter table playoff_game alter column id set default nextval('playoff_game_id_seq');
alter table rating alter column id set default nextval('rating_id_seq');
alter table replay alter column id set default nextval('replay_id_seq');
alter table schedule alter column id set default nextval('schedule_id_seq');
alter table stream alter column id set default nextval('stream_id_seq');
alter table tournament alter column id set default nextval('tournament_id_seq');
alter table "user" alter column id set default nextval('user_id_seq');

select setval('user_id_seq', (select max(id) + 1 from "user"), false); -- Since IDs greater than that were taken by spam accounts.
select setval('rating_id_seq', (select max(id) + 1 from rating), false); -- Sequence did not exist in CWT 5.
select setval('bet_id_seq', (select max(id) + 1 from bet), false); -- Sequence did not exist in CWT 5.
select setval('country_id_seq', (select max(id) + 1 from country), false); -- Sequence did not exist in CWT 5.
select setval('message_id_seq', (select max(id) + 1 from message), false); -- Sequence did not exist in CWT 5.


-- todo binaries for game replay and user photo
-- todo create streams and channels by using CWT REST API
-- todo drop tables and sequences
-- todo BBCode (configuration.rules, configuration.news, comments etc.) to markdown (or no formatting) GH-155
