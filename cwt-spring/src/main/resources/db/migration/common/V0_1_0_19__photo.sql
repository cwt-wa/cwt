create table photo
(
    id         bigint not null,
    extension  text,
    file       bytea,
    media_type text,
    primary key (id)
);

alter table "user"
    add column photo_id bigint;

alter table "user"
    add foreign key (photo_id) references photo (id);

create unique index uidx_fk_user_photo on "user" (photo_id);
