# CWT 5 to CWT 6 Migration Guide

## Database

The database migration is crucial. Not only has the schema changed, also is the ORDBMS changed from MySQL to PostgreSQL.

### Remove spam accounts

In order for the migration to be more efficient, the thousands of spam accounts should be deleted in production before starting the migration.

The following SQL identifies inactive users:

```sql
    select *
    from users
    where id not in (
        (select home_id as id
         from games
         union
         select away_id as id
         from games
         union
         select user_id as id
         from infoboards
         union
         select user_id as id
         from infoboards
         union
         select user_id as id
         from comments
         union
         select home_id as id
         from schedules
         union
         select away_id as id
         from schedules
         union
         select user_id as id
         from standings
         union
         select moderator_id as id
         from tournaments_moderators
         union
         select user_id as id
         from traces
        )
    ) and id not in (19, 51, 59, 60, 63, 162, 164, 171, 181, 182, 186, 231, 232, 234, 246, 250, 254, 268, 268, 630, 631, 632, 633, 634, 637)
```

Basically users not involved in any particular action on the website except having registered. There are exceptions to this of users matching that criteria but I know they're actual users of the community. So they're excluded.
There's a database view of that query called `inactive_users`.

So, now these users could be deleted. Hopefully it won't hurt the referential integrity. I wouldn't notice because there actually is no such thing in CWT 5.

The following statements caused deletion. The temporary table was necessary because the View by which user IDs are retrieved references the table from which to delete which is a conflicting operation.

```sql
delete from profiles where user_id in (select id from inactive_users);

create temporary table temp_inactive_users(    id bigint not null
);

insert into temp_inactive_users (select id from inactive_users);
delete from users where id in (select id from temp_inactive_users);
```

```
sql> delete from profiles where user_id in (select id from inactive_users)
[2019-08-12 20:54:19] 7300 rows affected in 42 s 2 ms
sql> create temporary table temp_inactive_users(    id bigint not null
     )
[2019-08-12 20:54:20] completed in 115 ms
sql> insert into temp_inactive_users (select id from inactive_users)
[2019-08-12 20:55:03] 7301 rows affected in 43 s 82 ms
sql> delete from users where id in (select id from temp_inactive_users)
[2019-08-12 20:55:15] 7301 rows affected in 11 s 817 ms
[2019-08-12 20:55:35] transaction committed: @spam users [CWT [PROD]]
```

Remaining number of users is 256.

