alter table user_
  add column country text;
alter table user_
  add column about text;

update user_ u
set country = (select country from user_profile p where p.user_id = u.id),
    about   = (select about from user_profile p where p.user_id = u.id);

drop table user_profile;
drop table user_setting;

alter table user_ rename to "user";

select *
from "user" order by id asc;
