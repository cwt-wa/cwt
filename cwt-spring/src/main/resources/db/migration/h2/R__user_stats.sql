create or replace view user_stats as (
  select u.id                                                                                                                                                                                                                     as user_id,
         coalesce(gold.gold, 0) * 20 + coalesce(silver.silver, 0) * 5 +
         coalesce(bronze.bronze, 0)                                                                                                                                                                                               as trophy_points,
         coalesce(participations.participations, 0)                                                                                                                                                                               as participations,
         '[1,2002,5,0],[2,2003,5,0],[3,2004,5,0],[4,2005,5,0],[5,2006,5,0],[6,2007,5,3],[7,2008,5,2],[8,2009,5,5],[9,2010,5,3],[10,2011,5,6],[11,2012,5,2],[12,2013,5,6],[13,2014,5,0],[14,2015,5,2],[15,2016,5,2],[16,2018,5,0]' as timeline
  from "user" u
         left join (select count(GOLD_WINNER_ID) as gold, GOLD_WINNER_ID from TOURNAMENT group by GOLD_WINNER_ID) gold
                   on gold.GOLD_WINNER_ID = u.id
         left join (select count(SILVER_WINNER_ID) as silver, SILVER_WINNER_ID
                    from TOURNAMENT
                    group by SILVER_WINNER_ID) silver on silver.SILVER_WINNER_ID = u.id
         left join (select count(BRONZE_WINNER_ID) as bronze, BRONZE_WINNER_ID
                    from TOURNAMENT
                    group by BRONZE_WINNER_ID) bronze on bronze.BRONZE_WINNER_ID = u.id
         left join (select count(s.USER_ID) as participations, s.USER_ID
                    from "group" g
                           join GROUP_STANDING s on g.ID = s.GROUP_ID
                    group by s.USER_ID) participations on participations.USER_ID = u.ID
  order by trophy_points desc, participations desc, username desc
);
