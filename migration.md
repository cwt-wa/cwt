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

### MySQL to PostgreSQL

The migration from MySQL to PostgreSQL is as easy as executing a command-line command. [`pgloader`](https://pgloader.io) is the great help here.

```
pgloader mysql://root:cwt@localhost/db10838396-cwt pgsql://postgres:postgres@localhost/migration
```

Once that's done and the issues logged by `pgloader` have been resolved, one can proceed by running the CWT 6 Flyway migration. This thankfully work on top of the existing CWT 5 schema without conflicting. The Flyway configuration could look like this:

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

United Kingdom had two mappings in the DB the one with underscore being the correct.

```sql
update profiles
set country='United Kingdom'
where country = 'United_Kingdom';
```

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