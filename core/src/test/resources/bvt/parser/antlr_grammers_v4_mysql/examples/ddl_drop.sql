#begin
-- Drop table
drop temporary table if exists temp_t1;
drop temporary table `some_temp_table`;
-- drop table if exists `one_more 1343 *&&^ table`;
drop table antlr_all_tokens, antlr_function_tokens, antlr_keyword_tokens, antlr_tokens, childtable, guns, log_table, new_t, parenttable, ship_class, ships, ships_guns, t1, t2, t3, t4, tab1;
#end
#begin
-- Drop database
drop database somedb;
drop schema if exists myschema;
drop database if exists `select`;
drop schema `current_date`;
drop schema if exists `super`;
#end
#begin
-- Drop event
drop event if exists testevent1;
drop event if exists testevent2;
drop event someevent;
#end
#begin
-- Drop index
drop index index1 on t1 algorithm=default;
drop index index2 on t2 algorithm=default lock none;
drop index index3 on antlr_tokens algorithm default lock=none;
#end
#begin
-- Drop logfile group
-- http://dev.mysql.com/doc/refman/5.6/en/create-logfile-group.html
DROP LOGFILE GROUP lg1 ENGINE = NDB;
#end
#begin
-- Drop server
drop server if exists s;
drop server some_server_name_enough_character_length;
#end
#begin
-- Drop tablespace
drop tablespace tblsp1 engine = NDB;
drop tablespace tblsp2 engine = InnoDB;
#end
#begin
-- Drop trigger
drop trigger if exists test.trg_my1;
drop trigger trg_my2;
#end
#begin
-- Drop view
drop view if exists my_view1, my_view2, my_view3, my_view4;
drop view some_view restrict;
drop view if exists `view`, one_more_view, 1view cascade;
#end
#begin
-- Drop procedure
drop procedure if exists some_proc;
drop procedure some_proc;
#end
#begin
-- Drop function
drop function if exists foo;
drop function bar;

#end
