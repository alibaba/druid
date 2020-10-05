package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Test;

public class ADB30_IntegrateUserPrivilege extends MysqlTest {


	@Test
	public void testView() throws Exception {
		// 首先用户创建一个view, 用户没有任何权限，不能read这张表
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("use test4dmp");
		parse("create view tt as select id from grade");

		// 用户有物理table权限，不可以read这张表
		parse("grant select on grade to test identified by 'ccc'");

		// 用户有global update权限，不可以read这张表
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("grant update on *.* to test");
		parse("grant update on * to test");

		// 用户有view table select权限，可以read这张表
		parse("grant select on test4dmp.tt to test");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 用户有global权限，可以read这张表
		parse("grant select on *.* to test");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 用户有db权限，可以read这张表
		parse("grant select on * to test");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 漏洞：用户有view table select权限，此时select view table和view映射的物理表，虽然物理表没有权限，但是仍然可以select.
		parse("grant select on test4dmp.tt to test");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// show create view权限
		parse("grant SHOW VIEW on test4dmp.tt to test");
		parse("grant Select on test4dmp.tt to test");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		parse("grant SHOW VIEW on * to test");
		parse( "show create view tt");
		parse("grant Select on test4dmp.tt to test");
		parse( "show create view tt");
	}

	@Test
	public void testDropView() throws Exception {
		// 用户创建一个view
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("use test4dmp");
		parse("create view tt2 as select id from grade");

		// 拥有global delete 权限，失败
		parse("grant delete on *.* to test");
		parse( "drop view test4dmp.tt2");

		// 拥有other db drop权限，失败
		parse("grant drop on mysql.* to test");
		parse( "drop view test4dmp.tt2");

		// 拥有other table drop 权限，失败
		parse("grant drop on test4dmp.tt to test");
		parse( "drop view test4dmp.tt2");

		// 拥有global drop权限，成功
		parse("grant drop on *.* to test");
		parse("drop view test4dmp.tt2");

		// 拥有db drop 权限，成功
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("create view tt2 as select id from grade");
		parse("grant drop on test4dmp.* to test");
		parse("drop view test4dmp.tt2");

		// 拥有table drop权限，成功
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("create view tt2 as select id from grade");
		parse("grant drop on test4dmp.tt2 to test");
		parse("drop view test4dmp.tt2");
	}

	@Test
	public void testCreateView() throws Exception {
		// 用户创建一个view
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("use test4dmp");
		parse("grant select on *.* to test");

		// 没有Create_view权限，有select权限，create view失败
		parse("create view tt2 as select id from grade");

		// Create_view其他db权限，有select权限，create view失败
		parse("grant CREATE VIEW on mysql.* to test");
		parse("create view tt2 as select id from grade");

		// Create_view权限准确，有select权限，create view失败
		parse("grant CREATE VIEW on test4dmp.* to test");
		parse("create view tt2 as select id from grade");
		parse("drop view test4dmp.tt2");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// table级别create view, column级别select 级别校验
		parse("grant CREATE VIEW on test4dmp.tt3 to test");
		parse("grant select(id) on test4dmp.grade to test");
		parse("create view tt2 as select id from grade");

		// db级别create view, table级别select 级别校验
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("grant CREATE VIEW on test4dmp.* to test");
		parse("grant select on test4dmp.grade to test");
		parse("create view tt3 as select id from grade limit 1");

		// db级别create view, table级别select 级别校验 失败
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("grant CREATE VIEW on test4dmp.* to test");
		parse("grant select on test4dmp.test to test");
		parse("drop view test4dmp.tt3");
		parse("create view tt3 as select id from grade limit 1");

		// create view replace语法，需要DROP权限
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("grant CREATE VIEW on test4dmp.* to test");
		parse("grant select on test4dmp.grade to test");
		parse("create or replace view tt3 as select id from grade limit 1");

		parse("grant DROP on test4dmp.grade to test");
		parse("create  or replace view tt3 as select id from grade limit 1");

		parse("grant DROP on test4dmp.tt3 to test");
		parse("create or replace view tt3 as select id from grade limit 1");

		// create view replace语法，需要DROP权限
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("grant CREATE VIEW on test4dmp.* to test");
		parse("grant select on test4dmp.grade to test");
		parse("grant DROP on test4dmp.* to test");
		parse("create  or replace view tt3 as select id from grade limit 1");
	}

	@Test
	public void testShowCreateTable() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("use test4dmp");
		parse("grant select on *.* to test");

		parse("show create table test4dmp.test");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		parse("grant update on *.* to test");
		parse("show create table test4dmp.test");

