alter table stream add column game_id bigint;

alter table stream add constraint fk_stream_games foreign key (game_id) references game;
create index idx_fk_stream_games on stream (game_id);

