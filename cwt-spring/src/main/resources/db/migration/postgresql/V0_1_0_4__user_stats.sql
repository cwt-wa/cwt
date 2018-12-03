create materialized view user_stats AS
  (select u.id as user_id, coalesce(gold.gold, 0) * 3 + coalesce(silver.silver, 0) * 2 +
                            coalesce(bronze.bronze, 0) as trophy_points, participations.participations, timeline(u.id)
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
   order by trophy_points desc, participations desc, username desc);

alter table user_ add column user_stats_id bigint;

update user_ set user_stats_id = id;
