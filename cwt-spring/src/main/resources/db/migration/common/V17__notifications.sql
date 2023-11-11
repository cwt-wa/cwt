create table notification
(
    id bigint not null,
    user_id bigint not null,
    setting int not null,
    subscription text not null,
    subscription_created timestamp not null,
    user_agent text not null,
    modified timestamp not null default now(),
    created timestamp not null default now(),
    primary key (id),
    foreign key (user_id) references "user" (id),
);

create index idx_notif_user on notification (user_id);