		parse("grant select on test4dmp.test to test");
		parse("show create table test4dmp.test");
	}

	@Test
	public void test_1() throws Exception {
		SQLStatement parse = parse("CREATE USER IF NOT EXISTS 'adb_system' IDENTIFIED BY 'qpzkC6cgQGPxte5';");
		System.out.println(parse);
	}

	@Test
	public void testInsertSelect() throws Exception {
		// 验证sql:  insert into test select * from test02 where id=15;
		// 没有权限场景下失败
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		parse("create user if not exists 'test'@'%' IDENTIFIED BY 'ccc';");
		parse("drop table if exists test4p.tt2");
		parse("create table test4p.tt2(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");

		parse("create table test4p.tt3(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		parse("insert into test4p.tt3 values(4,2);");
		parse("insert into test4p.tt3 values(5,2);");
		parse("insert into test4p.tt3 values(6,2);");
		parse("insert into tt2 select * from tt3 where id=1;");
		parse("insert into test4p.tt2 select * from test4p.tt3 where id=1;");

		parse("grant all on test4p.tt to test@'%' identified by 'ccc';");
		parse("insert into tt2 select * from tt3 where id=1;");
		parse("insert into test4p.tt2 select * from test4p.tt3 where id=1;");
		parse("grant select on test4p.tt3 to test@'%' identified by 'ccc';");
		parse("insert into tt2 select * from tt3 where id=1;");
		parse("insert into test4p.tt2 select * from test4p.tt3 where id=1;");
		parse("grant insert on test4p.tt2 to test@'%' identified by 'ccc';");
		parse("drop user if exists 'test';");

		// 提供了db级别all权限的场景下，可以成功插入数据
		// 跑的时间太长，忽略掉
//		parse("grant all on test4p.* to test@'%' identified by 'ccc';");
//		parse("insert into tt2 select * from tt3 where id=10;");
//		parse("drop user if exists 'test';");

		// 提供global级别all权限场景下，可以成功插入数据
		// 跑的时间太长，忽略掉
//		parse("grant all on *.* to test@'%' identified by 'ccc';");
//		parse("insert into tt2 select * from tt3 where id=10;");
//		parse("drop user if exists 'test';");
	}

	@Test
	public void testCreateDropUser() throws Exception {
		parse("use mysql");
		parse("select * from mysql.user");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		parse("create user if not exists 'test'@'%' IDENTIFIED BY 'testpasswd';");
		parse("select user()");
		parse("select 1");
		parse( "select * from mysql.user");

		parse("drop user if exists 'test'");

		parse("create user if not exists test@'%' IDENTIFIED BY password 'testpasswd';");

		// create redundance fail
		parse("create user test@'%s' IDENTIFIED BY password 'testpasswd'");

		parse("drop user 'test'");
		// drop not exists fail
		parse("drop user 'test'");

		// create three user
		parse("create user if not exists test1@'%' identified by 'aa2', if not exists test2@'%' identified by password 'bb', test3;");

		parse("grant update(user,host),insert(user),select on mysql.user to test1@'%' identified by 'ccc';");
		parse("grant update,select on *.* to test1@'%' identified by 'ccc';");
		parse("grant update,select on mysql.* to test1@'%' identified by 'ccc';");

		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
	}

	@Test
	public void testRenameUser() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 具有 global/db/table/column 权限的用户 , rename一次
		parse("create user test@'%' identified by '111111';");
		parse("grant select,update on *.* to test@'%';");
		parse("grant update on mysql.* to test@'%';");
		parse("grant update on mysql.user to test@'%';");
		parse("grant update(host) on mysql.user to test@'%';");

		parse("rename user test@'%' to test1@'%';");
		parse("select * from mysql.user");
	}

	@Test
	public void testSetPassword() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3'");
		parse("create user test@'%' IDENTIFIED BY 'testpasswd';");

		parse("set password for 'test'@'%' = 'ddd';");

		parse("set password for 'test'@'%' = password('ddd')");

		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3'");
		parse("create user test@'%' IDENTIFIED BY password 'testpasswd'");
		parse("set password for 'test'@'%' = password('ddd')");

		parse("set password for 'test' = password('eee')");
	}

	@Test
	public void testGrantRevoke() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		parse("grant select on *.* to test@'%' identified by 'ccc';");

		parse("grant all on *.* to test@'%' identified by 'ccc';");

		parse("grant grant option on *.* to test@'%' identified by 'ccc';");

		parse("revoke select, grant option on *.* from test@'%';");

		parse("grant select on aa.* to test@'%' identified by 'ddd';");

		parse("grant update,delete on aa.* to test@'%' identified by 'ccc' with grant option;");

		parse("grant update,delete on aa.bb to test@'%' identified by 'ccc' with grant option;");
		parse("revoke update,delete on aa.bb from test@'%'");

