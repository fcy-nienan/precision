show databases;
use fcy;
show tables;
delete from enuminfo;
delete from metainfo;
delete from task;
delete from result;
delete from fields;



select * from enuminfo;
select * from fields;
select * from metainfo;
select * from task;
select * from result;

/*盘点完成*/
delete from result where id=5;
update task set status=3 where id=7;
update result set times=-1 where id=6;
