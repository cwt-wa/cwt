create table bet
(
        id          bigint  not null,
    user_id     bigint  not null,
    game_id     bigint  not null,
    bet_on_home boolean not null,
    foreign key (user_id) references "user" (id),
    foreign key (game_id) references game (id),
    primary key (id)
);

create unique index uidx_bet_user_game on bet (user_id, game_id);
create index fk_bet_user on bet (user_id);
create index fk_bet_game on bet (game_id);
