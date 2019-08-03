create table schedule
(
    id           bigint    not null,
    away_user_id bigint    not null,
    home_user_id bigint    not null,
    appointment  timestamp not null,
    author_id    bigint    not null,
    created      timestamp not null,
    primary key (id),
    foreign key (home_user_id) references "user" (id),
    foreign key (away_user_id) references "user" (id),
    foreign key (author_id) references "user" (id)
);

create index idx_schedule_home on schedule (home_user_id);
create index idx_schedule_away on schedule (away_user_id);
create index idx_schedule_author on schedule (author_id);

create table schedule_channel
(
    channel_id  text   not null,
    schedule_id bigint not null,
    foreign key (channel_id) references channel (id),
    foreign key (schedule_id) references schedule (id)
);

create index idx_schedule_channel_channel on schedule_channel (channel_id);
create index idx_schedule_channel_schedule on schedule_channel (schedule_id);
create unique index uidx_schedule_channel on schedule_channel (channel_id, schedule_id);
