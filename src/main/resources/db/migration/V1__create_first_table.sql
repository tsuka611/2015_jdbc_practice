create table test (
    id int(11) primary key auto_increment
  , name varchar(255) not null
);

-- first data
insert into test(name) values ('太郎');
insert into test(name) values ('次郎');
insert into test(name) values ('高志');
