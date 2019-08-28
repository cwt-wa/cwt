create sequence tetris_id_seq increment by 50;

create table tetris (
    id bigint not null default nextval('tetris_id_seq'),
    user_id bigint,
    guestname text,
    highscore int not null, 
    foreign key (user_id) references "user" (id),
    created timestamp not null default now(),
    primary key (id)
);

create index idx_tetris_user on tetris (user_id);

