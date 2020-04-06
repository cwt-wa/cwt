update game
set reporter_id=1
where reporter_id is null;

alter table game
    alter column reporter_id set not null;
