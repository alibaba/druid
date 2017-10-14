#begin
-- Alter Table
alter table ship_class add column ship_spec varchar(150) first, add somecol int after start_build;
alter table t3 add column (c2 decimal(10, 2) comment 'comment`' null, c3 enum('abc', 'cba', 'aaa')), add index t3_i1 using btree (c2) comment 'some index';
alter table t2 add constraint t2_pk_constraint primary key (1c), alter column `_` set default 1;
alter table ship_class change column somecol col_for_del tinyint first;
alter table ship_class drop col_for_del;
alter table t3 drop index t3_i1;
alter table childtable drop index fk_idParent_parentTable;
alter table t2 drop primary key;
alter table t3 rename to table3column;
alter table childtable add constraint `fk1` foreign key (idParent) references parenttable(id) on delete restrict on update cascade;
alter table table3column default character set = cp1251;
#end
#begin
-- Alter database
alter database test default character set = utf8;
alter schema somedb_name upgrade data directory name;
#end
#begin
-- Alter event
alter definer = current_user event someevent on schedule at current_timestamp + interval 30 minute;
alter definer = 'ivan'@'%' event someevent on completion preserve;
alter definer = 'ivan'@'%' event someevent rename to newsomeevent;
alter event newsomeevent enable comment 'some comment';
-- delimiter //
alter definer = current_user event newsomeevent on schedule at current_timestamp + interval 2 hour
rename to someevent disable
do begin update test.t2 set 1c = 1c + 1; end; -- //
-- delimiter ;
#end
#begin
-- Alter function/procedure
alter function f_name comment 'some funct' language sql sql security invoker;
alter function one_more_func contains sql sql security definer;
alter procedure p_name comment 'some funct' language sql sql security invoker;
alter procedure one_more_proc contains sql sql security definer;
#end
#begin
-- Alter logfile group
-- http://dev.mysql.com/doc/refman/5.6/en/alter-logfile-group.html
ALTER LOGFILE GROUP lg_3 ADD UNDOFILE 'undo_10.dat' INITIAL_SIZE=32M ENGINE=NDBCLUSTER;
ALTER LOGFILE GROUP lg_1 ADD UNDOFILE 'undo_10.dat' wait ENGINE=NDB;
#end
#begin
-- Alter server
-- http://dev.mysql.com/doc/refman/5.6/en/alter-server.html
ALTER SERVER s OPTIONS (USER 'sally');
#end
#begin
-- Alter tablespace
alter tablespace tblsp_1 add datafile 'filename' engine = ndb;
alter tablespace tblsp_2 drop datafile 'deletedfilename' wait engine ndb;
#end
#begin
-- Alter view
alter view my_view1 as select 1 union select 2 limit 0,5;
alter algorithm = merge view my_view2(col1, col2) as select * from t2 with check option;
alter definer = 'ivan'@'%' view my_view3 as select count(*) from t3;
alter definer = current_user sql security invoker view my_view4(c1, 1c, _, c1_2) 
	as select * from  (t1 as tt1, t2 as tt2) inner join t1 on t1.col1 = tt1.col1;
#end
