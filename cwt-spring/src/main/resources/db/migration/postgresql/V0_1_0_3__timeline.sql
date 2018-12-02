CREATE OR REPLACE FUNCTION timeline(bigint)
  RETURNS TABLE(timeline text)
ROWS 1 LANGUAGE SQL AS $$

select string_agg(cast(round as text), '''') as timeline
from (select TO_CHAR(t.CREATED, ''yyyy'')                                                                                    as year,
             5                                                                                                               as max_round,
             least(greatest((case coalesce(g.g_id, 0) when 0 then 0 else 1 end), max(po.round) + 1), 5 - 1) + (case
                                                                                                                 when t.GOLD_WINNER_ID = $1
                                                                                                                         then 3
                                                                                                                 when t.SILVER_WINNER_ID = $1
                                                                                                                         then 2
                                                                                                                 when t.BRONZE_WINNER_ID = $1
                                                                                                                         then 1
                                                                                                                 else 0 end) as round
      from TOURNAMENT t
             left join (select g.id as g_id, s.id as s_id, g.TOURNAMENT_ID as t_id, s.USER_ID as u_id
                        from "group" g
                               join GROUP_STANDING s on g.ID = s.GROUP_ID) g on g.t_id = t.id and g.u_id = $1
             left join (select TOURNAMENT_ID, HOME_USER_ID, AWAY_USER_ID, max(round) as round
                        from GAME g
                               join PLAYOFF_GAME po1 on g.PLAYOFF_ID = po1.ID
                        group by TOURNAMENT_ID, HOME_USER_ID, AWAY_USER_ID) po
               on po.TOURNAMENT_ID = t.id and (po.HOME_USER_ID = $1 or po.AWAY_USER_ID = $1)
      group by year, g.g_id, t.GOLD_WINNER_ID, t.SILVER_WINNER_ID, t.BRONZE_WINNER_ID
      order by year asc) as x;
$$;