Additionally user Jakk0 has been merged into Jakka, MIGHTYtaner into tanerr and Afinaaa into Afina using a feature that was implemented in [#90](https://github.com/Zemke/cwt/issues/90). 

User “NouS” has been reinstated from the inactive users because he appears tobe an actual user who is also registered on TUS and he is in the `applications` table which is not taken into account by the `inactive_users` view.

Other entries in the `applications` table whose user relation had been removed have been removed 

### MySQL to PostgreSQL

The migration from MySQL to PostgreSQL is as easy as executing a command-line command. [`pgloader`](https://pgloader.io) is the great help here.

```
pgloader mysql://root:cwt@localhost/db10838396-cwt pgsql://postgres:postgres@localhost/migration
```

Once that's done and the issues logged by `pgloader` have been resolved, one can proceed by running the CWT 6 Flyway migration. This thankfully almost entirely works on top of the existing CWT 5 schema without conflicting. You only must not run the `V0_1_0_23__serial.sql` migration.

The Flyway configuration could look like this:

```properties
flyway.url=jdbc:postgresql://127.0.0.1:5432/migration
flyway.schemas=db10838396-cwt
flyway.user=postgres
flyway.password=postgres
flyway.locations=filesystem:/Users/Zemke/Code/cwt-evolution/cwt-spring/src/main/resources/db/migration/common,filesystem:/Users/Zemke/Code/cwt-evolution/cwt-spring/src/main/resources/db/migration/feat
```

Run the migration and create the `flyway_schema_history` table using the `baselineOnMigrate` flag.

```
flyway migrate -configFiles=/Users/Zemke/Desktop/migration.properties -baselineOnMigrate=true
```

Then delete rows from the table to signal an unmigrated database:

```sql
delete from flyway_schema_history;
```

Then run the migration again without the flag.

```
flyway migrate -configFiles=/Users/Zemke/Desktop/migration.properties
```

Next up is writing some SQL to migrate data from the old tables into the new. This would be the actual migration.

#### Users without password

The following users won't be able to log in anymore because their passwords use a backward-incompatible encryption strategy. In CWT 5 there two password encryption strategies and CWT 6 will introduce a third one. Some users have been migrated to the seconds and they will be migrated to the third by CWT 6 but the oldest passwords will be neglected.

Email addresses have been added to the list because they could potentially reset their passwords using that email address (currently there's no password forgotten feature).

|username|email|
|---|---|
|domi||
|Alex13||
|Fonseca||
|Jakka|gi-jakka@hotmail.de|
|HaXeN||
|Jigsaw|lukexs@o2.pl|
|Antares|antares614@yahoo.com|
|NormalPRO|badkoff@mail.ru|
|SirGorash|sirgorash1@hotmail.com|
|Jule||
|Thurbo|thurbo@gmx.net|
|xDragonfirex||
|pandello||
|Pipinasso||

#### Troubleshooting

The following errors occur causing the affected tables to be empty.

```
2019-08-11T17:36:48.053000+01:00 ERROR Database error 23502: null value in column "modified" violates not-null constraint
DETAIL: Failing row contains (1, 2, 20, Quite a rocky start for both of us, many unnecessary mistakes :\, , 2012-10-06 22:45:55+00, null).
CONTEXT: COPY comments, line 1: "1	2	20	Quite a rocky start for both of us, many unnecessary mistakes :\\		2012-10-06 22:45:55	\N"
2019-08-11T17:36:48.233000+01:00 ERROR Database error 23502: null value in column "created" violates not-null constraint    
DETAIL: Failing row contains (76, 6, 0, 80, 2, 49, 1, 3, f, 48, null, 0).
CONTEXT: COPY games, line 76: "76	6	0	80	2	49	1	3	f	48	\N	0"
2019-08-11T17:36:48.655000+01:00 ERROR Database error 23502: null value in column "reported" violates not-null constraint
DETAIL: Failing row contains (12, 2, 6, 2, 49, 1, 3, f, Last Sixteen, null, 0, 2013-02-05 19:59:04+00).
CONTEXT: COPY restores, line 12: "12	2	6	2	49	1	3	f	Last Sixteen	\N	0	2013-02-05 19:59:04"
2019-08-11T17:36:48.789000+01:00 ERROR Database error 23502: null value in column "modified" violates not-null constraint
DETAIL: Failing row contains (1, 1, null, Germany, NNN, flzemke@gmail.com, zemke-, , http://www.facebook.com/florian.zemke, , https://twitter.com/FlorianZemke, Im thankful for the CWT community which has given me the..., t, f).
CONTEXT: COPY profiles, line 1: "1	1	\N	Germany	NNN	flzemke@gmail.com	zemke-		http://www.facebook.com/florian.zemke		https://twitter...."
2019-08-11T17:36:49.053000+01:00 ERROR Database error 23502: null value in column "created" violates not-null constraint
DETAIL: Failing row contains (3, domi, , 57c61a9c3593b1a512e6ff5e53b6897c, f, retired, 00000000100000000, 1, 2.00, 0, , null).
CONTEXT: COPY users, line 3: "3	domi		57c61a9c3593b1a512e6ff5e53b6897c	f	retired	00000000100000000	1	2.00	0		\N"
2019-08-11T17:36:49.708000+01:00 ERROR PostgreSQL Database error 42601: syntax error at or near "-"
QUERY: ALTER DATABASE migration SET search_path TO public, db10838396-cwt, "db10838396-cwt";
```

The issues are related to MySQL `datetime` fields which can be `null` although there are not-null constraints when ... well there's some MySQL weirdness in play.

These tables were fixed in production with sane defaults appropriate for each table individually. For instance and in no particular order:

```sql
update comments
set modified=created
where modified is null;

update profiles p
set modified=(select created from users where id = p.id)
where modified is null;

update restores
set reported = '2010-01-01 00:00:00'
where reported is null;

update users
set created = '2002-01-01 00:00:00'
where created is null;
``` 

The last error which is a syntax error has vanished during the process. So, no further issues expected.

There are some rows in `games` and `restores` with invalid dates containing invalid months and/or dates such as `2018-00-00 00:00:00`. Those have been fixed:

```sql
update restores set reported=replace(reported, '-00', '-01') where reported like '%-00%';
update games set created=replace(created, '-00', '-01') where created like '%-00%';
```

#### Various fixes in the production database

```sql
-- Re-add previously deleted user.
INSERT INTO users (id, username, password, md5password, admin, stage, timeline, participations, achievements, trophies, reset_key, created)
VALUES (6109, 'NouS', '47d260137b9efac923fb0cbfec9f3a99ce2f08ac', '', false, 'retired', '0000000000000000', 0, 0.00, 0, '', '2018-10-03 01:09:17');

insert into profiles (id, user_id, modified, country, clan, email, skype, icq, facebook, googlep, twitter, about, hideprofile, hideemail)
values (6109, 6109, now(), '', '', '', '', '', '', '', '', '', true, true);

-- Delete applicants whose user reference had been deleted.
delete
from applications
where user_id in (select * from (select a.user_id

                                 from applications a
                                          left join users u on u.id = a.user_id
                                 where u.username is null
                                 order by user_id desc) as x
);

-- The playoff–game relation is bi-directional and the game side sometimes had it mapped erroneously, but the playoff side was correct.
update games set playoff_id=13 where id=61;
update games set playoff_id=12 where id=59;
update games set playoff_id=14 where id=62;
update games set playoff_id=15 where id=63;
update games set playoff_id=16 where id=64;
update games set playoff_id=27 where id=943;
update games set playoff_id=28 where id=944;
update games set playoff_id=29 where id=945;

-- Group D in 2006 missing some data
update standings set user_id=39 where id =214;
update games set home_id=39 where home_id=214;
update games set away_id=39 where away_id=214;
insert into standings (group_id, user_id, points, games, game_ratio, round_ratio) values(52, 149, 0, 0, 0, 0);
insert into games (tournament_id, group_id, playoff_id, home_id, away_id, score_h, score_a, techwin, downloads, created, reporter_id)
    values (5, 52, 0, 155, 39, 2, 0, 0, 0, now(), 1);
update standings set games=(games + 1), game_ratio=(game_ratio - 1), round_ratio=(round_ratio - 2) where id = 214; -- DreamTrance
update standings set games=(games + 1), round_ratio=(round_ratio + 2), game_ratio=(game_ratio + 1), points=(points + 3) where id = 212; -- XWorm

-- There are some games in the vaccum CWT space.
delete from games where home_id = 0 and away_id = 0 and group_id = 0 and playoff_id = 0;

-- There is one profile missing.
insert into profiles (id, user_id, modified, country, clan, email, skype, icq, facebook, googlep, twitter, about, hideprofile, hideemail)
values (215, 215, now(), '', '', '', '', '', '', '', '', '', true, true);

-- Comment on non-existent game.
delete from comments where id = 1377;

-- Comments and ratings whose game reference had been removed.
delete from comments where game_id=1221 or game_id=1201 or game_id=1290;
delete from ratings where game_id=1221 or game_id=1201 or game_id=1290 or game_id=980;

-- Comments and ratings whose game reference had been removed.
delete from traces where id in (select * from (select t.id from traces t left join games g on g.id = t.on where g.id is null) as x);

-- Delete first of two bets.
delete from traces where id = 2831;

-- Apparently there had been two profiles for user with ID 226 (tanerrr).
delete from profiles where id = 288; -- taner
delete from profiles where id = 34; -- jakka
delete from profiles where id = 740; -- afina

-- United Kingdom had two mappings in the DB the one with underscore being the correct.
update profiles
set country='United Kingdom'
where country = 'United_Kingdom';
```

## Binary

When it became apparent that I can only get a free Postgres installation with database volume of less than 20MB I decided to keep files saved to the disk.

Although a Digital Ocean droplet is persistent I still wanted to have the binaries in an explicit data store. In a Cloud world it makes sense to have these not be part of the deployed application.

I therefore set up a data store application [Zemke/cwt-binary](https://github.com/Zemke/cwt-binary) in PHP Lumen with POST and GET endpoints for replays and user photos. \
The application relies on the filenames to be the ID of the user or game respectively and then the file extension. I therefore had to perform some batch renaming which I did using Bash scripts.

### Remove spam account photos

First of all I removed all profile photo of spam accounts. I therefore created a list of usernames which are real and removed any photo of a user that is not included in that list. \
Given is a directory `users/` containing users named after the username.

```bash
#!/bin/bash

NAMES=(
"Dario" "Zemke" "Joschi" "Korydex" "khamski" "zoky" "Alex13" "Ivo" "DarKxXxLorD" "Kayz" "franz" "KBA3u" "Mablak" "Tade" "Koras" "Fonseca" "Thouson" "viks" "Johnmir" "Bytor" "nickynick" "Stripe" "kilobyte" "Gopnick" "Tomek" "Crespo" "Zolodu" "K4Tsis" "Rafka" "Random00" "SirGorash" "kalababa" "FaD" "LittleBiatch" "Fenrys" "Chicken23" "Free" "WeeM4n" "pandello" "BetongAsna" "dsa" "Pipinasso" "Hussar" "kukumber" "euenahub" "pizzasheet" "Peja" "barman" "nOox" "LeTotalKiller" "Manolo" "Uzurpator" "SiD" "hldd3n" "Kyho" "Goro" "Wormslayer" "Tajniak" "SuperPipo" "j0hny" "13" "HwoarangCS" "Siwy" "Kano" "kain" "Akt" "Dmitry" "cangaceiro" "Marjano" "tanerrr" "Pellefot" "Chelsea" "DarkOne" "KRD" "TOMT" "Piki1802" "Husk" "Chilitolindo" "Senator" "c00L" "tita" "StepS" "combo" "Zexorz" "Felo" "StJimmy" "spyvsspyfan123" "tadeuszek" "MIGHTYtaner" "thewalrus" "Asbest" "KungPow" "kins" "Jakk0" "CoolMan" "ArtecTheFox" "pavlepavle" "Triad" "floydmilleraustr" "Aladdin" "Vojske" "Abegod" "Afina" "WhiteRqbbit" "DVD" "Afinaaa" "goosey" "WormingGirl" "Deadbeat" "Lagamba" "MegaAdnan" "SaaN" "vok90" "AduN" "daiNa" "Street" "Instantly" "Krivoy" "MemberBerries" "Kory" "mielu" "domi" "oz" "Jellenio" "Cinek" "Maze" "Jakka" "HaXeN" "Master" "WorldMaster" "TerRoR" "Automatico" "AquaWorm" "Chaos1187" "Chew" "Clay" "cOOL" "CrimsonScourge" "Darasek" "DustedTrooper" "Dani" "David" "Dvorkin" "Dan" "Doubletime" "DreamTrance" "Jigsaw" "KinslayeR" "lacoste" "Antares" "lales" "Albino" "chuvash" "Fantomas" "NormalPRO" "Jule" "MrTPenguin" "Boolc" "aSh" "akkad" "Ashmallum" "Eebu" "Feiticeiro" "fury" "Franpetas" "GeLeTe" "GeneralHorn" "Goku" "HHC" "GForce" "Hallq" "Hatequitter" "Intrepid" "Inspire" "Javito" "jessbaec" "Konrad" "Kortren" "Kisiro" "linusb" "Ljungberg" "LostBoy" "Luisinho" "Leoric" "Makimura" "MexicanGeneral" "MBonheur" "m0nk" "MajesticJara" "MasterTool" "Misirac" "Mattekale" "Nut" "Nachwein" "Optymus" "Papabizkitpark" "PitBacardi" "Phartknocker" "PaRaNoIcO" "PeTaZeTa" "Philippo" "RatoonSoft" "Retcerahc" "Rigga" "Raven" "Rafalus" "Ramone" "Spike" "Simon" "Saibot" "Semaphore" "Salda" "Sniper" "tixas" "Teletubbies" "Trixsk8" "UrbanSpaceman" "UncleDave" "UCantseeMe" "Ukrop" "Voodoo" "Vodkoff" "Vuk" "Wormsik" "Wriggler" "Xaositect" "XWorm" "Zero" "Drakken"
)

echo ${NAMES[*]}

containsElement () {
  local e match="$1"
  shift
  for e; do [[ "$e" == "$match" ]] && return 0; done
  return 1
}

for file in users/* 
do
  filename=$(echo "${file}" | sed -r 's/users\/(.+)\..*/\1/')
  containsElement "${filename}" "${NAMES[@]}" && mv "${file}" final/
done
```

### Rename photos from username to ID

```bash
#!/bin/bash

declare -A arr
arr=(["Zemke"]=1 ["Joschi"]=2 ["domi"]=3 ["Korydex"]=4 ["khamski"]=5 ["zoky"]=6 ["Alex13"]=7 ["Ivo"]=8 ["DarKxXxLorD"]=9 ["Kayz"]=10 ["Mablak"]=11 ["Tade"]=12 ["Koras"]=13 ["Fonseca"]=14 ["Thouson"]=15 ["viks"]=16 ["Johnmir"]=17 ["Bytor"]=18 ["Waffle"]=19 ["nickynick"]=20 ["Stripe"]=21 ["kilobyte"]=22 ["Gopnick"]=23 ["Tomek"]=24 ["Crespo"]=25 ["Zolodu"]=26 ["Dario"]=27 ["K4Tsis"]=28 ["Rafka"]=29 ["Random00"]=30 ["Jellenio"]=31 ["Cinek"]=32 ["Maze"]=33 ["HaXeN"]=35 ["Master"]=36 ["RatoonSoft"]=130 ["WorldMaster"]=37 ["TerRoR"]=38 ["Phantom"]=63 ["pandello"]=64 ["BetongAsna"]=65 ["DreamTrance"]=39 ["Jigsaw"]=40 ["KinslayeR"]=41 ["lacoste"]=42 ["Antares"]=43 ["lales"]=44 ["Albino"]=45 ["chuvash"]=46 ["Fantomas"]=47 ["NormalPRO"]=48 ["SirGorash"]=49 ["Jule"]=50 ["Thurbo"]=51 ["MrTPenguin"]=52 ["Boolc"]=53 ["Retcerahc"]=131 ["Rigga"]=132 ["kalababa"]=54 ["FaD"]=55 ["LittleBiatch"]=56 ["Fenrys"]=57 ["Chicken23"]=58 ["xDragonfirex"]=59 ["Diablovt"]=60 ["Free"]=61 ["WeeM4n"]=62 ["dsa"]=66 ["Pipinasso"]=67 ["aSh"]=68 ["akkad"]=69 ["Ashmallum"]=70 ["Automatico"]=71 ["AquaWorm"]=72 ["Chaos1187"]=73 ["Chew"]=74 ["Clay"]=75 ["cOOL"]=76 ["CrimsonScourge"]=77 ["Drakken"]=78 ["Darasek"]=79 ["DustedTrooper"]=80 ["Dani"]=81 ["David"]=82 ["Dvorkin"]=83 ["Dan"]=84 ["Doubletime"]=85 ["euenahub"]=86 ["Eebu"]=87 ["Feiticeiro"]=88 ["fury"]=89 ["Franpetas"]=90 ["GeLeTe"]=91 ["GeneralHorn"]=92 ["Goku"]=93 ["HHC"]=94 ["GForce"]=95 ["Hallq"]=96 ["Hatequitter"]=97 ["Intrepid"]=98 ["Inspire"]=99 ["Javito"]=100 ["jessbaec"]=101 ["Konrad"]=102 ["Kortren"]=103 ["Kisiro"]=104 ["linusb"]=105 ["Ljungberg"]=106 ["LostBoy"]=107 ["Luisinho"]=108 ["Leoric"]=109 ["Makimura"]=110 ["MexicanGeneral"]=111 ["MBonheur"]=112 ["mielu"]=113 ["Manolo"]=114 ["m0nk"]=115 ["MajesticJara"]=116 ["MasterTool"]=117 ["Misirac"]=118 ["Mattekale"]=119 ["Nut"]=120 ["Nachwein"]=121 ["Optymus"]=122 ["oz"]=123 ["Papabizkitpark"]=124 ["PitBacardi"]=125 ["Phartknocker"]=126 ["PaRaNoIcO"]=127 ["PeTaZeTa"]=128 ["Philippo"]=129 ["Raven"]=133 ["Rafalus"]=134 ["Ramone"]=135 ["Spike"]=136 ["Simon"]=137 ["Saibot"]=138 ["Semaphore"]=139 ["Salda"]=140 ["Sniper"]=141 ["tixas"]=142 ["Teletubbies"]=143 ["Trixsk8"]=144 ["UrbanSpaceman"]=145 ["UncleDave"]=146 ["UCantseeMe"]=147 ["Ukrop"]=148 ["Voodoo"]=149 ["Vodkoff"]=150 ["Vuk"]=151 ["Wormsik"]=152 ["Wriggler"]=153 ["Xaositect"]=154 ["XWorm"]=155 ["Zero"]=156 ["Hussar"]=157 ["kukumber"]=158 ["franz"]=159 ["KBA3u"]=160 ["pizzasheet"]=161 ["Casso"]=162 ["Peja"]=163 ["Gabriel"]=164 ["caniman"]=171 ["barman"]=174 ["nOox"]=178 ["LeTotalKiller"]=179 ["Uzurpator"]=180 ["Jago"]=181 ["Sbaffo"]=182 ["Crazy"]=186 ["SiD"]=187 ["hldd3n"]=192 ["Kyho"]=194 ["Goro"]=198 ["Wormslayer"]=199 ["Tajniak"]=200 ["SuperPipo"]=201 ["j0hny"]=202 ["13"]=208 ["HwoarangCS"]=209 ["Siwy"]=210 ["KnightTemplar"]=211 ["Kano"]=212 ["kain"]=213 ["WormingGirl"]=215 ["Akt"]=217 ["Dmitry"]=223 ["cangaceiro"]=224 ["Marjano"]=225 ["tanerrr"]=226 ["Pellefot"]=230 ["Evito"]=231 ["tadeusz"]=232 ["Szoszo"]=234 ["Chelsea"]=237 ["DarkOne"]=238 ["KRD"]=240 ["HenryCS"]=246 ["TOMT"]=248 ["vesuvio"]=250 ["Piki1802"]=251 ["Husk"]=252 ["Chilitolindo"]=253 ["Kaleu"]=254 ["Senator"]=256 ["c00L"]=258 ["tita"]=262 ["VoK"]=263 ["StepS"]=265 ["combo"]=266 ["Phanton"]=268 ["Zexorz"]=269 ["Felo"]=274 ["StJimmy"]=275 ["spyvsspyfan123"]=276 ["tadeuszek"]=287 ["thewalrus"]=290 ["Asbest"]=291 ["KungPow"]=292 ["kins"]=295 ["Jakka"]=34 ["CoolMan"]=298 ["ArtecTheFox"]=299 ["pavlepavle"]=312 ["Triad"]=329 ["MegaAdnan"]=331 ["floydmilleraustr"]=512 ["Aladdin"]=528 ["Vojske"]=625 ["Abegod"]=628 ["Afina"]=629 ["Atr0x"]=630 ["vok90"]=631 ["AduN"]=632 ["daiNa"]=633 ["Street"]=634 ["Instantly"]=637 ["WhiteRqbbit"]=639 ["DVD"]=685 ["Eray"]=1061 ["goosey"]=1331 ["Bhaal"]=1457 ["THYX"]=1460 ["FuSi"]=1810 ["Nunca"]=2284 ["PavelBistrov"]=6100 ["NouS"]=6109 ["Silaneo"]=6118 ["KingKong"]=6119 ["Stoner"]=6128 ["Kievz"]=6168 ["Sensei"]=6193 ["Campbell659"]=6787)

echo "${arr['Aladdin']}";

for file in final/*
do
  filename=$(echo "${file}" | sed -r 's/final\/(.+)\..*/\1/')
  #echo "$filename" 
  mv "$file" "final/${arr[$filename]}$(echo $file | sed -r 's/.*(\..*)$/\1/')"
done
```

#### Rename replays to just ID.WAgame

```bash
#!/bin/bash

for file in replays/*
do
  id=$(echo "$file" | sed -r 's/replays\/\[([0-9]+?)\].*/\1/')
  mv "$file" "final/${id}$(echo $file | sed -r 's/.*(\..*)$/\1/')";
done
```
