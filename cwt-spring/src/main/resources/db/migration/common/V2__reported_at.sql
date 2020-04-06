alter table game
    add column reported_at timestamp;

update game g
set reported_at = created;
