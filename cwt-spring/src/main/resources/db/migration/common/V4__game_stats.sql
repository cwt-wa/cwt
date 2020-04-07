create table game_stats
(
    game_id    bigint    not null,
    data       text      not null,
    started_at timestamp not null,
    created    timestamp not null default now(),
    primary key (game_id),
    foreign key (game_id) references game (id)
);

create unique index uidx_game_stats on game_stats (game_id, started_at);
