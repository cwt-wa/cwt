create table user_stats (
  user_id        bigint not null,
  trophy_points  int    not null,
  participations int    not null,
  timeline       text   not null,
  constraint user_stats__pkey
  primary key (user_id),
);

insert into user_stats (user_id, trophy_points, participations, timeline)
select u.id                                                                                        as user_id,
       coalesce(gold.gold, 0) * 3 + coalesce(silver.silver, 0) * 2 +
       coalesce(bronze.bronze, 0)                                                                  as trophy_points,
       participations.participations,
       (select string_agg(cast(t.something as varchar), '')
        from (select least(cast(TO_CHAR(created, 'Y') as int), 7) as something from tournament) t) as timeline
from USER_ u
       left join (select count(GOLD_WINNER_ID) as gold, GOLD_WINNER_ID from TOURNAMENT group by GOLD_WINNER_ID) gold
         on gold.GOLD_WINNER_ID = u.id
       left join (select count(SILVER_WINNER_ID) as silver, SILVER_WINNER_ID
                  from TOURNAMENT
                  group by SILVER_WINNER_ID) silver on silver.SILVER_WINNER_ID = u.id
       left join (select count(BRONZE_WINNER_ID) as bronze, BRONZE_WINNER_ID
                  from TOURNAMENT
                  group by BRONZE_WINNER_ID) bronze on bronze.BRONZE_WINNER_ID = u.id
       join (select count(s.USER_ID) as participations, s.USER_ID
             from "group" g
                    join GROUP_STANDING s on g.ID = s.GROUP_ID
             group by s.USER_ID) participations on participations.USER_ID = u.ID
order by trophy_points desc, participations desc, username desc;

alter table user_
  add column user_stats_id bigint;

update user_
set user_stats_id = id;

alter table user_
  add constraint fk_user_stats foreign key (user_stats_id) references user_stats;

