create table ranking
(
    user_id bigint not null,
    last_tournament_id bigint not null,
    last_place int not null default 0,
    last_diff int not null default 0,
    points decimal(9, 5) not null default 0,
    gold int not null default 0,
    silver int not null default 0,
    bronze int not null default 0,
    won int not null default 0,
    lost int not null default 0,
    won_ratio float not null default 0,
    participations int not null default 0,
    played int not null default 0,
    primary key (user_id),
    foreign key (user_id) references "user" (id),
    foreign key (last_tournament_id) references tournament (id)
);

create index idx_ranking_user on ranking (user_id);
create index idx_ranking_tournament on ranking (last_tournament_id);

