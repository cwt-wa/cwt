alter sequence bet_id_seq increment by 1;
alter sequence game_id_seq increment by 1;
alter sequence game_stats_id_seq increment by 1;
alter sequence playoff_game_id_seq increment by 1;
alter sequence rating_id_seq increment by 1;
alter sequence replay_id_seq increment by 1;
alter sequence group_id_seq increment by 1;
alter sequence message_id_seq increment by 1;
alter sequence schedule_id_seq increment by 1;
alter sequence tetris_id_seq increment by 1;
alter sequence tournament_id_seq increment by 1;
alter sequence country_id_seq increment by 1;
alter sequence photo_id_seq increment by 1;
alter sequence user_id_seq increment by 1;
alter sequence application_id_seq increment by 1;
alter sequence comment_id_seq increment by 1;
alter sequence group_standing_id_seq increment by 1;

drop sequence if exists restores_id_seq;

