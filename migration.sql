insert into "user" (id, activated, activation_key, created, email, modified, password_hash, password_legacy_hash, reset_date, reset_key, username, about,
                    country_id, photo_id)
select p.id,
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
       null -- todo photo
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

INSERT INTO authority (id, name)
VALUES (1, 'ROLE_USER');
INSERT INTO authority (id, name)
VALUES (2, 'ROLE_ADMIN');

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
       null, -- todo replay
       nullif(reporter_id, 0),
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

insert into message (id, body, category, created, author_id, news_type)
select id,
       message,
       'SHOUTBOX',
       created,
       user_id,
       null
from infoboards
where category = 1;

-- traces

insert into rating (id, type, game_id, user_id, modified)
select nextval('rating_seq'),
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

insert into bet (id, user_id, game_id, bet_on_home, modified)
select nextval('bet_seq'),
       user_id,
       "on",
       case when additional = 'bet_h' then true else false end,
       created
from traces
where controller = 'Bet';


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

-- todo create streams and channels by using CWT REST API
-- todo set sequences
-- todo drop tables
-- todo BBCode (configuration.rules, configuration.news, comments etc.) to markdown (or no formatting) GH-155
