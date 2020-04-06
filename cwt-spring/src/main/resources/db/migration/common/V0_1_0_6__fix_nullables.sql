alter table tournament drop column open;
alter table tournament drop column host_id;
alter table tournament alter column status set default 'OPEN';
alter table tournament alter column created set default now();

alter table "user" alter column activated set default true;
