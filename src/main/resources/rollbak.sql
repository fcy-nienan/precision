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
delete from result where precision_id=10;
update result set amount=20 where precision_id=11 and times=-1;
update result set times=-1 where id=6;
update task set status=1 where precision_id=10;