//		parse("grant update(dd,cc),delete(dd),select on aa.bb to test1@'%' identified by 'ccc';");

		parse("grant update(user,host),insert(user),select on mysql.user to test1@'%' identified by 'ccc';");

		parse("revoke update(user),select on mysql.user from test1@'%';");

		parse("grant select, update on db.tbl to 'test'");

		parse("use mysql");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("grant select, update on host to 'test';");
		parse("revoke all privileges on host from 'test'");
		parse("revoke all on host from 'test'");
	}


	@Test
	public void testUserADUSExecuteSQL() throws Exception {
		parse("drop user if exists 'test';");

		// global授权selelct, 可以select所有表
		parse("grant select on *.* to test@'%' identified by 'ccc';");
		parse("use mysql");
		parse("select * from user;");
		parse("select * from mysql.user;");
		parse("select * from INFORMATION_SCHEMA.KEPLER_META_TABLES;");
		parse("drop user if exists 'test';");

		// global授权all, 可以select所有表
		parse("grant all on *.* to test@'%' identified by 'ccc';");
		parse("select * from user;");
		parse("select * from mysql.user;");
		parse("select * from INFORMATION_SCHEMA.KEPLER_META_TABLES;");
		parse("drop user if exists 'test';");

		// db级别授权all, 可以select mysql表，不可以select INFORMATION_SCHEMA表
		parse("grant all on mysql.* to test@'%' identified by 'ccc';");
		parse("select * from user;");
		parse("select * from mysql.user;");
		parse( "select * from INFORMATION_SCHEMA.KEPLER_META_TABLES;");

		// db级别授权select, 可以select mysql表，不可以select INFORMATION_SCHEMA表
		parse("revoke all on mysql.* from test@'%';");
		parse("grant select on mysql.* to test@'%' identified by 'ccc';");
		parse("select * from user");
		parse("select * from db");
		parse("select * from mysql.user");
		parse("select * from mysql.db");
		parse( "select * from INFORMATION_SCHEMA.KEPLER_META_TABLES;");

		// table级别授权all, 可以select mysql表，不可以select INFORMATION_SCHEMA表和mysql库其他表
		parse("revoke select on mysql.* from test@'%';");
		parse("grant all on mysql.user to test@'%' identified by 'ccc';");
		parse( "select * from INFORMATION_SCHEMA.KEPLER_META_TABLES;");
		parse( "select * from db;");
		parse( "select * from mysql.db;");

		// table级别授权select, 可以select mysql表，不可以select INFORMATION_SCHEMA表和mysql库其他表
		parse("revoke all on mysql.user from test@'%';");
		parse("grant select on mysql.user to test@'%' identified by 'ccc';");
		parse( "select * from INFORMATION_SCHEMA.KEPLER_META_TABLES;");
		parse( "select * from db;");
		parse( "select * from mysql.db;");
		parse("drop user if exists 'test';");

		// column级别授权select host, 可以select mysql表，不可以select INFORMATION_SCHEMA表和mysql库其他表
		// mysql/information_schema 这些内存表没有做指定列获取的sql优化, 所以这里肯定会获取不到
//		parse("revoke select on mysql.user from test@'%';");
//		parse("grant select(host) on mysql.user to test@'%' identified by 'ccc';");
//		parse("select host from mysql.user;");
//		parse("select user from mysql.user;");
//		parse("select * from INFORMATION_SCHEMA.KEPLER_META_TABLES");
//		parse("select * from mysql.db");
//		parse("drop user if exists 'test'");

		// 创建表, 并做insert校验
		parse("use test4p");
		parse("create table test4p.tt(id int) DISTRIBUTE BY BROADCAST;");

		// global select权限不可以 insert 数据
		parse("grant select on *.* to test@'%' identified by 'ccc';");
		parse("select * from tt;");
		parse( "insert into tt values(3);");
		parse("select * from test4p.tt;");
		parse( "insert into test4p.tt values(1);");

		// global insert 权限可以 insert 数据
		parse("grant insert on *.* to test@'%' identified by 'ccc';");
		parse("select * from tt;");
		parse("insert into tt values(2);");
		parse("select * from test4p.tt;");
		parse("insert into test4p.tt values(1);");
		parse("drop user if exists 'test';");

		// db insert 权限可以 insert 数据
		parse("grant insert on test4p.* to test@'%' identified by 'ccc';");
		parse( "select * from tt;");
		parse("insert into tt values(1);");
		parse( "select * from test4p.tt;");
		parse("insert into test4p.tt values(1);");
		parse("drop user if exists 'test';");

		// 其他db insert 权限不可以 insert 数据
		parse("grant insert on mysql.* to test@'%' identified by 'ccc';");
		parse( "select * from tt;");
		parse( "insert into tt values(1);");
		parse( "select * from test4p.tt;");
		parse( "insert into test4p.tt values(1);");
		parse("drop user if exists 'test';");

		// table insert 权限不可以 insert 数据
		parse("grant insert on test4p.tt to test@'%' identified by 'ccc';");
		parse( "select * from tt;");
		parse("insert into tt values(1);");
		parse( "select * from test4p.tt;");
		parse("insert into test4p.tt values(1);");
		parse("drop user if exists 'test';");

		// column insert 权限可以 insert 数据
		parse("grant insert(id) on test4p.tt to test@'%' identified by 'ccc';");
		parse( "select * from tt;");
		parse("insert into tt values(1);");
		parse( "select * from test4p.tt;");
		parse("insert into test4p.tt values(1);");
		parse("insert into test4p.test values(1);");
		parse("drop user if exists 'test';");

		////////// test update
		parse("create table test4p.tt2(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		// global insert权限不可以 update 数据
		parse("grant insert on *.* to test@'%' identified by 'ccc';");
		parse("update tt2 set id2=2;");
		parse("update test4p.tt2 set id2=2;");
		parse("drop user if exists 'test';");

		// global update权限可以 update 数据
		parse("grant update on *.* to test@'%' identified by 'ccc';");
		parse("update tt2 set id2=2;");
		parse("update test4p.tt2 set id2=2;");
		parse("drop user if exists 'test';");

		// db update权限可以 update 数据
		parse("grant update on test4p.* to test@'%' identified by 'ccc';");
		parse("update tt2 set id2=2;");
		parse("update test4p.tt2 set id2=2;");
		parse("drop user if exists 'test';");

		// 其他db update权限不可以 update 数据
		parse("grant update on mysql.* to test@'%' identified by 'ccc';");
		parse("update tt2 set id2=2;");
		parse("update test4p.tt2 set id2=2;");

		// table update权限可以 update 数据
		parse("grant update on test4p.tt2 to test@'%' identified by 'ccc';");
		parse("update tt2 set id2=2;");
		parse("update test4p.tt2 set id2=2;");
		parse("drop user if exists 'test';");

		// 其他table update权限不可以 update 数据
		parse("grant update on test4p.t3 to test@'%' identified by 'ccc';");
		parse("update tt2 set id2=2;");
		parse("update test4p.tt2 set id2=2;");
		parse("drop user if exists 'test';");

		// column update 权限可以 update 数据
		parse("grant update(id2) on test4p.tt2 to test@'%' identified by 'ccc';");
		parse("update tt2 set id2=2;");
		parse("update test4p.tt2 set id2=2;");
		parse("drop user if exists 'test';");

		// all 权限可以 update 数据
		parse("grant all on test4p.tt2 to test@'%' identified by 'ccc';");
		parse("update tt2 set id2=2;");
		parse("update test4p.tt2 set id2=2;");
		parse("drop user if exists 'test';");

		////////// test delete
		// global delete 权限可以 delete 数据
		parse("grant delete on *.* to test@'%' identified by 'ccc';");
		parse("delete from tt2 where id2=2;");
		parse("delete from test4p.tt2 where id2=2;");
		parse("drop user if exists 'test';");

		// db delete 权限可以 delete 数据
		parse("grant delete on test4p.* to test@'%' identified by 'ccc';");
		parse("delete from tt2 where id2=2;");
		parse("delete from test4p.tt2 where id2=2;");
		parse("drop user if exists 'test';");

		// 其他db delete 权限不可以 delete 数据
		parse("grant delete on mysql.* to test@'%' identified by 'ccc';");
		parse("delete from tt2 where id2=2;");
		parse("delete from test4p.tt2 where id2=2;");
		parse("drop user if exists 'test';");

		// table delete 权限可以 delete 数据
		parse("grant delete on test4p.tt2 to test@'%' identified by 'ccc';");
		parse("delete from tt2 where id2=2;");
		parse("drop user if exists 'test';");

		// table all 权限可以 delete 数据
		parse("grant all on test4p.tt2 to test@'%' identified by 'ccc';");
		parse("delete from tt2 where id2=2;");
		parse("drop user if exists 'test';");

		// 其他table all 权限不可以 delete 数据
		parse("grant all on test4p.tt to test@'%' identified by 'ccc';");
		parse("delete from tt2 where id2=2;");
		parse("delete from test4p.tt2 where id2=2;");
		parse("drop user if exists 'test';");

		// 验证sql: truncate
		// 没有权限场景下会失败
		parse("create table test4p.truncatetestt(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		parse("truncate table truncatetestt");
		parse("truncate table test4p.truncatetestt");
		parse("grant all on mysql.* to test@'%' identified by 'ccc';");
		parse("truncate table truncatetestt");
		parse("truncate table test4p.truncatetestt");
		parse("grant DROP on test4p.* to test@'%' identified by 'ccc';");
		parse("truncate table truncatetestt");
		parse("truncate table test4p.truncatetestt");
		parse("drop user if exists 'test';");

		// 存在all权限场景下成功
		parse("grant ALL on *.* to test@'%' identified by 'ccc';");
		parse("truncate table truncatetestt");
		parse("truncate table test4p.truncatetestt");
		parse("drop user if exists 'test';");

		// 验证sql: replace into test(id,col1,col2,col3) values(22,'row22','2018-01-01',3.1);
		parse("grant ALL on *.* to test@'%' identified by 'ccc';");
		parse("replace into truncatetestt values(4,2);");
		parse("replace into test4p.truncatetestt values(4,2);");
		parse("drop user if exists 'test';");

		parse("grant ALL on test4p.truncatetestt to test@'%' identified by 'ccc';");
		parse("replace into truncatetestt values(4,2);");
		parse("replace into test4p.truncatetestt values(4,2);");
		parse("drop user if exists 'test';");

		parse("grant insert(id, id2) on test4p.truncatetestt to test@'%' identified by 'ccc';");
		parse("replace into truncatetestt values(4,2);");
		parse("replace into test4p.truncatetestt values(4,2);");
		parse("drop user if exists 'test';");

		parse("grant select on test4p.* to test@'%' identified by 'ccc';");
		parse("replace into truncatetestt values(4,2);");
		parse("replace into test4p.truncatetestt values(4,2);");
		parse("drop user if exists 'test';");

		parse("grant select on *.* to test@'%' identified by 'ccc';");
		parse("replace into truncatetestt values(4,2);");
		parse("replace into test4p.truncatetestt values(4,2);");
		parse("drop user if exists 'test';");

		// 验证sql: insert overwrite into test select * from test02 where id=3;
		parse("insert overwrite into test select * from test02 where id=3;");
		parse("insert into truncatetestt values(1, 2) on duplicate key update id2=2;");
		parse("insert into test4p.truncatetestt values(1, 2) on duplicate key update id2=2;");
		parse("grant insert on test4p.* to test@'%' identified by 'ccc';");
		parse("insert into truncatetestt values(1, 2) on duplicate key update id2=2;");
		parse("insert into test4p.truncatetestt values(1, 2) on duplicate key update id2=2;");
	}

	@Test
	public void testUserExecSQLAboutPrivilege() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2'");

		// 创建一个空用户，没有任何权限是无法 CREATE USER, DROP USER, REVOKE USER, GRANT, SET PASSWORD的
		parse("create user test@'%' identified by 'ccc';");
		parse("grant grant option on *.* to test1@'%' identified by 'ccc';");
		parse( "select * from mysql.user;");
		parse("create user if not exists 'test1'@'%';");
		parse("grant grant option on *.* to test1@'%';");
		parse("drop user if exists 'test1'@'%';");
		parse("revoke grant option on *.* from test1@'%';");
		parse("set password for 'test1'@'%' = password('ddd')");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2'");

		// grant 权限只能执行grant操作
		parse("grant grant option, create user on *.* to test@'%'");
		parse("create user if not exists 'test1'@'%';");
		parse( "select * from mysql.user;");
		parse("grant grant option on *.* to test1@'%'");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2'");

		// CREATE_USER 权限可以执行 create user操作
		parse("grant grant option, create user on *.* to test@'%'");
		parse("create user if not exists 'test1'@'%';");
		parse("grant grant option on *.* to test1@'%';");
		parse("revoke grant option on *.* from test1@'%';");
		parse("rename user 'test1' to 'test2'");
		parse("drop user 'test2'@'%';");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2'");
	}


	@Test
	public void testCreateTable() throws Exception {
		// table的all权限可以create table, drop table
		parse("drop user if exists 'test';");
		parse("grant all on test4p.ttc3 to test@'%' identified by 'ccc';");
		parse("use test4p");
		parse("create table ttc3(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		parse("drop table ttc3");
		parse("drop user if exists 'test';");

		// db的all权限可以create table
		parse("grant all on test4p.* to test@'%' identified by 'ccc';");
		parse("create table ttc4(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		parse("drop table test4p.ttc4");
		parse("drop user if exists 'test';");

		// db的create权限可以create table
		parse("grant CREATE on test4p.* to test@'%' identified by 'ccc';");
		parse("create table ttc5(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		parse("drop table test4p.ttc5");
		parse("drop user if exists 'test';");

		// db的update权限不可以create table
		parse("grant UPDATE on test4p.* to test@'%' identified by 'ccc';");
		parse("create table ttc6(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		parse("drop user if exists 'test';");

		// global的update权限不可以create table
		parse("grant UPDATE on *.* to test@'%' identified by 'ccc';");
		parse("create table test4p.ttc7(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		parse("drop user if exists 'test';");

		// table的create权限可以create table
		parse("grant CREATE on test4p.ttc8 to test@'%' identified by 'ccc';");
		parse("create table ttc8(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		parse("drop user if exists 'test';");

		// table的drop权限可以drop table
		parse("grant DROP,CREATE on test4p.ttc9 to test@'%' identified by 'ccc';");
		parse("create table ttc9(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		parse("drop table ttc9");
		parse("drop user if exists 'test';");
	}

	@Test
	public void testDropDatabase() throws Exception {
		parse("drop user if exists 'test';");
		// global的insert权限，不可以drop database
		parse("grant INSERT on *.* to test@'%' identified by 'ccc';");
		parse("create database test5p");
		parse("drop database test5p");
		parse("drop user if exists 'test';");

		// db的drop权限，可以drop database
		parse("grant DROP on test5p.* to test@'%' identified by 'ccc';");
		parse("drop database test5p");
		parse("drop user if exists 'test';");

		// global的all权限，可以drop database
		parse("grant ALL on *.* to test@'%' identified by 'ccc';");
		parse("create database test5p");
		parse("drop database test5p");
		parse("drop user if exists 'test';");
	}

	@Test
	public void testAlterTable() throws Exception {
		parse("grant INSERT on *.* to test@'%' identified by 'ccc';");
		parse("use test4p;");
		parse("drop user if exists 'test';");

		parse("create table test4p.talt1(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");
		parse("alter table talt1 add column id3 int;");

		parse("grant ALTER on *.* to test@'%' identified by 'ccc';");
		parse("alter table test4p.talt1 add column id3 int;");
		parse("alter table talt1 add column id3 int;");
		parse("grant CREATE on *.* to test@'%' identified by 'ccc';");
		parse("alter table test4p.talt1 add column id3 int;");
		parse("alter table talt1 add column id3 int;");
		parse("grant INSERT on *.* to test@'%' identified by 'ccc';");
		parse("alter table test4p.talt1 add column id3 int;");
		parse("alter table talt1 add column id6 int;");
		parse("drop user if exists 'test';");

		// db/table级别授权，也可以执行命令
		parse("grant CREATE on test4p.* to test@'%' identified by 'ccc';");
		parse("grant INSERT on test4p.* to test@'%' identified by 'ccc';");
		parse("grant ALTER on test4p.talt1 to test@'%' identified by 'ccc';");
		parse("alter table test4p.talt1 add column id4 int;");
		parse("alter table talt1 add column id5 int;");
	}

	@Test
	public void testUpdateInto() throws Exception {
		parse("grant ALTER on *.* to test@'%' identified by 'ccc';");
		parse("use test4p;");
		parse("drop user if exists 'test';");

		parse("create table test4p.updatet4p(id int, id2 int, primary key (id)) DISTRIBUTE BY BROADCAST;");

//		parse("update into test4p.updatet4p values(1, 1)");
//		parse("update into updatet4p values(1, 1)");
//		parse("update into test4p.updatet4p values(1, 2)");
//		parse("update into updatet4p values(1, 2)");
		parse("grant SELECT on test4p.* to test@'%' identified by 'ccc';");
//		parse("update into test4p.updatet4p values(1, 2)");
//		parse("update into updatet4p values(1, 2)");
		parse("grant UPDATE on test4p.updatet4p to test@'%' identified by 'ccc';");
//		parse("update into test4p.updatet4p values(1, 2)");
//		parse("update into updatet4p values(1, 2)");
	}

	@Test
	public void testRevokeAll() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("grant select,update,delete on *.* to test1@'%' identified by 'ccc';");
		parse("grant select,update,delete on mysql.* to test1@'%' identified by 'ccc';");
		parse("grant select,update,delete on mysql.host to test1@'%' identified by 'ccc';");
		parse("grant update(host) on mysql.host to test1@'%' identified by 'ccc';");

		parse("grant select,update,delete on *.* to test@'%' identified by 'ccc';");
		parse("use mysql");

		// 没有授权CREATE USER权限的时候, revoke失败
		parse("revoke all privileges, grant option from test1;");
		parse("grant CREATE USER on *.* to test@'%';");
		parse("revoke all privileges from test1;");
		parse("revoke all from test1;");
		parse("revoke grant option from test1;");
		parse("revoke all privileges, grant option on *.* from test1;");

		parse("revoke all privileges, grant option from test1;");
	}

	public void grantDone() throws Exception {
		parse("grant select on *.* to test1;");
		parse("grant select on mysql.* to test1;");
		parse("grant select on mysql.host to test1;");
		parse("grant select(host) on mysql.host to test1;");
	}

	@Test
	public void testRevokeGrant2() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		// 如果用户有global权限grant select，能够grant global/db/table/column select权限
		parse("grant select, CREATE USER on *.* to test@'%' with grant option;");
		parse("grant select on *.* to test1;");
		parse("grant select on mysql.* to test1;");
		parse("grant select on mysql.host to test1;");
		parse("grant select(host) on mysql.host to test1;");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("grant select, grant option, CREATE USER on *.* to test@'%';");
		parse("grant select on *.* to test1;");
		parse("grant select on mysql.* to test1;");
		parse("grant select on mysql.host to test1;");
		parse("grant select(host) on mysql.host to test1;");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 如果用户有db权限grant select, 不能够grant global的select，能够grant db/table/column.
		parse("grant select on mysql.* to test@'%' with grant option;");
		parse("grant CREATE USER on *.* to test@'%'");
		parse("grant select on *.* to test1;");
		parse("grant select on mysql.* to test1;");
		parse("grant update on mysql.* to test1;");
		parse("grant select on mysql.host to test1;");
		parse("grant update on mysql.host to test1;");
		parse("grant select(host) on mysql.host to test1;");
		parse("grant update(host) on mysql.host to test1;");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 如果用户有table权限grant select, 不能够grant global/db. 能够grant table/column.
		parse("grant select on mysql.host to test@'%' with grant option;");
		parse("grant CREATE USER on *.* to test@'%'");
		parse("grant select on *.* to test1;");
		parse("grant select on mysql.* to test1;");
		parse("grant select on mysql.host to test1;");
		parse("grant update on mysql.host to test1;");
		parse("grant select(host) on mysql.host to test1;");
		parse("grant update(host) on mysql.host to test1;");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 如果用户没有grant的global all权限, 不能够grant global/db/table/column.
		parse("grant all on *.* to test@'%'");
		parse("grant select on *.* to test1;");
		parse("grant select on mysql.* to test1;");
		parse("grant select on mysql.host to test1;");
		parse("grant select(host) on mysql.host to test1;");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		///////
		// 如果用户有global权限grant select，能够 revoke global/db/table/column select权限
		parse("grant select, CREATE USER on *.* to test@'%' with grant option;");
		grantDone();
		parse("revoke select on *.* from test1;");
		parse("revoke update on *.* from test1;");
		parse("revoke select on mysql.* from test1;");
		parse("revoke update on mysql.* from test1;");
		parse("revoke select on mysql.host from test1;");
		parse("revoke update on mysql.host from test1;");
		parse("revoke select(host) on mysql.host from test1;");
		parse("revoke update(host) on mysql.host from test1;");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 如果用户有db权限grant select, 不能够revoke global的select，能够revoke db/table/column.
		parse("grant select on mysql.* to test@'%' with grant option;");
		parse("grant CREATE USER on *.* to test@'%'");
		grantDone();
		parse("revoke select on *.* from test1;");
		parse("revoke select on mysql.* from test1;");
		parse("revoke update on mysql.* from test1;");
		parse("revoke select on mysql.host from test1;");
		parse("revoke update on mysql.host from test1;");
		parse("revoke select(host) on mysql.host from test1;");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 如果用户有table权限grant select, 不能够revoke global/db. 能够revoke table/column.
		parse("grant select on mysql.host to test@'%' with grant option;");
		parse("grant CREATE USER on *.* to test@'%'");
		grantDone();
		parse("revoke select on *.* from test1;");
		parse("revoke select on mysql.* from test1;");
		parse("revoke select on mysql.host from test1;");
		parse("revoke update on mysql.host from test1;");
		parse("revoke select(host) on mysql.host from test1;");
		parse("revoke update(host) on mysql.host from test1;");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 如果用户没有grant的global all权限, 不能够revoke global/db/table/column.
		parse("grant all on *.* to test@'%'");
		grantDone();
		parse("revoke select on *.* from test1;");
		parse("revoke select on mysql.* from test1;");
		parse("revoke select on mysql.host from test1;");
		parse("revoke select(host) on mysql.host from test1;");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
	}

	@Test
	public void testShowGrants() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("show grants");

		parse("create user test@'%' identified by '123456';");
		parse("show grants");
		parse("grant select,update on *.* to test@'%';");
		parse("show grants");
		parse("grant update on mysql.* to test@'%';");
		parse("show grants");
		parse("grant update on mysql.user to test@'%';");
		parse("show grants");
		parse("grant update(host) on mysql.user to test@'%';");
		parse("show grants");

		parse("show grants");
	}

	@Test
	public void testEnableUser() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("create user test@'%' identified by '123456';");
		parse("grant grant option, CREATE USER on *.* to test@'%';");
		parse("create user test1@'%' identified by '123456';");

		parse("revoke CONNECT from test1@'%';");
		parse("GRANT CONNECT on *.* to test1@'%'");
	}

	@Test
	public void testCreateUserBeforeOpenSwitch() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("create user test@'%' identified by '123456';");

		// 打开开关后，create user的账号没有任何权限
		parse( "select * from mysql.user");

		// 关闭开关后， create user的账号拥有所有权限
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("create user test@'%' identified by '123456';");
		parse("select * from mysql.user");
		parse("create user test2@'%' identified by '123456';");

		// 打开开关后，这个账号仍然拥有所有权限
		parse("select * from mysql.user");
		parse("create user test3@'%' identified by '123456';");
	}

	@Test
	public void testGrantUser() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("create user test@'%'");
		parse("grant all on *.* to test");

		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
		parse("create user test@'%'");
		parse("grant all on *.* to test");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 没有 create user 权限的用户，无法 通过 grant create user.
		parse("grant grant option, select on *.* to test");
		parse("grant select on *.* to test2");
		parse("grant create user on *.* to test");
		parse("grant select on *.* to test2");
	}

	@Test
	public void testGrantRevokeAvailable() throws Exception {
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 拥有all权限的用户，但是无法grant权限给其他用户
		parse("grant all on *.* to test@'%' identified by 'ccc';");
		parse("grant select on *.* to test1@'%';");
		parse("grant select on mysql.* to test1@'%';");
		parse("grant select on mysql.user to test1@'%';");
		parse("grant select(host) on mysql.user to test1@'%';");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 拥有select 权限的用户，可以grant select权限给所有的用户
		parse("grant select, CREATE USER on *.* to test@'%' identified by 'ccc' with grant option;");
		parse("grant select on *.* to test1@'%';");
		parse("grant select on mysql.* to test1@'%';");
		parse("grant select on mysql.user to test1@'%';");
		parse("grant select(host) on mysql.user to test1@'%';");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 拥有select 权限的用户，可以grant select权限给所有的用户的另一种写法, 也可以revoke
		parse("grant select, grant option, CREATE USER on *.* to test@'%' identified by 'ccc';");
		parse("grant select on *.* to test1@'%';");
		parse("grant select on mysql.* to test1@'%';");
		parse("grant select on mysql.user to test1@'%';");
		parse("grant select(host) on mysql.user to test1@'%';");
		parse("grant update on *.* to test1@'%';");
		parse("grant update on mysql.* to test1@'%';");
		parse("grant update on mysql.user to test1@'%';");
		parse("grant update(host) on mysql.user to test1@'%';");
		parse("revoke select on *.* from test1@'%';");
		parse("revoke select on mysql.* from test1@'%';");
		parse("revoke select on mysql.user from test1@'%';");
		parse("revoke select(host) on mysql.user from test1@'%';");
		parse("revoke update on *.* from test1@'%';");
		parse("revoke update on mysql.* from test1@'%';");
		parse("revoke update on mysql.user from test1@'%';");
		parse("revoke update(host) on mysql.user from test1@'%';");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");

		// 没有grant权限，无法给用户grant/revoke权限
		parse("grant select, CREATE USER on *.* to test@'%' identified by 'ccc';");
		parse("grant select on mysql.* to test1@'%';");
		parse("grant select on mysql.* to test1@'%' identified by 'ccc';");
		parse("revoke select on mysql.* from test1@'%';");

		// 拥有 all 权限的用户, 可以revoke all
		parse("grant all, grant option on *.* to test@'%' identified by 'ccc';");
		parse("grant all,grant option on *.* to test1@'%';");
		parse("revoke all on *.* from test1@'%';");
		parse("revoke grant option on *.* from test1@'%';");
		parse("drop user if exists 'test', if exists 'test1', if exists 'test2', if exists 'test3';");
	}
}