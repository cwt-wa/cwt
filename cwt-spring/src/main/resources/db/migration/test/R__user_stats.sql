create table if not exists user_stats
(
  user_id        bigint not null,
  trophy_points  int    not null,
  participations int    not null,
  timeline       text   not null,
  constraint fkio2xcw9ogcqb
    primary key (user_id),
  constraint qjy43i7zadjadj
    foreign key (user_id) references "user"
);
