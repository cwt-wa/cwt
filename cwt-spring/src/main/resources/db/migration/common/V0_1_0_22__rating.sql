alter table rating
    add column modified timestamp default now() not null;

drop table rating_result;

create sequence bet_seq increment by 1;
