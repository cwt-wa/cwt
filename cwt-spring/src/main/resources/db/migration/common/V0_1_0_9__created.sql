update application set created=now() where created is null;
alter table application alter column created set default now();
alter table application alter column created set not null;

update comment set created=now() where created is null;
alter table comment alter column created set default now();
alter table comment alter column created set not null;

update game set created=now() where created is null;
alter table game alter column created set default now();
alter table game alter column created set not null;

update message set created=now() where created is null;
alter table message alter column created set default now();
alter table message alter column created set not null;

update page set created=now() where created is null;
alter table page alter column created set default now();
alter table page alter column created set not null;

update tournament set created=now() where created is null;
alter table tournament alter column created set default now();
alter table tournament alter column created set not null;

update "user" set created=now() where created is null;
alter table "user" alter column created set default now();
alter table "user" alter column created set not null;

update comment set modified=now() where modified is null;
alter table comment alter column modified set default now();
alter table comment alter column modified set not null;

update configuration set modified=now() where modified is null;
alter table configuration alter column modified set default now();
alter table configuration alter column modified set not null;

update game set modified=now() where modified is null;
alter table game alter column modified set default now();
alter table game alter column modified set not null;

update page set modified=now() where modified is null;
alter table page alter column modified set default now();
alter table page alter column modified set not null;

update "user" set modified=now() where modified is null;
alter table "user" alter column modified set default now();
alter table "user" alter column modified set not null;
