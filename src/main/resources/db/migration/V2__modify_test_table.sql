alter table test add mail varchar(255);
alter table test add tel varchar(255);
alter table test add update_date datetime;
alter table test add is_deleted boolean not null default 0;

-- update for test data
update test set mail = 'hoge@active.co.jp', tel='09050505555', update_date = '2014-04-12' where name = '太郎';
update test set mail = 'fuga@active.co.jp', tel='09060606666', update_date = '2014-04-11' where name = '次郎';
update test set mail = 'takashi@active.co.jp', tel='09060406444', update_date = '2013-04-11' where name = '高志';
