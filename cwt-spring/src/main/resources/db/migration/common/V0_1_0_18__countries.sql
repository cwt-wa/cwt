create table country
(
    id      bigint    not null,
    flag    text      not null,
    name    text      not null,
    created timestamp not null default now(),
    primary key (id)
);

create unique index uidx_country_name on country (name);

insert into country (id, flag, name)
select row_number() over () as id, concat(flag, '.png'), initcap(replace(flag, '_', ' '))
from unnest(string_to_array(
        'unknown,afghanistan,andorra,argentina,australia,austria,azerbaijan,belarus,belgium,bolivia,bosnia,brazil,bulgaria,canada,chile,china,colombia,costa_rica,cote_divoire,croatia,cyprus,czech_republic,denmark,ecuador,egypt,estonia,finland,france,georgia,germany,greece,guatemala,honduras,hungary,iceland,india,iran,iraq,ireland,israel,italy,jamaica,kazakhstan,kyrgyzstan,latvia,lebanon,lithuania,luxembourg,macedonia,malaysia,malta,mexico,moldova,netherlands,north_korea,norway,poland,portugal,puerto_rico,romania,russia,serbia,slovakia,slovenia,south_afrika,south_korea,spain,sweden,switzerland,turkey,ukraine,united_kingdom,united_states,uruguay,venezuela',
        ',')) as flag;

-- User table

alter table "user"
    add column country_id bigint not null default 1;

alter table "user"
    add foreign key (country_id) references country (id);

update "user"
set country_id = coalesce((select c.id from country c where country = c.name), 1);

alter table "user"
    drop column country;
