create table ranking
(
    id bigint not null,
    user_id bigint not null,
    last_tournament_id bigint not null,
    lastPlace int not null default 0,
    lastDiff int not null default 0,
    points decimal(8, 5) not null default 0,
    gold int not null default 0,
    silver int not null default 0,
    bronze int not null default 0,
    won int not null default 0,
    lost int not null default 0,
    won_ratio float not null default 0,
    participations int not null default 0,
    played int not null default 0,
    primary key (id),
    foreign key (user_id) references "user" (id),
    foreign key (last_tournament_id) references tournament (id)
);

create sequence ranking_id_seq increment by 1;
create index idx_ranking_user on ranking (user_id);
create index idx_ranking_tournament on ranking (last_tournament_id);

