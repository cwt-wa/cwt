insert into "user" (id, activated, activation_key, created, email, modified, password_hash, password_legacy_hash, reset_date, reset_key, username, about,
                    country_id, photo_id)
select p.id,
       true,
       null,
       created,
       p.email,
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
       concat(year, '-01-01 00:00:00'),
       review,
       status,
       bronze_id,
       gold_id,
       silver_id,
       5,
       false
from tournaments;

insert into application (id, created, revoked, applicant_id, tournament_id)
select id, created, false as revoked, user_id as applicant_id, 4 as tournament_id
from applications;

---

-- TODO Set sequence.
INSERT INTO authority (id, name)
VALUES (1, 'ROLE_USER');
INSERT INTO authority (id, name)
VALUES (1, 'ROLE_ADMIN');

-- TODO Set sequence.
INSERT INTO user_authority (user_id, authority_id)
select id, 1
from "user";

INSERT INTO user_authority (user_id, authority_id)
VALUES (2, 1); -- Zemke
INSERT INTO user_authority (user_id, authority_id)
VALUES (10, 1); -- Kayz

insert into comment (id, body, created, modified, author_id, game_id)
select id, message, created, modified, user_id, game_id
from comments;


insert into game (id, created, modified, score_away, score_home, tech_win, away_user_id, group_id, home_user_id, playoff_id, replay_id, reporter_id,
                  tournament_id, voided)
select id,
       created,
       created,
       score_a,
       score_h,
       techwin,
       away_id,
       group_id,
       home_id,
       playoff_id,
       null, -- todo replay
       reporter_id,
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

insert into "group" (id, label, tournament_id)
select id, label, tournament_id
from "groups";

insert into message (id, body, category, created, author_id, news_type)
select id,
       message,
       (case when category = 3 then 'NEWS' when category = 2 then 'PRIVATE' else 'SHOUTBOX' end),
       created,
       user_id,
       null -- todo news types can be REPORT, RATING, COMMENT
from infoboards;

-- todo message_recipient

-- todo bet, configuration, rating
-- todo drop tables
-- todo create streams and channels by using CWT REST API
