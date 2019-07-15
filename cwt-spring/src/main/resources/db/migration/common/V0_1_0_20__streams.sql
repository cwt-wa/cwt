create table channel
(
    id                text      not null,
    user_id           bigint    not null,
    display_name      text,
    type              text,
    profile_image_url text,
    view_count        bigint,
    broadcaster_type  text,
    offline_image_url text,
    login             text,
    description       text,
    modified          timestamp not null default now(),
    created           timestamp not null default now(),
    primary key (id),
    foreign key (user_id) references "user" (id)
);

create unique index uidx_fk_channel_user on channel (user_id);

create table stream
(
    id            bigint not null,
    user_id       text,
    channel_id    text not null,
    user_name     text,
    title         text,
    description   text,
    created_at    text,
    published_at  text,
    url           text,
    thumbnail_url text,
    viewable      text,
    view_count    bigint,
    language      text,
    type          text,
    duration      text,
    primary key (id),
    foreign key (channel_id) references channel (id)
);

create index idx_fk_stream_channel on stream (channel_id);

insert into configuration (key)
VALUES ('PAGINATION_CURSOR_VIDEOS_TWITCH_API');

comment on table channel is 'A channel on CWT resembles a user on Twitch.';
comment on table stream is 'A stream on CWT resembles a video (not a stream!) on Twitch.';

comment on column channel.id is 'Resembles the user ID from the Twitch API.';
comment on column stream.user_id is 'Resembles the user ID from the Twitch API and is the channel ID in CWT.';


