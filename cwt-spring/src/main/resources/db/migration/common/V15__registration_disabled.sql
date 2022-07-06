insert into
  configuration
  (key, modified, value, author_id, description)
values
  (
    'DISABLE_REGISTRATION',
    now(),
    'false',
    (select id from "user" where username='Zemke'),
    'Disable registration of further users.'
  );

