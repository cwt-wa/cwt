create temporary table tmp_message_recipient
(
    message_id bigint not null,
    user_id    bigint not null
);

insert into tmp_message_recipient (message_id, user_id)
select user_id, message_id
from message_recipient;

drop table message_recipient;

create table message_recipient
(
    user_id    bigint not null,
    message_id bigint not null,
    foreign key (user_id) references "user" (id),
    foreign key (message_id) references message (id)
);

create index idx_msg_recipient on message_recipient (user_id);
create index idx_msg_message_recipient on message_recipient (message_id);

insert into message_recipient (message_id, user_id)
select message_id, user_id
from tmp_message_recipient;
