create table game_stats
(
    game_id bigint    not null,
    data    json      not null,
    created timestamp not null default now(),
    primary key (game_id),
    foreign key (game_id) references game (id)
);
