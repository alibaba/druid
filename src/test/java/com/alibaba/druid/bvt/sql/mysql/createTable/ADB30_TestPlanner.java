package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

@Ignore
public class ADB30_TestPlanner extends MysqlTest {



	@Test
	public void testCurXXX() {
		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  date_test < curdate() and date_test>=  '2018-01-01'");
		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  date_test < curtime() and date_test>=  '2018-01-01'");
		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  date_test < CURRENT_TIMESTAMP() and date_test>=  '2018-01-01'");
		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  date_test < now() and date_test>=  '2018-01-01'");

		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  time_test < curdate() and time_test>=  '2018-01-01'");
		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  time_test < curtime() and time_test>=  '2018-01-01'");
		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  time_test < CURRENT_TIMESTAMP() and time_test>=  '2018-01-01'");
		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  time_test < now() and time_test>=  '2018-01-01'");

		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  timestamp_test < curdate() and timestamp_test>=  '2018-01-01'");
		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  timestamp_test < curtime() and timestamp_test>=  '2018-01-01'");
		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  timestamp_test < CURRENT_TIMESTAMP() and timestamp_test>=  '2018-01-01'");
		parse("SELECT ((- 1) * B.id - 110000000) AS id_cb, B.* FROM test4dmp.test B WHERE  timestamp_test < now() and timestamp_test>=  '2018-01-01'");
	}

	@Test
	public void testInnerJoinEqualPredicateTransfer() {
		parse("select a.id from test4dmp.test a join test4dmp.test b on a.id=b.id and a.id=1");
		parse("select a.id from test4dmp.test a join test4dmp.test b on a.id=b.id  where a.id=1 and b.int_test<1");

		parse("select a.id from test4dmp.test a join test4dmp.test b on a.id=b.id  where a.int_test>1 and a.id in ( 2)");

		parse("select a.id from test4dmp.test a join test4dmp.test b on a.id=b.id  where a.id+1>long_test and a.id>3");

	}

	@Test
	public void testSelectSubqueryJoin() {
		// agg only, works
		parse("select  (select max(b.int_test) from test4dmp.test b where b.id<10 ) from test4dmp.test a");
		// join on select works
		parse("select  (select max(b.int_test) from test4dmp.test b, test4dmp.test c where b.id<10 and b.id=c.id ) from test4dmp.test a");

		try {
			// TODO join on select&from, failed
			parse("select  (select max(b.int_test) from test4dmp.test b where b.id<10 and b.id=a.id ) from test4dmp.test a");
			Assert.assertTrue(false);
		} catch (Throwable e) {
			Assert.assertTrue(true);
		}

	}

	@Test
	public void testAggValuesFolding() {
				parse("select sum(long_test), id from (select * from test4dmp.test where false ) t1 group by id");
	}

	@Test
	public void testWindowUnionAllPushdown() {
		parse("select * ,rank() over(partition by long_test order by int_test) as rk from ( " +
				"select id, long_test, int_test from test4dmp.test " +
				"union all " +
				"select id, long_test, int_test from test4dmp.test  " +
				") tt where id=100");
	}

	@Test
	public void testSimpleSqlNode() {
		parse("select 1, id from test4dmp.test where id = 1 and int_test > 4 limit 5");
		parse("select 1 from test4dmp.test as a where id = 1 and int_test > 4 limit 5");
		parse("select 1, id from test4dmp.test ");
		parse("select 1, id from test4dmp.test order by id");
//		parse("select id as a from test4dmp.test order by a")));
//		parse("select 1, sum(id) from test4dmp.test")));

		parse("select 1 from test4dmp.test b join test4dmp.test a where a.id = 1 and b.int_test > 4 limit 5");
		parse("select id from test4dmp.test as a where id = 1 and int_test > 4 group by id");
		parse("select 1, sum(id) from test4dmp.test group by 2 ");
		parse("select 1, id from test4dmp.test where id = 1 and int_test != 1 or int_test != 2");
		parse("select id as a from test4dmp.test union all select 'b' from test4dmp.test");
	}

	@Test
	public void testUserColumn() {
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where user in ('a')").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where id > 1 and user in ('a') ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where id > 1 or user in ('a') ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where id > 1 and user in ('a') or user in ('b') ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where user in ('a') and id > 1 and user in ('a') ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where user > '1' and user in ('a') ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where id > 1 and user in ('a') or id != id+1 ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where id > 1 and user in ('a','b','c') ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where 2 > 1 and user not in ('a') ").toString().contains("USER"));


		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where 2 > 1 and user not in ('a')  or user ='a' and id = 1 or user !='b'").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where 2 > 1 and user = '1' or user not in ('a') ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where user <> '455' or user <'p' or user  in ('a') ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where  lower(user) not in ('a') ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where id<1 and upper(user)  in ('a') ").toString().contains("USER"));
		Assert.assertTrue(!parse("select user as aa from test4dmp.test_user where  upper(user)  in ('a') and upper(`user`)='A'  ").toString().contains("USER"));

	}

	@Test
	public void testRtIndexAllDdl() {
		parse("select 1 as rt_index_all, 1 as rt_key");
	}

	@Test
	public void testParserRdslogTableDdl() {
		parse("select 1 as text");

		parse("CREATE TABLE test4dmp.rds_logs (\n" +
				"    ins_ip varchar(39) NOT NULL,\n" +
				"    ins_port int(11) NOT NULL,\n" +
				"    cid int(11) DEFAULT NULL, // will have default value like '-1' or '0'\n" +
				"    tid int(11) DEFAULT NULL,\n" +
				"    ts timestamp NOT NULL,\n" +
				"    origin_time bigint(20) DEFAULT NULL, // us timestamp\n" +
				"    user_ip varchar(39) DEFAULT NULL,\n" +
				"    user varchar(32) DEFAULT NULL,\n" +
				"    db varchar(32) DEFAULT NULL,\n" +
				"    fail varchar(32) DEFAULT NULL,\n" +
				"    latency bigint(20) DEFAULT NULL,\n" +
				"    return_rows bigint(20) DEFAULT NULL,\n" +
				"    update_rows bigint(20) DEFAULT NULL,\n" +
				"    check_rows bigint(20) DEFAULT NULL,\n" +
				"    isbind int(11) DEFAULT NULL,\n" +
				"    s_hash bigint(20) DEFAULT NULL,\n" +
				"    log MULTIVALUE nlp_tokenizer 'ik' value_type 'varchar', // max size 100KB, will be truncated\n" +
				"    ins_name varchar(32) DEFAULT NULL, // if cid is default value, this col will be queried\n" +
				"    db_type varchar(32) DEFAULT NULL,\n" +
				"    extension text DEFAULT NULL, // kv json structure not quried\n" +
				"    key ins_ip_idx(ins_ip),\n" +
				"    key ins_port_idx(ins_port),\n" +
				"    key cid_idx(cid),\n" +
				"    key tid_idx(tid),\n" +
				"    key ts_idx(ts),\n" +
				"    key origin_time_idx(origin_time),\n" +
				"    key user_ip_idx(user_ip),\n" +
				"    key user_idx(user),\n" +
				"    key db_idx(db),\n" +
				"    key fail_idx(fail),\n" +
				"    key latency_idx(latency),\n" +
				"    key return_rows_idx(return_rows),\n" +
				"    key update_rows_idx(update_rows),\n" +
				"    key check_rows_idx(check_rows),\n" +
				"    key isbind_idx(isbind),\n" +
				"    key s_hash_idx(s_hash),\n" +
				"    key ins_name_idx(ins_name),\n" +
				"    key db_type_idx(db_type)\n" +
				")\n" +
				"DISTRIBUTE BY HASH(cid)\n" +
				"PARTITION BY VALUE(cid) LIFECYCLE -1\n" +
//				"SUBPARTITION BY VALUE(YEARMONTHDAY(ts)) LIFECYCLE -1\n" +
				"ARCHIVE BY OSS\n" +
				"engine='CSTORE'");

		parse("CREATE TABLE test4dmp.rds_logs2 (\n" +
				"    ins_ip varchar(39) NOT NULL,\n" +
				"    ins_port int(11) NOT NULL,\n" +
				"    cid int(11) DEFAULT NULL, // will have default value like '-1' or '0'\n" +
				"    tid int(11) DEFAULT NULL,\n" +
				"    ts timestamp NOT NULL \n" +
				")\n" +
				"index_all='Y' \n" +
				"DISTRIBUTE BY HASH(cid)\n" +
				"PARTITION BY VALUE(cid) \n" +
//				"SUBPARTITION BY VALUE(YEARMONTHDAY(ts)) \n" +
				"ARCHIVE BY OSS\n" +
				"engine='CSTORE'");
	}

	@Test
	public void testDistributedBy() {

		String sql = "CREATE TABLE test4dmp.rds_logs (\n" +
				"    key ins_name_idx(ins_name),\n" +
				"    key db_type_idx(db_type)\n" +
				")\n" +
				"DISTRIBUTE BY HASH(cid)\n" +
				"PARTITION BY VALUE(cid) LIFECYCLE -1\n" +
				"ARCHIVE BY OSS\n" +
				"engine='CSTORE'";

		SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

		String formatSQL = SQLUtils.toMySqlString(stmt, VisitorFeature.OutputDistributedLiteralInCreateTableStmt);

		assertEquals("create table test4dmp.rds_logs ( key ins_name_idx (ins_name), key db_type_idx (db_type) ) engine = 'CSTORE' distributed by hash(cid) partition by value (cid) lifecycle -1 archive by = OSS",
				formatSQL);


		assertEquals("CREATE TABLE test4dmp.rds_logs (\n" +
						"\tKEY ins_name_idx (ins_name),\n" +
						"\tKEY db_type_idx (db_type)\n" +
						") ENGINE = 'CSTORE'\n" +
						"DISTRIBUTE BY HASH(cid)\n" +
						"PARTITION BY VALUE (cid) LIFECYCLE -1\n" +
						"ARCHIVE BY = OSS",
				stmt.toString());

		assertEquals("CREATE TABLE test4dmp.rds_logs (\n" +
						"\tKEY ins_name_idx (ins_name),\n" +
						"\tKEY db_type_idx (db_type)\n" +
						") ENGINE = 'CSTORE'\n" +
						"DISTRIBUTED BY HASH(cid)\n" +
						"PARTITION BY VALUE (cid) LIFECYCLE -1\n" +
						"ARCHIVE BY = OSS",
				stmt.toString(VisitorFeature.OutputDistributedLiteralInCreateTableStmt));
	}

	@Test
	public void testArchiveTable() {
		parse("select 1 as archive");

		parse("archive table test4dmp.test");

		parse("archive table test4dmp.test  3:1");

		parse("archive table test4dmp.test ");

		parse("archive table test4dmp.test   1:100,2:2,3:3");
	}

	@Test
	public void testAlterTableDropSubpartition() {

		parse("alter table test4dmp.test  drop subpartition 1:1");

		parse("alter table test4dmp.test drop subpartition 3:100,1:2, 10: 1");
	}

	@Ignore
	public void testAlterTableDropPartition() {

		// old
		parseTrue("truncate table test4dmp.test  partition 1,2","TRUNCATE TABLE test4dmp.test PARTITION 1, 2");

		// old all
		parseTrue("truncate table test4dmp.test  partition all","TRUNCATE TABLE test4dmp.test PARTITION ALL");

		// new
		parseTrue("alter table test4dmp.test  drop partition 1,2",
				"ALTER TABLE test4dmp.test\n" +
				"\tDROP PARTITION 1,2");
	}

	@Ignore
	public void testAlterTableLifecycle() {

		// old
		parse("alter table test4dmp.test   partitions 100");

		// new
		parse("alter table test4dmp.test   partition lifecycle 100");

		// new for subpartition lifecycle
		parse("alter table test4dmp.test   subpartition lifecycle 100:1,101:2");
	}

	@Test
	public void testBuildTableWithSplit() {
		parse("select 1 as split");
		parse("build table test4dmp.test");
		 parse("build table test4dmp.test with split");
		 parse("build table test4dmp.test version=1");
		 parse("build table test4dmp.test version=1 with   split");
	}

	@Test
	public void testSubpartitionAndArchive() {
		parse("select 1 as ARCHIVE, 1 as oss");
	}

	@Test
	public void testPredicateSimplify() {
		parse("select 1 from test4dmp.test where date(date_test)='2018-01-29'");
	}

	@Test
	public void testResultKeyword() {
		parse("select 1 as result");
	}

	@Test
	public void testRoundReturnType() {
		parse("select round(1), round(1,1)");
	}

	private void checkDateFolded(String s) {
		Assert.assertTrue(!s.contains("PLUS"));
		Assert.assertTrue(!s.contains("MINUS"));
		Assert.assertTrue(s.contains("-"));
	}

	private void checkTimeFolded(String s) {
		Assert.assertTrue(!s.contains("PLUS"));
		Assert.assertTrue(!s.contains("MINUS"));
		Assert.assertTrue(s.contains(":"));
	}

	private void checkTimestampFolded(String s) {
		Assert.assertTrue(!s.contains("PLUS"));
		Assert.assertTrue(!s.contains("MINUS"));
		Assert.assertTrue(s.contains("-"));
		Assert.assertTrue(s.contains(":"));
	}

	@Test
	public void testInstanceGroup() {
		parse("select 1 as INSTANCE_GROUP");

		parse("add INSTANCE_GROUP 1 REPLICATION = 3");
		parse("drop INSTANCE_GROUP 2,3" );
	}

	@Test
	public void testGroupingSets() {
		parse("select id from test4dmp.test group by grouping sets((int_test), (id), (int_test,id))");
	}

	@Test
	public void testNestedConstantFolding() {
		parse("select curdate(), curtime(), now()");
		parse("select curdate(),subdate(curdate(), 1)");
		parse("select subdate(curdate(), 1), curdate()");
		parse("select subdate(curdate(), 1), curtime()");

		parse("select date '2015-01-14' + interval '10' month");

		parse("select count(*) from test4dmp.test a join test4dmp.test b on a.id=b.id where 1=0");

		parse("select asin(1), asin(1.0)");

		parse("SELECT CASE 1 WHEN 1 THEN 'one' WHEN 2 THEN 'two' ELSE 'more' END");
	}

	@Test
	public void testViewWithDuplicatedColumnName() {
		parse(
				"create view v1 as select a.id from test4dmp.test a left join test4dmp.test b on a.id=b.id ");
		 parse(
				"create view v1 as select * from test4dmp.test a left join test4dmp.test b on a.id=b.id ");
	}

	@Test
	public void testFilterPushdownDeep() {
		parse(
				"SELECT\n" + "  o.id ,\n" + "  o.int_test,\n" + "  w.string_test ,\n" + "  i.float_test\n" +
				"FROM (\n" +
						"  SELECT\n" + "    id,\n" + "    int_test,\n" + "    long_test,\n" +
						"    SUM(out_time - create_time) / COUNT(1) / 1000 / 3600 AS order_avg_process_hour\n" +
						"  FROM (\n" +
						"    SELECT\n" + "      id,\n" + "      int_test,\n" + "      long_test,\n" + "      string_test,\n" +
						"      MAX(if(id = 3, timestamp_test, NULL)) AS create_time,\n" + "      MAX(if(id IN (7, 10, 14), timestamp_test, NULL)) AS out_time\n" +
						"  FROM test4dmp.test\n" + "  WHERE id IN (3, 7, 10, 14)\n" +
						"  GROUP BY id, int_test, long_test, string_test\n" +
						"  HAVING SUM(if(id IN (3, 7, 10, 14), 1, 0)) = 2\n" + "  ) o\n" +
						"  GROUP BY id, int_test, long_test\n" + ") o\n" +
						"JOIN test4dmp.test i ON i.long_test = o.long_test\n" +
						"JOIN test4dmp.test w ON w.int_test = o.int_test\n" +
						"WHERE w.long_test IS not NULL AND o.int_test = '11'\n" + "LIMIT 0, 10");
	}

	@Test
	public void testMongoAuditLog() {
		parse("select MONGO_AUDIT_LOG_INDEX_RECOMMEND(string_test) from test4dmp.test");
	}

	@Test
	public void testCC() {
		parse("select connected_components(id, long_test, 10) from test4dmp.test");
	}

	@Test
	public void testPageRank() {
		parse("select page_rank(id, long_test, float_test) from test4dmp.test");
		parse("select page_rank(id, long_test, float_test, 5) from test4dmp.test");
		parse("select page_rank(id, long_test, float_test, 5, 0.85) from test4dmp.test");

		parse("select gsa_page_rank(id, long_test, float_test) from test4dmp.test");
		parse("select gsa_page_rank(id, long_test, float_test, 5) from test4dmp.test");
		parse("select gsa_page_rank(id, long_test, float_test, 5, 0.85) from test4dmp.test");
	}

	@Test
	public void testCreateCStoreTableWithDistKeyInorder() {
		System.out.println(parse("CREATE TABLE `user_vcpu_level` (\n" +
				"  `id` bigint NOT NULL AUTO_INCREMENT,\n" +
				"  `ali_uid` bigint  DEFAULT '0',\n" +
				"  `vcpu_level` varchar DEFAULT NULL,\n" +
				"  `ds` varchar DEFAULT NULL,\n" +
				"  PRIMARY KEY (`id`)\n" +
				") engine='ANALYSIS' index_all='Y' distribute by hash(id)").toString());
	}

	@Test
	public void testCreateEvent() {
		parse("select 1 as event, 1 as SCHEDULE, 1 as at, 1 as every, 1 as `do`, 1 as starts");

		System.out.println(parse("CREATE EVENT `test4dmp`.`e1` "
				+ "ON SCHEDULE "
				+ "    AT '2017-01-01 11:21:21' "
				+ "DO "
				+ "  delete from test4dmp.test where 1<id").toString());

		System.out.println(parse("CREATE EVENT `e2` "
				+ "ON SCHEDULE "
				+ " EVERY 1 HOUR "
				+ "COMMENT 'abc' "
				+ "DO "
				+ "  select id from test4dmp.test limit 1").toString());

		System.out.println(parse("CREATE EVENT `e3` "
				+ "ON SCHEDULE "
				+ " EVERY 10 minute "
				+ "DO "
				+ "  alter table test4dmp.test add column col11 bigint").toString());

		System.out.println(parse("CREATE EVENT `e4` "
				+ "ON SCHEDULE "
				+ " EVERY 10 year "
				+ "DO "
				+ "  drop table test").toString());

		System.out.println(parse("CREATE EVENT `e5` "
				+ "ON SCHEDULE "
				+ " EVERY 13 month "
				+ "DO "
				+ "  flush table test4dmp.test").toString());

		System.out.println(parse("CREATE EVENT `e6` "
				+ "ON SCHEDULE "
				+ " EVERY 12 second "
				+ "DO "
				+ "  build table test4dmp.test").toString());

		System.out.println(parse("CREATE EVENT `e7` "
				+ "ON SCHEDULE "
				+ " EVERY 1 day starts '2018-12-12 01:00:00' "
				+ "DO "
				+ "  insert overwrite into test4dmp.test select * from test4dmp.test").toString());

		System.out.println(parse("CREATE EVENT `e8` "
				+ "ON SCHEDULE "
				+ " EVERY 2 minute "
				+ "DO "
				+ "  replace into test4dmp.test(id) select id from test4dmp.test").toString());


		System.out.println(parse("create event custins1675_daily\n"
				+ "  on schedule\n"
				+ "  every 5 minute\n"
				+ "do\n"
				+ "insert into custins1675_oss_dump_table\n"
				+ "select log, db, tid, `user`, user_ip, sql_type, fail, check_rows, update_rows, latency, ts\n"
				+ "from\n"
				+ "  rds_logs\n"
				+ "where cid = 1675 and ts >= date(subdate(now(), 1)) and ts <= CURDATE()").toString());
	}

	@Test
	public void testDropEvent() {
		parse("drop event test4dmp.e1");
		parse("drop event if exists e1");
	}

	@Test
	public void testShowEvent() {
		parse("select 1 as events, 1 as tables");

		parse("show create event e1");
		parse("show create event test4dmp.e1");

		parse("show events");
		parse("show events from test4dmp");
	}

	@Test
	public void testDatetimeFunctionFolding() {
		parse("select str_to_date('20171215', '%Y%m%d') from test4dmp.test");
		parse("select year('2017-12-15'),date('2015-01-01 11:21:22') from test4dmp.test");
	}

	@Test
	public void testColumnNameWithoutTblName() {
		 parse("select test.id, id, id as id, test.id as id from test4dmp.test") ;

		parse("select a.id from test4dmp.test as a group by a.id order by 1 ");

		parse("select a.id from test4dmp.test a join test4dmp.test  ");

		parse("select test.id from test4dmp.test a join test4dmp.test  ");
	}

	@Test
	public void testShowFullTable() {
		parse("show full tables from test4dmp");
		parse("show full tables from test4dmp like '%t%'");
		parse("show full tables from test4dmp where Table_type != 'VIEW'");

		parse("show tables from test4dmp");
		parse("show tables from test4dmp like '%t%'");
		parse("show tables from test4dmp where Table_type != 'VIEW'");
	}

	@Test
	public void testAdKeyWithDollar() {
		parse("alter table test4dmp.test add column `a$b` bigint");
		parse("alter table test4dmp.test add key `a$b_idx`(`a$b`)");
		parse("alter table `test4dmp`.`test` add key `a$b_idx`(`a$b`)");
	}

	@Test
	public void testDelete() {
		parse("delete from test4dmp.test where id < 10 and int_test != 100");
		parse("delete from test4dmp.test");
		parse("delete from test4dmp.test where 1=2");
	}

	@Test
	public void testUpdate() {
		parse("update test4dmp.test set id = 5");
		parse("update test4dmp.test set id = 2*id");
		parse("update test4dmp.test set id = 1 where false");

		parse("update test4dmp.test set id = 5, int_test=2 where id < 10 and int_test != 100");
		parse("update test4dmp.test set id = id+5 where int_test != 100");

		parse("update test4dmp.test set string_test=default, int_test=1, long_test=2, date_test='20170101' where long_test between 1 and 2000");

		parse("update test4dmp.test set int_test = 1 where id=2 limit 1");
		try {
			parse("update test4dmp.test set int_test = 1 where id=2 limit 2");
			Assert.assertTrue(false);
		} catch (Throwable e) {
			Assert.assertTrue(true);
		}

		System.out.println(parse("update test4dmp.test set date_test ='20180101' where id=5"));
	}

	@Test
	public void testShowProcedureStatus() {
		parse("show procedure status");
		parse("show procedure status where db= 'tt'");
		parse("show procedure status like 'abv'");
	}

	@Test
	public void testShowFunctionStatus() {
		parse("show function status");
		parse("show function status where db= 'tt'");
		parse("show function status like 'abv'");
	}

	@Test
	public void testDropProcedure() {
		parse("drop procedure if exists test4dmp.p1");
		parse("drop procedure p2");
	}

	@Test
	public void testCreateProcedure() {
		parse("select 1 as LANGUAGE, 1 as `CONTAINS`, 1 as SECURITY,  1 as DEFINER");

		System.out.println(parse(
				"CREATE PROCEDURE `test4dmp`.`v2`()\n" +
						"LANGUAGE SQL\n" +
						"NOT DETERMINISTIC\n" +
						"CONTAINS SQL\n" +
						"SQL SECURITY DEFINER\n" +
						"COMMENT 'abc'\n" +
						"select id from test4dmp.test limit 12").toString());

		System.out.println(parse(
				"CREATE PROCEDURE `test4dmp`.`v2`()\n" +
						"NOT DETERMINISTIC\n" +
						"CONTAINS SQL\n" +
						"SQL SECURITY DEFINER\n" +
						"COMMENT 'abc'\n" +
						"select id from test4dmp.test limit 12").toString());

		System.out.println(parse(
				"CREATE PROCEDURE `test4dmp`.`v2`()\n" +
						"CONTAINS SQL\n" +
						"SQL SECURITY DEFINER\n" +
						"COMMENT 'abc'\n" +
						"select id from test4dmp.test limit 12").toString());

		System.out.println(parse(
				"CREATE PROCEDURE `test4dmp`.`v2`()\n" +
						"SQL SECURITY DEFINER\n" +
						"COMMENT 'abc'\n" +
						"select id from test4dmp.test limit 12").toString());

		System.out.println(parse(
				"CREATE PROCEDURE `test4dmp`.`v2`()\n" +
						"COMMENT ''\n" +
						"select id from test4dmp.test limit 12").toString());

		System.out.println(parse(
				"CREATE PROCEDURE `test4dmp`.`v2`()\n" +
						"select id from test4dmp.test limit 12").toString());
	}

	@Test
	public void testRollup() {
		parse("select id from test4dmp.test group by int_test, id with rollup");
		parse("select id from test4dmp.test group by rollup(int_test, id)");
	}

	@Test
	public void testShowTriggers() {
		parse("show triggers");
		parse("select 1 as triggers");
	}

	@Test
	public void testCreateBlob() {
		parse("create table t_blob(a int, b blob, c bytes) DISTRIBUTE BY HASH(a) engine='ANALYSIS' INDEX_ALL='Y'");
	}

	@Test
	public void testShowGlobalStatus() {
		parse("show status");
		parse("show global status");
		parse("show global status where 2>1 ");
		parse("show global status where Variable_name regexp 'xxxx'");
	}

	@Test
	public void testCastLargeString() {
		parse("select id from test4dmp.test where string_test='abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabc'");
	}

	@Test
	public void testGeo() {
		parse("select id from test4dmp.test where GEO_IN_CIRCLE(id, '24,143', 120)");
		parse("select id from test4dmp.test where GEO_IN_POLYGON(id, '24 143,1 1,2 2,1 1,1 1,1 1,1 1,1 1')");
		parse("select id from test4dmp.test where GEO_IN_POINTS(id, '24 143')");
	}

	@Test
	public void testMD5() {
		parse("select md5(null), md5('hello'), md5(1), md5('')");
	}

	@Test
	public void testTruncateTable() {
		System.out.println(parse("truncate table test4dmp.test").toString());
		System.out.println(parse("truncate table test4dmp.test partition all").toString());
		System.out.println(parse("truncate table test4dmp.test partition 100000000").toString());
		System.out.println(parse("truncate table test4dmp.test partition 1,2,3").toString());
		try {
			System.out.println(parse("truncate partition test4dmp.test 100").toString());
			Assert.assertTrue(false);
		} catch (Throwable e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testSubmitJob() {
		parse("select 1 as submit, 1 as job, 1 as await, 1 as cancel");

		parse("submit job select 1");

		parse("submit job insert into test4dmp.test select * /* id */, id from test4dmp.test");

		parse("show job status");
		parse("show job status where job='abc'");
		parse("show job status where job_id='abc'");

		parse("submit job await select 1");

		parse("cancel job '1'");

		parse("submit job truncate table test4dmp.test ");
		parse("submit job truncate table test partition 3");
		parse("submit job truncate table test4dmp.test partition 3,4,5");
		parse("submit job truncate table test4dmp.test partition all");
	}

	@Test
	public void testBuildedFunc() {
		parse("select * from test4dmp.test where id=MAX_PARTITION()");
	}

	@Test
	public void testView() {
		parse("create view v1 as select * from test4dmp.test ");
		parse("create view test4dmp.v1 as select * from test4dmp.test ");
		parse("drop view test4dmp.v1");
		parse("drop view if exists v1, test4dmp.v1, v1");

		parse("create SQL SECURITY DEFINER view v2 as select * from test4dmp.test ");

		parse("create view or replace v1 as select * from test4dmp.test ");
		parse("create or replace view v1 as select * from test4dmp.test ");
	}

	@Test
	public void testWith() {
		parse("with a as ( select * from test4dmp.test ) select * from a ");
	}

	@Test
	public void testMultiValueJoinPlan() {
		parse("select count(*), ref(a.multivalue_test, 1) from test4dmp.test a join test4dmp.test b on a.id=b.id  where ref(a.multivalue_test, 1) < 1 group by ref(a.multivalue_test, 1) ");
		parse("select count(*), ref(a.multivalue_test, 1) from test4dmp.test a join test4dmp.test b join test4dmp.test c on a.id=b.id and c.id=b.id  where ref(a.multivalue_test, 1) < 1 group by ref(a.multivalue_test, 1) ");
	}

	@Test
	public void testMultiValuePlan() {
		parse("select id, ref(multivalue_test, 0) from test4dmp.test where ref(multivalue_test, 1) > 1");
		parse("select sum(id), ref(multivalue_test, 0) from test4dmp.test where ref(multivalue_test, 1) > 1 group by ref(multivalue_test, 0)");
	}

	@Test
	public void testSingleLineComment() {
		parse("select 1, // abd \n 2 -- aadf");
		parse("select 1, /*abd*/ \n 2 /* aadf*/");
		parse("select 1, # abd \n 2 # aadf");
	}

	@Ignore
	public void testAnalyzeExtend() {
		parse("select analyze id, long_test from test4dmp.test");
		parse("select analyze_with_hint 'mode=dag' id, long_test from test4dmp.test");
		parse("/*mode=dag*/ select analyze_with_hint 'k1=v1, k2=v2' id, long_test from test4dmp.test");
	}

	@Test
	public void testConnectBy() {
		parse("select id, long_test from test4dmp.test start with id < 10 connect by prior id = long_test");
		parse("select id, string_test, long_test from test4dmp.test start with id < 10 connect by prior id = long_test");
	}

	@Test
	public void testQuoting2() {
		parse("select 'a\"b'");
		parse("select 'a''b' from test4dmp.test where string_test like 'a''b'");
		parse("select 'a\\[b'");
	}

	@Test
	public void testAggBug() {
		parse("select id, sum(int_test)*float_test from test4dmp.test group by id");
		parse("select id, sum(int_test)*float_test as a from test4dmp.test group by id");
	}

	@Test
	public void testCountNonReservedKeyword() {
		parse("select 1 as count");
	}

	@Test
	public void testCaseWhenMultiTypes() {
		parse("select concat(case when month('2014-02-21') = 1 then year('2014-01-01') else concat('0', 'a') end) as t");
		parse("select concat(case when month('2014-02-21') = 1 then concat('0', 'a') else year('2014-01-01') end) as t");
	}

	@Test
	public void testIntervalNumber() {
		parse("select '2014-02-12' - interval 2 month");
		parse("select '2014-02-12' + interval 2 month");
		parse("select date '2014-02-12' + interval 2 month");
		parse("select date '2014-02-12' + interval '2' month");
	}

	@Test
	public void testUnion() {
		parse("select * from ((select id, string_test from test4dmp.test where id < 20) union (select id , string_test from test4dmp.test where id > 1)) p order by 1 asc, 2 desc limit 0, 10");
		parse("select * from ((select id, string_test from test4dmp.test where id < 20) union all (select id , string_test from test4dmp.test where id > 1)) p order by 1 asc, 2 desc limit 0, 10");
	}

	@Test
	public void testInsertOverwriteTable() {
		parse("insert overwrite into test4dmp.test321  " +
				"select id, string_test from test4dmp.test ");
		parse("insert into test4dmp.test321  " +
				"select id, string_test from test4dmp.test ");
		parse("insert into test4dmp.test321 (id123, string_test123) " +
				"select id, string_test from test4dmp.test ");
		parse("insert into test4dmp.test321 (string_test123, id123) " +
				"select string_test, 1 from test4dmp.test ");
		parse("insert overwrite into test4dmp.test321 (string_test123, id123) " +
				"select 'aaa', id from test4dmp.test ");

		parse("replace into test4dmp.test321 (string_test123, id123) " +
				"select string_test, 1 from test4dmp.test ");

	}

	@Test
	public void testStraightJoin() {
		parse("SELECT * from test4dmp.test a STRAIGHT_JOIN test4dmp.test b on a.id=b.id");
		parse("SELECT * from test4dmp.test a JOIN test4dmp.test b on a.id=b.id");
		parse("SELECT * from test4dmp.test a inner join test4dmp.test b on a.id=b.id");
		parse("SELECT * from test4dmp.test a right join test4dmp.test b on a.id=b.id");
		parse("SELECT * from test4dmp.test a left outer join test4dmp.test b on a.id=b.id");
	}

	@Test
	public void testTrim() {
		parse("SELECT trim(null), trim(''), trim(' '),trim('y' FROM 'xxxbarxxx') from test4dmp.test");
	}

	@Test
	public void testValidHavingWithoutGroupBy() {
		parse("select id from test4dmp.test group by 1 having id < 10 order by id");
		parse("select id from test4dmp.test  having sum(id) < 10 order by id");
		parse("select id from test4dmp.test  having max(id) < 10 order by id");
	}

	@Test
	public void testGroupByAscDesc() {
		parse("select id, count(*) from test4dmp.test group by id");
		parse("select id, count(*) from test4dmp.test group by id asc");
		parse("select id, count(*) from test4dmp.test group by id desc");
		parse("select id, int_test, count(*) from test4dmp.test group by id desc, int_test order by int_test");
	}

	@Test
	public void testGroupByNonAggColumns() {
		parse("select id, int_test from test4dmp.test group by id");
		parse("select * from test4dmp.test group by id");
		parse("select sum(id), substring(string_test, 1,4) from test4dmp.test group by id");
		parse("select sum(int_test), substring(string_test, 1,4) from test4dmp.test");
		parse("select * from ( select string_test , max(id) as id from test4dmp.test) t");
		parse("select * from ( select string_test x , max(id) as id from test4dmp.test) t");
	}

	@Test
	public void testIsNull() {
		parse("select id from test4dmp.test where isnull(id)");
	}

	@Test
	public void testRankKeyword() {
		parse("select id as rank, id as rank, 1 as rank, '' as rank from test4dmp.test where long_test > 990 group by id order by 1");
	}

	@Test
	public void testGroupByAggPushDown() {
		parse("select id, count(*) from test4dmp.test where long_test > 990 group by id");
	}

	// Note: will cause exception for codegen: Integer.valueOf(inp0_.stringValue())
	@Test
	public void testSumText() {
		parse("select sum(cast(string_test as integer)) from test4dmp.test where id > 10 && id < 100");
	}

	@Test
	public void testAs() {
		parse("select 1 as 'a f', 1 as \"a nib b\", 1 as `a ` ");
	}

	@Test
	public void testQuoting() {
		parse("select 'a', \"a\" , avg(`id`) as `x y z ` from test4dmp.test where string_test = \"abi\" and date_test > \"2012-12-22 22:22:01\" group by id having `x y z ` > \"10\" ");
	}

	@Test
	public void testDateTimeKeywordAsColName() {
		parse("select time '10:11:11', time('11:21:12'), time from test4dmp.test");
		parse("select timestamp '2012-12-12 10:11:11', timestamp('2000-01-01 11:21:12'), timestamp from test4dmp.test");
		parse("select date '2901-12-22', date('1999-04-21'), date from test4dmp.test");

		parse("select time '10:11:11', time('11:21:12') from test4dmp.test where time='11:12:00' limit 7");
		parse("select timestamp '2012-12-12 10:11:11', timestamp('2000-01-01 11:21:12') from test4dmp.test where timestamp > timestamp '1998-01-01 01:01:00' ");
		parse("select date '2901-12-22', date('1999-04-21') from test4dmp.test where '2000-01-01' > date ");
	}


	@Test
	public void testTableNameStartWithNumber() {
		parse("SELECT 1 from test4dmp.12_12test where 1=1");
		parse("SELECT 1 from test4dmp.1212test where 1=1" );
		parse("SELECT 1 from test4dmp.test12 where 1=1" );
//		parse("SELECT 1 from test4dmp.12 where 1=1", "test4dmp.12"); Do not support
	}

	@Test
	public void testWhereBinaryOp() {
		parse("select id from test4dmp.test where id > 10 && id < 100");
		parse("select id from test4dmp.test where id > 10 XOR id < 100");
		parse("select id from test4dmp.test where !(id < 10)");
	}

	@Test
	public void testGroupByPosition() {
		parse("select id, count(*) from test4dmp.test group by 1 order by 1 limit 10");
		parse("select 1, count(*) from test4dmp.test group by 1 order by 1 limit 10");
		parse("select 1 as c, count(*) from test4dmp.test group by 1 order by 1 limit 10");

			parse("select id, count(*) from test4dmp.test group by 2 order by 1 limit 10");

			parse("select id, count(*) from test4dmp.test group by 3 order by 1 limit 10");
	}

	@Test
	public void testTableNameStartWithNumber_dml() {
		parse("select id123, test321.id123, 1, count(*) from test4dmp.test321 where id123 > 10 group by id123");
	}

	@Test
	public void testLimitOffset() {
		parse("select id from test4dmp.test order by 1 limit 1, 20");
		parse("select id from test4dmp.test order by 1 limit 1 OFFSET 20");
	}

	@Test
	public void testSelectNull() {
		parse("SELECT null");
		parse("select count(*) from (select null) as T");
	}

	// TODO plan not right
	@Test
	public void testInOrNotIn() {
		parse("select id,date_format(date_test,'%W:%Y')from test4dmp.test where (date_format(date_test,'%W')  in (select date_format(date_test,'%W') from test4dmp.test where id<=20 )) or  (date_format(date_test,'%Y')  not in (select date_format(date_test,'%Y') from test4dmp.test where id <=1)) order by 1");
		parse("select count(*) from test4dmp.test where (int_test in (select int_test from test4dmp.test where id<=20 )) or  (long_test  not in (select long_test from test4dmp.test where id <=1))");
	}

	@Test
	public void testOrderByFunction() {
		parse(" select date_format(date_test,'%W') from test4dmp.test order by date_format(date_test,'%W')");
		parse(" select distinct date_format(date_test,'%W'),date_format(date_test,'%D') from test4dmp.test order by date_format(date_test,'%D')");
		parse(" select date_format(date_test,'%W') from test4dmp.test order by date_format(date_test,'%D')");
		// TODO
//		parse(" select distinct date_format(date_test,'%W') from test4dmp.test order by date_format(date_test,'%D')");
	}

	@Test
	public void testNotExistEmptySubquery() {
		parse("select * from test4dmp.test where not exists (select a.id from test4dmp.test a where id<-1) order by 1 limit 10");
	}

	public void testUserTable() {
		parse("SELECT user, host from test4dmp.user");
	}

	@Test
	public void testGetFormat() {
		parse("SELECT GET_FORMAT(DATE, 'USA'),GET_FORMAT(DATETIME, 'JIS'),GET_FORMAT(TIME, 'ISO')");
	}

	public void testGetFormat2() {
		parse("SELECT GET_FORMAT(DAE, 'USA')");
	}

	@Test
	public void testMppGroupByNotSupport() {
		parse("select max(string_test) from test4dmp.test where id = 1 group by int_test");
	}

	@Test
	public void testKeyWordAlias() {
		parse("select if(1>2, 2, 4), if(1>0, 2, 4), if(0, 1, 2), if(null, 1, 2)");
		parse("SELECT QUARTER('2008-03-01'), QUARTER('2008-04-01')");
		parse("SELECT date('2003-12-31 01:02:03'), date '2003-12-31', date(null)");
		parse("SELECT TIME('2003-12-31 01:02:03'), TIME('11:02:03')");
		parse("SELECT TIMESTAMP('2003-12-31'), TIMESTAMP('2003-12-31 01:12:12', '01:12:11')");
	}

	@Test
	public void testOLTPPoint() {
		parse("select int_test from test4dmp.test where id = 1");
	}

	@Test
	public void testOLTPRange() {
		parse("select int_test from test4dmp.test where id between 200 and 200 + 99");
	}

	@Test
	public void testOLTPSum() {
		parse("select sum(int_test) from test4dmp.test where id between 200 and 200 + 99");
	}

	@Test
	public void testOLTPOrder() {
		parse("select int_test from test4dmp.test where id between 200 and 200 + 99 order by int_test");
	}

	@Test
	public void testOLTPDistinct() {
		parse("select distinct int_test from test4dmp.test where id between 200 and 200 + 99 order by int_test");
	}

	@Test
	public void testNULLSubquery() {
		parse("select id, (select id from test4dmp.test where float_test=1.111) from test4dmp.test order by id limit 10");
	}

	@Test
	public void testGroupBySubquery() {
		parse("select id, (select id from test4dmp.test where id>=99 order by 1 limit 1)  as tmp_col,count(*)" +
				" from test4dmp.test a group by id, tmp_col order by tmp_col,1  limit 10");
	}

	@Test
	public void testJoinDupKeyColName() {
		parse("select * from test4dmp.test a join (select * from test4dmp.test b order by 1 limit 10) b on a.id=b.id order by 1");
	}

	@Test
	public void testJoinConditionWhere() {
		parse("select * from test4dmp.test a join test4dmp.test b where  a.id=b.id order by 1,2,3 limit 10");
	}

	@Test
	public void testStringSort() {
		List<String> strings = Lists.newArrayList("sad", "sde", "s_1");
		Collections.sort(strings);
		System.out.print(strings);
	}

//	@Test TODO `=` operator argumentMustBeScalar(1), so the right hand should be scalar.
	public void testRowSubquery() {
		parse("select id, int_test, boolean_test from test4dmp.test where (id, boolean_test) = " +
				"(select a.id, a.boolean_test from test4dmp.test a where a.id > 95 and a.int_test in (99) limit 1)");
	}

	@Test
	public void testRowSubquery1() {
		parse("select id, int_test, boolean_test from test4dmp.test where (id, boolean_test) in " +
				"(select a.id, a.boolean_test from test4dmp.test a where a.id > 95 and a.int_test in (99) limit 1)");
	}

	@Test
	public void testRow1() {
		parse("SELECT * FROM test4dmp.test WHERE ROW(1,2) = (2,2)");
	}

	@Test
	public void testSetOpTypeDeduce() {
		parse("select id ,timestamp_test from test4dmp.test union select id,date_test from test4dmp.test where id is not null order by 1,2 limit 50");
	}

	@Test
	public void testSetOpValidationChars() {
		parse("select CASE WHEN 1>0 THEN 'true' ELSE 'false' END union select CASE WHEN 1>0 THEN 'true1111' ELSE 'false2222222' END");
	}

	@Test
	public void testSetOpValidation5() {
		parse("select int_test,count(*) from (SELECT int_test from test4dmp.test where int_test BETWEEN 1 and 100 union  select int_test from test4dmp.test where long_test BETWEEN 5 and 120 )as x group by int_test order by int_test");
	}

	@Test
	public void testSetOpValidation4() {
		parse("SELECT short_test from test4dmp.test where int_test BETWEEN 1 and 10 union  select short_test from test4dmp.test where int_test BETWEEN 20 and 30 order by short_test");
	}

	@Test
	public void testSetOpValidation3() {
		parse("select a.id,concat(a.boolean_test,'_test') as new_name from test4dmp.test a union select b.int_test , concat(b.boolean_test,'_test') from test4dmp.test b where 1=0");
		parse("select a.id,concat(a.boolean_test,'_test') as new_name from test4dmp.test a union all select b.int_test , concat(b.boolean_test,'_test') from test4dmp.test b where 1=0");
	}

	@Test
	public void testSetOpValidation2() {
		parse("(select boolean_test from test4dmp.test ) union (select int_test from test4dmp.test )");
		parse("(select int_test from test4dmp.test ) union (select boolean_test from test4dmp.test )");
		parse("(select int_test, boolean_test from test4dmp.test ) union (select float_test, boolean_test from test4dmp.test )");

		parse("(select int_test from test4dmp.test ) union (select boolean_test from test4dmp.test ) limit 1");
		parse("(select boolean_test from test4dmp.test ) union (select int_test from test4dmp.test ) order by 1 limit 1");
	}

	@Test
	public void testSetOpValidation() {
		parse("(select id from test4dmp.test ) union (select int_test from test4dmp.test )");
	}

	@Test
	public void testScalarSubquery3() {
		parse("select * from test4dmp.test where exists (select 1) and id=1000");
	}

	@Test
	public void testScalarSubquery2() {
		parse("select (select id from test4dmp.test where id=1)");
	}

	@Test
	public void testScalarSubquery() {
		parse("select (select id from test4dmp.test where 1=0)");
	}

	@Test
	public void testCrossJoin2() {
		parse("select count(*) from test4dmp.test A cross join test4dmp.test B on A.id = B.id");
	}

	@Test
	public void testAggRemove2() {
		parse("select bit_and(int_test) from test4dmp.test where 1=0");
		parse("select sum(int_test) from test4dmp.test where 1=0");
	}

	@Test
	public void testBitAgg() {
		parse("select bit_and(int_test) from test4dmp.test where id in (1,2)");
		parse("select bit_or(int_test) from test4dmp.test where id in (1,2)");
		parse("select bit_xor(int_test) from test4dmp.test where id in (1,2)");
	}

	@Test
	public void testTimestampAdd() {
		parse("SELECT TIMESTAMPADD(MINUTE,1,'2003-01-02'), TIMESTAMPADD(SECOND,1,'2003-01-02')," +
				"TIMESTAMPADD(MICROSECOND,1,'2003-01-02'),TIMESTAMPADD(HOUR,1,'2003-01-02'),TIMESTAMPADD(DAY,1,'2003-01-02')," +
				"TIMESTAMPADD(WEEK,1,'2003-01-02'),TIMESTAMPADD(MONTH,1,'2003-01-02'),TIMESTAMPADD(QUARTER,1,'2003-01-02')," +
				"TIMESTAMPADD(YEAR,1,'2003-01-02')");
	}

	@Test
	public void testTimestampDiff() {
		parse("SELECT TIMESTAMPDIFF(MINUTE,'2003-02-01','2003-05-01 12:05:55')");
	}

	@Test
	public void testQuote() {
		parse("select QUOTE('Dont!'), QUOTE('D'), QUOTE(null)");
	}

	@Test
	public void testChar() {
		parse("SELECT CHAR(77,121,83,81,'76'), CHAR(77,77.3,'77.3'), CHAR(77,77.3,null), char(null), char('a'), char(0, -1), char(256), char(1, 255)");
	}

	@Test
	public void testGreatest() {
		parse("SELECT GREATEST(2, 0), GREATEST(34.0, 3.0, 5.0, 767.0), GREATEST('B', 'A', 'C'), GREATEST(34.0, 3.0, null, 767.0), GREATEST('B', null, 'C')");
	}

	@Test
	public void testNullSafeEqual() {
		parse("SELECT 1 <=> 1, NULL <=> NULL, 1 <=> NULL, 2 <=> 1");
	}

	@Test
	public void testConvert2() {
		parse("select convert('23', SIGNED)"); // TODO convert(1.234, decimal(10,2))
	}

	@Test
	public void testConvert() {
		parse("select convert('2013-12-30' using utf8)");
	}

	@Test
	public void testCaseWhen() {
		parse("SELECT CASE 1 WHEN 1 THEN 'one' WHEN 2 THEN 'two' ELSE 'more' END");
		parse("SELECT CASE WHEN 1>0 THEN 'true' ELSE 'false' END");
		parse("SELECT CASE 'c' WHEN 'operatorNullable' THEN 1 WHEN 'b' THEN 2 END");
	}

	@Test
	public void testCast() {
		parse("select cast(1 as integer), cast('2' as bigint),cast('3.0' as double) ");
		parse("select cast(1 as char), cast(2 as varchar)");
		parse("select cast('2013-12-30' as date), cast('2013-12-30' as timestamp)");
		parse("select cast(FORMAT(string_test,5) as double) from test4dmp.test");
	}

	@Test
	public void testGroupByAlias() {
		parse("select count(*), id+1 as B from test4dmp.test group by B limit 100");
	}

	@Test
	public void testHavingAlias() {
		parse("select t1.id, count(*) as A from test4dmp.test t1 inner join test4dmp.test t2 on t1.id = t2.int_test group by " +
				"t1.id having A >= 3 order by t1.id limit 100");
		parse("select t1.id, count(*) as A from test4dmp.test t1 inner join test4dmp.test t2 on t1.id = t2.int_test group by " +
				"t1.id having A*2 >= 3 order by t1.id limit 100");
	}

	@Test
	public void testGroupConcat() {
		parse("select group_concat(id order by id) from test4dmp.test where id > 9900 group by int_test");
		parse("select group_concat(distinct id order by id desc) from test4dmp.test group by id");
		parse("select group_concat(distinct int_test order by id) from test4dmp.test group by id");
		parse("select group_concat(distinct id order by id desc) from test4dmp.test group by id");
		parse("select group_concat(distinct id,int_test order by id desc) from test4dmp.test group by id");
		parse("select group_concat(id order by id desc) from test4dmp.test group by id");
		parse("select group_concat(distinct id order by int_test+1 desc) from test4dmp.test group by id");
		parse("select group_concat(id) from test4dmp.test group by id");
		parse("select group_concat(id SEPARATOR '|') from test4dmp.test group by id");
		parse("select group_concat(distinct id order by id SEPARATOR '') from test4dmp.test group by id");
	}

	@Test
	public void testBitOp() {
		parse("select 1 & 2, 's' & -0.0, '2' & 'fdfe', -1 & 0.0000");
		parse("select 1 | 2, 's' | -0.0, '2' | 'fdfe', -1 | 0.0000");
		parse("select 1 ^ 2, 's' ^ -0.0, '2' ^ 'fdfe', -1 ^ 0.0000");
		parse("select 1 << 2, -2 >> 12");
		parse("select  ~2, ~0.01, ~'dd'");
	}

	@Test // TODO temp table unnest
	public void testTempTable() {
		parse(
				"with t1 as (select id as aid from test4dmp.test where int_test > 1 order by id), " +
						"t2 as (select id as bid from test4dmp.test where int_test < 10 order by id) " +
						"select t1.aid from t1, t2, t2 as t3, (select id as aid from test4dmp.test where int_test > 1) t4 where t1.aid = t2.bid"
		);
	}

	@Test
	public void testSubQueryConverter() {
		parse("SELECT id FROM test4dmp.test WHERE id in (select id from test4dmp.test where id = 1)");
	}

	@Test
	public void testConditionReducing() {
		parse("SELECT max(-1.0) FROM test4dmp.test WHERE 1 = 0");
		parse("SELECT max(-1.0) FROM test4dmp.test WHERE 1 < 0");
	}

	@Test
	public void testCharCollation() {
		parse("SELECT CASE 1 WHEN 1 THEN 'one' WHEN 2 THEN 'two' ELSE 'more' END");
	}

	@Test
	public void testFormat() throws Exception {
		parse("select count(id) from test4dmp.test group by int_test limit 10");
	}

	@Test
	public void testAsColName() throws Exception {
		parse("select id as `a a`  from test4dmp.test");
	}

	@Test
	public void testAsKeyword() throws Exception {
		parse("select * from (SELECT 1) as schema"); // must in NonReservedKeyWord.
	}

	public void testDateCompute() throws Exception {
		parse("SELECT date '2008-12-31' + 1");
		parse("SELECT timestamp '2008-12-31 12:00:21' + 1");
		parse("SELECT time '12:00:21' + 1");
		parse("SELECT NOW() + 1");

		parse("SELECT date '2008-12-31' - 1");
		parse("SELECT timestamp '2008-12-31 12:00:21' - 1");
		parse("SELECT time '12:00:21' - 1");
		parse("SELECT NOW() - 1");
	}

	@Test
	public void testInterval() throws Exception {
		parse("SELECT date '2008-12-31' + INTERVAL '1' YEAR");
		parse("SELECT timestamp '2008-12-31 23:59:59' + INTERVAL '1' SECOND");
		parse("SELECT date '2008-12-31' - INTERVAL '1' YEAR");
	}

	@Test
	public void testIntervalWithCast() throws Exception {
//		parse("SELECT cast('2008-12-31 23:59:59' as date) + INTERVAL '30' day"); // TODO
		parse("SELECT cast ('1998-04-08' as date) - INTERVAL '1' month");
	}

	// TODO
//		@Test
	public void testIntervalInFunc() throws Exception {
		parse("SELECT DATE_ADD('2008-01-02', INTERVAL 31 DAY)");
	}

	@Test
	public void testExtract() throws Exception {
		parse("SELECT EXTRACT(YEAR FROM '2009-07-02')");
	}

	/**
	 * TODO rewrite
	 *
	 * @throws Exception
	 */
	@Test
	public void testCoalesce() throws Exception {
		parse("select * from test4dmp.test A left outer join test4dmp.test B on A.id = B.id where " +
				"coalesce(A.int_test, 0) > 0");
	}

	@Test
	public void testCrossJoin() throws Exception {
		parse("select count(*) from test4dmp.test A cross join test4dmp.test B");
	}

	@Test
	public void testArithmeticOperations() throws Exception {
		parse("select 1 % 3, -1, 1-2, 0-1, 2 MOD 5, 2+3, 9*10, 2/3, 2 DIV 3");
	}

	@Test
	public void testParserKeyword() throws Exception {
		parse("select insert('foobarbar', 5, 1, 'ab')");
	}

	@Test
	public void testParserSelectGroupByCol() throws Exception {
		parse("select count(id) from test4dmp.test group by int_test limit 10");
	}

	@Test
	public void testLike() throws Exception {
		parse("SELECT 'a' LIKE 'ae'");
		parse("SELECT 'David!' LIKE 'David_'");
		parse("SELECT 'David!' LIKE '%D%v%'");
		parse("SELECT 'David_' LIKE 'David|_' ESCAPE '|'");
		parse("SELECT 'David!' NOT LIKE '%D%v%'");
		parse("SELECT 'David_' NOT LIKE 'David|_' ESCAPE '|'");
	}

	@Ignore
	public void testREGEXP() throws Exception {
		parse("SELECT 'Monty!' SIMILAR TO '.*'");
		parse("SELECT 'Monty!' REGEXP '.*'");
		parse("SELECT 'Monty!' NOT SIMILAR TO '.*'");
		parse("SELECT 'Monty!' NOT REGEXP '.*'");
		parse("SELECT 'Monty!' RLIKE '.*'");
		parse("SELECT 'Monty!' NOT RLIKE '.*'");
	}

	public void testClusteringKey() {
		parse("alter table a add clustering key kk(a,b)");
		parse("alter table a drop clustering key kk");
	}

	@Test
	public void testDistinctPk() {
		parse(
				// agg will be removed
				"select distinct(id) from test4dmp.test"
		);
	}

	@Test
	public void testDistribution() {
		parse(
				"select id, count(int_test) from test4dmp.test group by id"
		);
	}

	@Test
	public void testDistribution2() {
		parse(
				"select sum(id),count(distinct id),sum(int_test),count(distinct int_test) from test4dmp.test group by id order by id"
		);
	}

	@Test
	public void testCollation1() {
		parse(
				"with t11 as (select id as aid from test4dmp.test where int_test > 1 order by id), " +
				"t22 as (select id as bid from test4dmp.test where int_test < 10 order by id) " +
				"select aid from t11, t22 where t11.aid = t22.bid"
		);
	}

	@Test
	public void testWithoutDB() {
			parse(
					"select id, count(int_test) from test group by id"
			);
	}

	@Test
	public void testAggTableScan() {
		parse(
				"select case when (select count(*) from test4dmp.test   where int_test between 1 and 20) > 409437\n" +
						"            then (select avg(int_test) \n" +
						"                  from test4dmp.test \n" +
						"                  where int_test between 1 and 20) \n" +
						"            else (select avg(int_test)\n" +
						"                  from test4dmp.test   \n" +
						"                  where int_test between 1 and 20) end bucket1 "
		);
	}

	@Test
	public void testProjectTrait() {
		parse(
				"select int_test, id + 1, 2, id from test4dmp.test"
		);
//		parse(
//				"select id, id + 1 from test4dmp.test"
//		);
//		parse(
//				"select id, 2, id + 1 from test4dmp.test"
//		);
//		parse(
//				"select id + 1 from test4dmp.test"
//		);
	}

	@Test
	public void testAssert() {
		parse(
				"select * from test4dmp.test limit 100"
		);
	}

	@Test
	public void testFilterTrait() {
		parse(
				"select X.xid from (select id+1 as xid from test4dmp.test) as X where X.xid < 10"
		);
	}

	@Test
	public void testAggNotRemove() throws Exception {
		parse("select distinct int_test from test4dmp.test where int_test>110 order by int_test");
	}

	@Test
	public void testAggRemove() throws Exception {
		parse("select distinct id from test4dmp.test where int_test>110 order by id");
	}

	@Test
	public void testValues1() throws Exception {
		parse("select * from test4dmp.test where id in (select 1)");
	}

	@Test
	public void testValues2() throws Exception {
		parse("select * from test4dmp.test where id in (select 1 from test4dmp.test)");
	}

	@Test
	public void testValues3() throws Exception {
		parse("select * from test4dmp.test where id in (select 1 from test4dmp.test group by 1)");
	}

	@Test
	public void testValues4() throws Exception {
		parse("select * from test4dmp.test where id in (select 1 from test4dmp.test group by 1 order by 1)");
	}

	@Test
	public void testCorrelateSubquery() {
		parse(
				        "select  \n" +
						"  count(distinct cs_order_number) order_count, sum(cs_order_number) " +
						"from\n" +
						"   tpcds.catalog_sales cs1\n" +
						"  ,tpcds.customer_address\n" +
						"where \n" +
						"  cs1.cs_ship_addr_sk = ca_address_sk\n" +
						"  and ca_state = 'NY'\n" +
						"  and exists (select 1\n" +
						"              from tpcds.catalog_sales cs2\n" +
						"              where cs1.cs_order_number = cs2.cs_order_number\n" +
						"                    and cs1.cs_warehouse_sk <> cs2.cs_warehouse_sk)\n" +
						"  and not exists(select 1\n" +
						"                 from tpcds.catalog_returns cr1\n" +
						"                 where cs1.cs_order_number = cr1.cr_order_number)\n" +
						"order by count(distinct cs_order_number)\n" +
						"limit 100"
		);
	}

	@Test
	public void testSubquery2() {
		parse(
					"select \n" +
					"  cd_dep_college_count\n" +
					" from\n" +
					"  tpcds.customer c,tpcds.customer_address ca,tpcds.customer_demographics\n" +
					" where\n" +
					"  c.c_current_addr_sk = ca.ca_address_sk and\n" +
					"  cd_demo_sk = c.c_current_cdemo_sk and \n" +
					"  exists (select *\n" +
					"          from tpcds.store_sales,tpcds.date_dim\n" +
					"          where c.c_customer_sk = ss_customer_sk and\n" +
					"                ss_sold_date_sk = d_date_sk "
					+ "                and d_year = 1999" // this matters!
  					+ ")"
		);
	}

	@Test
	public void testUnionAllProjectInWith() {
		parse(
						"with wscs as\n" +
						" (select sold_date_sk\n" +
						"  from (select ws_sold_date_sk sold_date_sk\n" +
						"        from tpcds.web_sales) \n" +
						"        union all\n" +
						"        (select cs_sold_date_sk sold_date_sk\n" +
						"        from tpcds.catalog_sales)\n" +
						"  ) " +
						" select sold_date_sk from wscs limit 10"
		);
	}

	@Test
	public void planQ8() throws Exception {
		parse(
				"select s_store_name\n" +
				"      ,sum(ss_net_profit)\n" +
				" from tpcds.store_sales\n" +
				"     ,tpcds.date_dim\n" +
				"     ,tpcds.store,\n" +
				"     (select ca_zip\n" +
				"      from (\n" +
				"      SELECT substring(ca_zip,1,5) ca_zip\n" +
				"      FROM tpcds.customer_address\n" +
				"      WHERE substring(ca_zip,1,5) IN (\n" +
				"                          '58429','40697','80614','10502','32779',\n" +
						"                          '91137','61265','98294','17921','18427',\n" +
						"                          '21203','59362','87291','84093','21505',\n" +
						"                          '17184','10866','67898','25797','28055',\n" +
						"                          '18377','80332','74535','21757','29742',\n" +
						"                          '90885','29898','17819','40811','25990',\n" +
						"                          '47513','89531','91068','10391','18846',\n" +
						"                          '99223','82637','41368','83658','86199',\n" +
						"                          '81625','26696','89338','88425','32200',\n" +
						"                          '81427','19053','77471','36610','99823',\n" +
						"                          '43276','41249','48584','83550','82276',\n" +
						"                          '18842','78890','14090','38123','40936',\n" +
						"                          '34425','19850','43286','80072','79188',\n" +
						"                          '54191','11395','50497','84861','90733',\n" +
						"                          '21068','57666','37119','25004','57835',\n" +
						"                          '70067','62878','95806','19303','18840',\n" +
						"                          '19124','29785','16737','16022','49613',\n" +
						"                          '89977','68310','60069','98360','48649',\n" +
						"                          '39050','41793','25002','27413','39736',\n" +
						"                          '47208','16515','94808','57648','15009',\n" +
						"                          '80015','42961','63982','21744','71853',\n" +
						"                          '81087','67468','34175','64008','20261',\n" +
						"                          '11201','51799','48043','45645','61163',\n" +
						"                          '48375','36447','57042','21218','41100',\n" +
						"                          '89951','22745','35851','83326','61125',\n" +
						"                          '78298','80752','49858','52940','96976',\n" +
						"                          '63792','11376','53582','18717','90226',\n" +
						"                          '50530','94203','99447','27670','96577',\n" +
						"                          '57856','56372','16165','23427','54561',\n" +
						"                          '28806','44439','22926','30123','61451',\n" +
						"                          '92397','56979','92309','70873','13355',\n" +
						"                          '21801','46346','37562','56458','28286',\n" +
						"                          '47306','99555','69399','26234','47546',\n" +
						"                          '49661','88601','35943','39936','25632',\n" +
						"                          '24611','44166','56648','30379','59785',\n" +
						"                          '11110','14329','93815','52226','71381',\n" +
						"                          '13842','25612','63294','14664','21077',\n" +
						"                          '82626','18799','60915','81020','56447',\n" +
						"                          '76619','11433','13414','42548','92713',\n" +
						"                          '70467','30884','47484','16072','38936',\n" +
						"                          '13036','88376','45539','35901','19506',\n" +
						"                          '65690','73957','71850','49231','14276',\n" +
						"                          '20005','18384','76615','11635','38177',\n" +
						"                          '55607','41369','95447','58581','58149',\n" +
						"                          '91946','33790','76232','75692','95464',\n" +
						"                          '22246','51061','56692','53121','77209',\n" +
						"                          '15482','10688','14868','45907','73520',\n" +
						"                          '72666','25734','17959','24677','66446',\n" +
						"                          '94627','53535','15560','41967','69297',\n" +
						"                          '11929','59403','33283','52232','57350',\n" +
						"                          '43933','40921','36635','10827','71286',\n" +
						"                          '19736','80619','25251','95042','15526',\n" +
						"                          '36496','55854','49124','81980','35375',\n" +
						"                          '49157','63512','28944','14946','36503',\n" +
						"                          '54010','18767','23969','43905','66979',\n" +
						"                          '33113','21286','58471','59080','13395',\n" +
						"                          '79144','70373','67031','38360','26705',\n" +
						"                          '50906','52406','26066','73146','15884',\n" +
						"                          '31897','30045','61068','45550','92454',\n" +
						"                          '13376','14354','19770','22928','97790',\n" +
						"                          '50723','46081','30202','14410','20223',\n" +
						"                          '88500','67298','13261','14172','81410',\n" +
						"                          '93578','83583','46047','94167','82564',\n" +
						"                          '21156','15799','86709','37931','74703',\n" +
						"                          '83103','23054','70470','72008','49247',\n" +
						"                          '91911','69998','20961','70070','63197',\n" +
						"                          '54853','88191','91830','49521','19454',\n" +
						"                          '81450','89091','62378','25683','61869',\n" +
						"                          '51744','36580','85778','36871','48121',\n" +
						"                          '28810','83712','45486','67393','26935',\n" +
						"                          '42393','20132','55349','86057','21309',\n" +
						"                          '80218','10094','11357','48819','39734',\n" +
						"                          '40758','30432','21204','29467','30214',\n" +
						"                          '61024','55307','74621','11622','68908',\n" +
						"                          '33032','52868','99194','99900','84936',\n" +
						"                          '69036','99149','45013','32895','59004',\n" +
						"                          '32322','14933','32936','33562','72550',\n" +
						"                          '27385','58049','58200','16808','21360',\n" +
				"                          '32961','18586','79307','15492')\n" +
				"     intersect\n" +
				"      select ca_zip\n" +
				"      from (SELECT substring(ca_zip,1,5) ca_zip,count(*) cnt\n" +
				"            FROM tpcds.customer_address, tpcds.customer\n" +
				"            WHERE ca_address_sk = c_current_addr_sk and\n" +
				"                  c_preferred_cust_flag='Y'\n" +
				"            group by ca_zip\n" +
				"            having count(*) > 10) A1 ) A2 ) V1\n" +
				" where ss_store_sk = s_store_sk\n" +
				"  and ss_sold_date_sk = d_date_sk\n" +
				"  and d_qoy = 1 and d_year = 2002\n" +
				"  and (substring(s_zip,1,2) = substring(V1.ca_zip,1,2))\n" +
				" group by s_store_name\n" +
				" order by s_store_name\n" +
				" limit 100");
	}

	@Test // TODO how to optimize this sql.
	public void planQ4_simple() {
		parse(
				"select c_customer_id customer_id\n" +
						"       ,c_first_name customer_first_name\n" +
//						"       ,c_last_name customer_last_name\n" +
//						"       ,c_preferred_cust_flag customer_preferred_cust_flag\n" +
//						"       ,c_birth_country customer_birth_country\n" +
//						"       ,c_login customer_login\n" +
//						"       ,c_email_address customer_email_address\n" +
						"       ,d_year dyear\n" +
						"       ,sum(((ss_ext_list_price-ss_ext_wholesale_cost-ss_ext_discount_amt)+ss_ext_sales_price)/2) year_total -- how to eliminate these projections\n" +
						"       ,'s' sale_type\n" +
						" from tpcds.customer\n" +
						"     ,tpcds.store_sales\n" +
						"     ,tpcds.date_dim\n" +
						" where c_customer_sk = ss_customer_sk -- foreign keys\n" +
						"   and ss_sold_date_sk = d_date_sk    -- foreign keys\n" +
						" group by c_customer_id\n" +
						"         ,c_first_name\n" +
//						"         ,c_last_name\n" +
//						"         ,c_preferred_cust_flag\n" +
//						"         ,c_birth_country\n" +
//						"         ,c_login\n" +
//						"         ,c_email_address\n" +
						"         ,d_year"
		);
	}

	@Test
	public void planQ4() {
		parse(
				"with year_total as (\n" +
				" select c_customer_id customer_id\n" +
				"       ,c_first_name customer_first_name\n" +
				"       ,c_last_name customer_last_name\n" +
				"       ,c_preferred_cust_flag customer_preferred_cust_flag\n" +
				"       ,c_birth_country customer_birth_country\n" +
				"       ,c_login customer_login\n" +
				"       ,c_email_address customer_email_address\n" +
				"       ,d_year dyear\n" +
				"       ,sum(((ss_ext_list_price-ss_ext_wholesale_cost-ss_ext_discount_amt)+ss_ext_sales_price)/2) year_total\n" +
				"       ,'s' sale_type\n" +
				" from tpcds.customer\n" +
				"     ,tpcds.store_sales\n" +
				"     ,tpcds.date_dim\n" +
				" where c_customer_sk = ss_customer_sk\n" +
				"   and ss_sold_date_sk = d_date_sk\n" +
				" group by c_customer_id\n" +
				"         ,c_first_name\n" +
				"         ,c_last_name\n" +
				"         ,c_preferred_cust_flag\n" +
				"         ,c_birth_country\n" +
				"         ,c_login\n" +
				"         ,c_email_address\n" +
				"         ,d_year\n" +
				" union all\n" +
				" select c_customer_id customer_id\n" +
				"       ,c_first_name customer_first_name\n" +
				"       ,c_last_name customer_last_name\n" +
				"       ,c_preferred_cust_flag customer_preferred_cust_flag\n" +
				"       ,c_birth_country customer_birth_country\n" +
				"       ,c_login customer_login\n" +
				"       ,c_email_address customer_email_address\n" +
				"       ,d_year dyear\n" +
				"       ,sum((((cs_ext_list_price-cs_ext_wholesale_cost-cs_ext_discount_amt)+cs_ext_sales_price)/2) ) year_total\n" +
				"       ,'c' sale_type\n" +
				" from tpcds.customer\n" +
				"     ,tpcds.catalog_sales\n" +
				"     ,tpcds.date_dim\n" +
				" where c_customer_sk = cs_bill_customer_sk\n" +
				"   and cs_sold_date_sk = d_date_sk\n" +
				" group by c_customer_id\n" +
				"         ,c_first_name\n" +
				"         ,c_last_name\n" +
				"         ,c_preferred_cust_flag\n" +
				"         ,c_birth_country\n" +
				"         ,c_login\n" +
				"         ,c_email_address\n" +
				"         ,d_year\n" +
				"union all\n" +
				" select c_customer_id customer_id\n" +
				"       ,c_first_name customer_first_name\n" +
				"       ,c_last_name customer_last_name\n" +
				"       ,c_preferred_cust_flag customer_preferred_cust_flag\n" +
				"       ,c_birth_country customer_birth_country\n" +
				"       ,c_login customer_login\n" +
				"       ,c_email_address customer_email_address\n" +
				"       ,d_year dyear\n" +
				"       ,sum((((ws_ext_list_price-ws_ext_wholesale_cost-ws_ext_discount_amt)+ws_ext_sales_price)/2) ) year_total\n" +
				"       ,'w' sale_type\n" +
				" from tpcds.customer\n" +
				"     ,tpcds.web_sales\n" +
				"     ,tpcds.date_dim\n" +
				" where c_customer_sk = ws_bill_customer_sk\n" +
				"   and ws_sold_date_sk = d_date_sk\n" +
				" group by c_customer_id\n" +
				"         ,c_first_name\n" +
				"         ,c_last_name\n" +
				"         ,c_preferred_cust_flag\n" +
				"         ,c_birth_country\n" +
				"         ,c_login\n" +
				"         ,c_email_address\n" +
				"         ,d_year\n" +
				"         )\n" +
				"select t_s_secyear.customer_id\n" +
				"      ,t_s_secyear.customer_first_name\n" +
				"      ,t_s_secyear.customer_last_name\n" +
				"      ,t_s_secyear.customer_email_address\n" +
				" from year_total t_s_firstyear\n" +
				"     ,year_total t_s_secyear\n" +
				"     ,year_total t_c_firstyear\n" +
				"     ,year_total t_c_secyear\n" +
				"     ,year_total t_w_firstyear\n" +
				"     ,year_total t_w_secyear\n" +
				" where t_s_secyear.customer_id = t_s_firstyear.customer_id\n" +
				"   and t_s_firstyear.customer_id = t_c_secyear.customer_id\n" +
				"   and t_s_firstyear.customer_id = t_c_firstyear.customer_id\n" +
				"   and t_s_firstyear.customer_id = t_w_firstyear.customer_id\n" +
				"   and t_s_firstyear.customer_id = t_w_secyear.customer_id\n" +
				"   and t_s_firstyear.sale_type = 's'\n" +
				"   and t_c_firstyear.sale_type = 'c'\n" +
				"   and t_w_firstyear.sale_type = 'w'\n" +
				"   and t_s_secyear.sale_type = 's'\n" +
				"   and t_c_secyear.sale_type = 'c'\n" +
				"   and t_w_secyear.sale_type = 'w'\n" +
				"   and t_s_firstyear.dyear =  2001\n" +
				"   and t_s_secyear.dyear = 2001+1\n" +
				"   and t_c_firstyear.dyear =  2001\n" +
				"   and t_c_secyear.dyear =  2001+1\n" +
				"   and t_w_firstyear.dyear = 2001\n" +
				"   and t_w_secyear.dyear = 2001+1\n" +
				"   and t_s_firstyear.year_total > 0\n" +
				"   and t_c_firstyear.year_total > 0\n" +
				"   and t_w_firstyear.year_total > 0\n" +
				"   and case when t_c_firstyear.year_total > 0 then t_c_secyear.year_total / t_c_firstyear.year_total else null end\n" +
				"           > case when t_s_firstyear.year_total > 0 then t_s_secyear.year_total / t_s_firstyear.year_total else null end\n" +
				"   and case when t_c_firstyear.year_total > 0 then t_c_secyear.year_total / t_c_firstyear.year_total else null end\n" +
				"           > case when t_w_firstyear.year_total > 0 then t_w_secyear.year_total / t_w_firstyear.year_total else null end\n" +
				" order by t_s_secyear.customer_id\n" +
				"         ,t_s_secyear.customer_first_name\n" +
				"         ,t_s_secyear.customer_last_name\n" +
				"         ,t_s_secyear.customer_email_address\n" +
				"limit 100");
	}

	@Test
	public void planQ32() {
		parse(
				"select  sum(cs_ext_discount_amt)  excess_discount_amount \n" +
						"from \n" +
						"   tpcds.catalog_sales \n" +
						"   ,tpcds.item \n" +
						"   ,tpcds.date_dim\n" +
						"where\n" +
						"i_manufact_id = 269\n" +
						"and i_item_sk = cs_item_sk \n" +
						"and d_date between '1998-03-18' and \n" +
						"        (cast('1998-03-18' as date) + INTERVAL '90' day)\n" +
						"and d_date_sk = cs_sold_date_sk \n" +
						"and cs_ext_discount_amt  \n" +
						"     > ( \n" +
						"         select \n" +
						"            1.3 * avg(cs_ext_discount_amt) \n" +
						"         from \n" +
						"            tpcds.catalog_sales \n" +
						"           ,tpcds.date_dim\n" +
						"         where \n" +
						"              cs_item_sk = i_item_sk \n" +
						"          and d_date between '1998-03-18' and\n" +
						"                             (cast('1998-03-18' as date) + INTERVAL '90' day)\n" +
						"          and d_date_sk = cs_sold_date_sk \n" +
						"      ) \n" +
						"limit 100"
		);
	}

	@Test
	public void planQ36() {
		parse(
						"select  \n" +
						"    sum(ss_net_profit)/sum(ss_ext_sales_price) as gross_margin\n" +
						"   ,i_category\n" +
						"   ,i_class\n" +
						"   ,grouping(i_category)+grouping(i_class) as lochierarchy\n" +
						"   ,rank() over (\n" +
						" \t    partition by grouping(i_category)+grouping(i_class),\n" +
						" \t    case when grouping(i_class) = 0 then i_category end \n" +
						" \t    order by sum(ss_net_profit)/sum(ss_ext_sales_price) asc\n" +
						"  ) as rank_within_parent\n" +
						" from\n" +
						"    tpcds.store_sales\n" +
						"   ,tpcds.date_dim       d1\n" +
						"   ,tpcds.item\n" +
						"   ,tpcds.store\n" +
						" where\n" +
						"    d1.d_year = 1999 \n" +
						" and d1.d_date_sk = ss_sold_date_sk\n" +
						" and i_item_sk  = ss_item_sk \n" +
						" and s_store_sk  = ss_store_sk\n" +
						" and s_state in ('SD','FL','MI','LA','MO','SC','AL','GA')\n" +
						" group by rollup(i_category,i_class)\n" +
						" order by\n" +
						"   lochierarchy desc\n" +
						"  ,case when lochierarchy = 0 then i_category end\n" +
						"  ,rank_within_parent\n" +
						"  limit 100"
		);
	}

	@Test
	public void planQ41() {
		parse(
						"select  distinct(i_product_name)\n" +
								" from tpcds.item i1\n" +
								" where i_manufact_id between 742 and 742+40 \n" +
								"   and (select count(*) as item_cnt\n" +
								"        from tpcds.item\n" +
								"        where (i_manufact = i1.i_manufact and\n" +
								"        ((i_category = 'Women' and \n" +
								"        (i_color = 'orchid' or i_color = 'papaya') and \n" +
								"        (i_units = 'Pound' or i_units = 'Lb') and\n" +
								"        (i_size = 'petite' or i_size = 'medium')\n" +
								"        ) or\n" +
								"        (i_category = 'Women' and\n" +
								"        (i_color = 'burlywood' or i_color = 'navy') and\n" +
								"        (i_units = 'Bundle' or i_units = 'Each') and\n" +
								"        (i_size = 'N/A' or i_size = 'extra large')\n" +
								"        ) or\n" +
								"        (i_category = 'Men' and\n" +
								"        (i_color = 'bisque' or i_color = 'azure') and\n" +
								"        (i_units = 'N/A' or i_units = 'Tsp') and\n" +
								"        (i_size = 'small' or i_size = 'large')\n" +
								"        ) or\n" +
								"        (i_category = 'Men' and\n" +
								"        (i_color = 'chocolate' or i_color = 'cornflower') and\n" +
								"        (i_units = 'Bunch' or i_units = 'Gross') and\n" +
								"        (i_size = 'petite' or i_size = 'medium')\n" +
								"        ))) or\n" +
								"       (i_manufact = i1.i_manufact and\n" +
								"        ((i_category = 'Women' and \n" +
								"        (i_color = 'salmon' or i_color = 'midnight') and \n" +
								"        (i_units = 'Oz' or i_units = 'Box') and\n" +
								"        (i_size = 'petite' or i_size = 'medium')\n" +
								"        ) or\n" +
								"        (i_category = 'Women' and\n" +
								"        (i_color = 'snow' or i_color = 'steel') and\n" +
								"        (i_units = 'Carton' or i_units = 'Tbl') and\n" +
								"        (i_size = 'N/A' or i_size = 'extra large')\n" +
								"        ) or\n" +
								"        (i_category = 'Men' and\n" +
								"        (i_color = 'purple' or i_color = 'gainsboro') and\n" +
								"        (i_units = 'Dram' or i_units = 'Unknown') and\n" +
								"        (i_size = 'small' or i_size = 'large')\n" +
								"        ) or\n" +
								"        (i_category = 'Men' and\n" +
								"        (i_color = 'metallic' or i_color = 'forest') and\n" +
								"        (i_units = 'Gram' or i_units = 'Ounce') and\n" +
								"        (i_size = 'petite' or i_size = 'medium')\n" +
								"        )))) > 0\n" +
								" order by i_product_name\n" +
								" limit 100"
		);
	}

	@Test
	public void planQ44() {
		parse(
						"select  asceding.rnk, i1.i_product_name best_performing, i2.i_product_name worst_performing\n" +
						"from(select *\n" +
						"     from (select item_sk,rank() over (order by rank_col asc) rnk\n" +
						"           from (select ss_item_sk item_sk,avg(ss_net_profit) rank_col \n" +
						"                 from tpcds.store_sales ss1\n" +
						"                 where ss_store_sk = 410\n" +
						"                 group by ss_item_sk\n" +
						"                 having avg(ss_net_profit) > 0.9*(select avg(ss_net_profit) rank_col\n" +
						"                                                  from tpcds.store_sales\n" +
						"                                                  where ss_store_sk = 410\n" +
						"                                                    and ss_hdemo_sk is null\n" +
						"                                                  group by ss_store_sk))V1)V11\n" +
						"     where rnk  < 11) asceding,\n" +
						"    (select *\n" +
						"     from (select item_sk,rank() over (order by rank_col desc) rnk\n" +
						"           from (select ss_item_sk item_sk,avg(ss_net_profit) rank_col\n" +
						"                 from tpcds.store_sales ss1\n" +
						"                 where ss_store_sk = 410\n" +
						"                 group by ss_item_sk\n" +
						"                 having avg(ss_net_profit) > 0.9*(select avg(ss_net_profit) rank_col\n" +
						"                                                  from tpcds.store_sales\n" +
						"                                                  where ss_store_sk = 410\n" +
						"                                                    and ss_hdemo_sk is null\n" +
						"                                                  group by ss_store_sk))V2)V21\n" +
						"     where rnk  < 11) descending,\n" +
						"tpcds.item i1,\n" +
						"tpcds.item i2\n" +
						"where asceding.rnk = descending.rnk \n" +
						"  and i1.i_item_sk=asceding.item_sk\n" +
						"  and i2.i_item_sk=descending.item_sk\n" +
						"order by asceding.rnk\n" +
						"limit 100"
		);
	}

	@Test
	public void planQ69() {
		parse(
						"select  \n" +
						"  cd_gender,\n" +
						"  cd_marital_status,\n" +
						"  cd_education_status,\n" +
						"  count(*) cnt1,\n" +
						"  cd_purchase_estimate,\n" +
						"  count(*) cnt2,\n" +
						"  cd_credit_rating,\n" +
						"  count(*) cnt3\n" +
						" from\n" +
						"  tpcds.customer c,tpcds.customer_address ca,tpcds.customer_demographics\n" +
						" where\n" +
						"  c.c_current_addr_sk = ca.ca_address_sk and\n" +
						"  ca_state in ('CO','IL','MN') and\n" +
						"  cd_demo_sk = c.c_current_cdemo_sk and \n" +
						"  exists (select *\n" +
						"          from tpcds.store_sales,tpcds.date_dim\n" +
						"          where c.c_customer_sk = ss_customer_sk and\n" +
						"                ss_sold_date_sk = d_date_sk and\n" +
						"                d_year = 1999 and\n" +
						"                d_moy between 1 and 1+2) and\n" +
						"   (not exists (select *\n" +
						"            from tpcds.web_sales,tpcds.date_dim\n" +
						"            where c.c_customer_sk = ws_bill_customer_sk and\n" +
						"                  ws_sold_date_sk = d_date_sk and\n" +
						"                  d_year = 1999 and\n" +
						"                  d_moy between 1 and 1+2) and\n" +
						"    not exists (select * \n" +
						"            from tpcds.catalog_sales,tpcds.date_dim\n" +
						"            where c.c_customer_sk = cs_ship_customer_sk and\n" +
						"                  cs_sold_date_sk = d_date_sk and\n" +
						"                  d_year = 1999 and\n" +
						"                  d_moy between 1 and 1+2))\n" +
						" group by cd_gender,\n" +
						"          cd_marital_status,\n" +
						"          cd_education_status,\n" +
						"          cd_purchase_estimate,\n" +
						"          cd_credit_rating\n" +
						" order by cd_gender,\n" +
						"          cd_marital_status,\n" +
						"          cd_education_status,\n" +
						"          cd_purchase_estimate,\n" +
						"          cd_credit_rating\n" +
						" limit 100"
		);
	}

	@Test
	public void planQ10_2() {
		parse(
				" select cd_gender,\n" + "  cd_marital_status,\n" + "  cd_education_status,\n" + "  count(*) cnt1,\n" + "  cd_purchase_estimate,\n" + "  count(*) cnt2,\n" + "  cd_credit_rating,\n" + "  count(*) cnt3,\n" + "  cd_dep_count,\n" + "  count(*) cnt4,\n" + "  cd_dep_employed_count,\n" + "  count(*) cnt5,\n" + "  cd_dep_college_count,\n" + "  count(*) cnt6\n" + " from\n" + "  tpcds.customer c,tpcds.customer_address ca,tpcds.customer_demographics\n" + " where ca_county in ('Walker County','Richland County','Gaines County','Douglas County','Dona Ana County') and\n" + "  exists (select *\n" + "          from tpcds.store_sales,tpcds.date_dim\n" + "          where c.c_customer_sk = ss_customer_sk and\n" + "                ss_sold_date_sk = d_date_sk ) and\n" + "   (exists (select *\n" + "            from tpcds.web_sales,tpcds.date_dim\n" + "            where  ws_sold_date_sk = d_date_sk and\n" + "                  d_year = 2002 and\n" + "                  d_moy  between 4 ANd 4+3) or \n" + "    exists (select * \n" + "            from tpcds.catalog_sales,tpcds.date_dim\n" + "            where cs_sold_date_sk = d_date_sk and\n" + "                  d_year <> 2002 and\n" + "                  d_moy not between 4 and 4+3))\n" + " group by cd_gender,\n" + "          cd_marital_status,\n" + "          cd_education_status,\n" + "          cd_purchase_estimate,\n" + "          cd_credit_rating,\n" + "          cd_dep_count,\n" + "          cd_dep_employed_count,\n" + "          cd_dep_college_count\n" + " order by cd_gender,\n" + "          cd_marital_status,\n" + "          cd_education_status,\n" + "          cd_purchase_estimate,\n" + "          cd_credit_rating,\n" + "          cd_dep_count,\n" + "          cd_dep_employed_count,\n" + "          cd_dep_college_count limit 20 "
		);
	}

	@Test
	public void testOrderByAgg() throws Exception {
		parse("select id from test4dmp.test group by id order by sum(id)");
	}

	@Test
	public void planWindownAgg() {
		parse(
				" select " +
						"  rank() over ( partition by ss_item_sk order by sum(ss_net_profit) desc) as ranking " +
						"from   tpcds.store_sales " +
						" group by ss_item_sk"
		);
		parse(
				" select " +
						"  avg(sum(ss_item_sk)) over ( partition by ss_item_sk order by sum(ss_net_profit) desc) as ranking " +
						"from   tpcds.store_sales " +
						" group by ss_item_sk"
		);
	}

	@Test
	public void planQ70() {
		parse(
				"select  \n" +
						"    sum(ss_net_profit) as total_sum\n" +
						"   ,s_state\n" +
						"   ,s_county\n" +
						"   ,grouping(s_state)+grouping(s_county) as lochierarchy\n" +
						"   ,rank() over (\n" +
						"      partition by grouping(s_state)+grouping(s_county),\n" +
						" \t    case when grouping(s_county) = 0 then s_state end \n" +
						" \t    order by sum(ss_net_profit) desc) as rank_within_parent\n" +
						" from\n" +
						"    tpcds.store_sales\n" +
						"   ,tpcds.date_dim       d1\n" +
						"   ,tpcds.store\n" +
						" where\n" +
						"    d1.d_month_seq between 1212 and 1212+11\n" +
						" and d1.d_date_sk = ss_sold_date_sk\n" +
						" and s_store_sk  = ss_store_sk\n" +
						" and s_state in\n" +
						"             ( select s_state\n" +
						"               from  (select s_state as s_state, \n" +
						" \t\t\t                       rank() over ( partition by s_state order by sum(ss_net_profit) desc) as ranking\n" +
						"                      from   tpcds.store_sales, tpcds.store, tpcds.date_dim\n" +
						"                      where  d_month_seq between 1212 and 1212+11\n" +
						" \t\t\t                       and d_date_sk = ss_sold_date_sk\n" +
						" \t\t\t                       and s_store_sk  = ss_store_sk\n" +
						"                      group by s_state\n" +
						"                     ) tmp1 \n" +
						"               where ranking <= 5\n" +
						"             )\n" +
						" group by rollup(s_state,s_county)\n" +
						" order by\n" +
						"   lochierarchy desc\n" +
						"  ,case when lochierarchy = 0 then s_state end\n" +
						"  ,rank_within_parent\n" +
						" limit 100"
		);
	}

	@Test
	public void planQ88() {
		parse(
					"select  *\n" +
					"from\n" +
					" (select count(*) h8_30_to_9\n" +
					" from tpcds.store_sales, tpcds.household_demographics , tpcds.time_dim, tpcds.store\n" +
					" where ss_sold_time_sk = time_dim.t_time_sk   \n" +
					"     and ss_hdemo_sk = household_demographics.hd_demo_sk \n" +
					"     and ss_store_sk = s_store_sk\n" +
					"     and time_dim.t_hour = 8\n" +
					"     and time_dim.t_minute >= 30\n" +
					"     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or\n" +
					"          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or\n" +
					"          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2)) \n" +
					"     and store.s_store_name = 'ese') s1,\n" +
					" (select count(*) h9_to_9_30 \n" +
					" from tpcds.store_sales, tpcds.household_demographics , tpcds.time_dim, tpcds.store\n" +
					" where ss_sold_time_sk = time_dim.t_time_sk\n" +
					"     and ss_hdemo_sk = household_demographics.hd_demo_sk\n" +
					"     and ss_store_sk = s_store_sk \n" +
					"     and time_dim.t_hour = 9 \n" +
					"     and time_dim.t_minute < 30\n" +
					"     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or\n" +
					"          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or\n" +
					"          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))\n" +
					"     and store.s_store_name = 'ese') s2,\n" +
					" (select count(*) h9_30_to_10 \n" +
					" from tpcds.store_sales, tpcds.household_demographics , tpcds.time_dim, tpcds.store\n" +
					" where ss_sold_time_sk = time_dim.t_time_sk\n" +
					"     and ss_hdemo_sk = household_demographics.hd_demo_sk\n" +
					"     and ss_store_sk = s_store_sk\n" +
					"     and time_dim.t_hour = 9\n" +
					"     and time_dim.t_minute >= 30\n" +
					"     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or\n" +
					"          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or\n" +
					"          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))\n" +
					"     and store.s_store_name = 'ese') s3,\n" +
					" (select count(*) h10_to_10_30\n" +
					" from tpcds.store_sales, tpcds.household_demographics , tpcds.time_dim, tpcds.store\n" +
					" where ss_sold_time_sk = time_dim.t_time_sk\n" +
					"     and ss_hdemo_sk = household_demographics.hd_demo_sk\n" +
					"     and ss_store_sk = s_store_sk\n" +
					"     and time_dim.t_hour = 10 \n" +
					"     and time_dim.t_minute < 30\n" +
					"     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or\n" +
					"          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or\n" +
					"          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))\n" +
					"     and store.s_store_name = 'ese') s4,\n" +
					" (select count(*) h10_30_to_11\n" +
					" from tpcds.store_sales, tpcds.household_demographics , tpcds.time_dim, tpcds.store\n" +
					" where ss_sold_time_sk = time_dim.t_time_sk\n" +
					"     and ss_hdemo_sk = household_demographics.hd_demo_sk\n" +
					"     and ss_store_sk = s_store_sk\n" +
					"     and time_dim.t_hour = 10 \n" +
					"     and time_dim.t_minute >= 30\n" +
					"     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or\n" +
					"          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or\n" +
					"          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))\n" +
					"     and store.s_store_name = 'ese') s5,\n" +
					" (select count(*) h11_to_11_30\n" +
					" from tpcds.store_sales, tpcds.household_demographics , tpcds.time_dim, tpcds.store\n" +
					" where ss_sold_time_sk = time_dim.t_time_sk\n" +
					"     and ss_hdemo_sk = household_demographics.hd_demo_sk\n" +
					"     and ss_store_sk = s_store_sk \n" +
					"     and time_dim.t_hour = 11\n" +
					"     and time_dim.t_minute < 30\n" +
					"     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or\n" +
					"          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or\n" +
					"          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))\n" +
					"     and store.s_store_name = 'ese') s6,\n" +
					" (select count(*) h11_30_to_12\n" +
					" from tpcds.store_sales, tpcds.household_demographics , tpcds.time_dim, tpcds.store\n" +
					" where ss_sold_time_sk = time_dim.t_time_sk\n" +
					"     and ss_hdemo_sk = household_demographics.hd_demo_sk\n" +
					"     and ss_store_sk = s_store_sk\n" +
					"     and time_dim.t_hour = 11\n" +
					"     and time_dim.t_minute >= 30\n" +
					"     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or\n" +
					"          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or\n" +
					"          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))\n" +
					"     and store.s_store_name = 'ese') s7,\n" +
					" (select count(*) h12_to_12_30\n" +
					" from tpcds.store_sales, tpcds.household_demographics , tpcds.time_dim, tpcds.store\n" +
					" where ss_sold_time_sk = time_dim.t_time_sk\n" +
					"     and ss_hdemo_sk = household_demographics.hd_demo_sk\n" +
					"     and ss_store_sk = s_store_sk\n" +
					"     and time_dim.t_hour = 12\n" +
					"     and time_dim.t_minute < 30\n" +
					"     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or\n" +
					"          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or\n" +
					"          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))\n" +
					"     and store.s_store_name = 'ese') s8\n"
		);
	}

	@Test
	public void planQ1() {
		parse(
						"WITH customer_total_return \n" +
						"     AS (SELECT sr_customer_sk     AS ctr_customer_sk, \n" +
						"                sr_store_sk        AS ctr_store_sk, \n" +
						"                sum(sr_fee) AS ctr_total_return \n" +
						"         FROM   tpcds.store_returns, \n" +
						"                tpcds.date_dim \n" +
						"         WHERE  sr_returned_date_sk = d_date_sk \n" +
						"                AND d_year = 2000 \n" +
						"         GROUP  BY sr_customer_sk, \n" +
						"                   sr_store_sk),\n" +
						"temp_table AS\n" +
						"(SELECT Avg(ctr1.ctr_total_return) * 1.2 as avg_value\n" +
						" FROM   customer_total_return ctr1, customer_total_return ctr2 \n" +
						" WHERE  ctr1.ctr_store_sk = ctr2.ctr_store_sk\n" +
						")\n" +
						"SELECT c_customer_id \n" +
						"FROM   customer_total_return ctr1, \n" +
						"       tpcds.store, \n" +
						"       tpcds.customer,\n" +
						"       temp_table\n" +
						"WHERE  ctr1.ctr_total_return > temp_table.avg_value\n" +
						"       AND s_store_sk = ctr1.ctr_store_sk \n" +
						"       AND s_state = 'NM' \n" +
						"       AND ctr1.ctr_customer_sk = c_customer_sk \n" +
						"ORDER  BY c_customer_id\n" +
						"LIMIT 100"
		);
	}

	@Test
	public void planQ78() {
		parse(
						"with ws as\n" +
						"  (select d_year AS ws_sold_year, ws_item_sk,\n" +
						"    ws_bill_customer_sk ws_customer_sk,\n" +
						"    sum(ws_quantity) ws_qty,\n" +
						"    sum(ws_wholesale_cost) ws_wc,\n" +
						"    sum(ws_sales_price) ws_sp\n" +
						"   from tpcds.web_sales\n" +
						"   left join tpcds.web_returns on wr_order_number=ws_order_number and ws_item_sk=wr_item_sk\n" +
						"   join tpcds.date_dim on ws_sold_date_sk = d_date_sk\n" +
						"   where wr_order_number is null\n" +
						"   group by d_year, ws_item_sk, ws_bill_customer_sk\n" +
						"   ),\n" +
						"cs as\n" +
						"  (select d_year AS cs_sold_year, cs_item_sk,\n" +
						"    cs_bill_customer_sk cs_customer_sk,\n" +
						"    sum(cs_quantity) cs_qty,\n" +
						"    sum(cs_wholesale_cost) cs_wc,\n" +
						"    sum(cs_sales_price) cs_sp\n" +
						"   from tpcds.catalog_sales\n" +
						"   left join tpcds.catalog_returns on cr_order_number=cs_order_number and cs_item_sk=cr_item_sk\n" +
						"   join tpcds.date_dim on cs_sold_date_sk = d_date_sk\n" +
						"   where cr_order_number is null\n" +
						"   group by d_year, cs_item_sk, cs_bill_customer_sk\n" +
						"   ),\n" +
						"ss as\n" +
						"  (select d_year AS ss_sold_year, ss_item_sk,\n" +
						"    ss_customer_sk,\n" +
						"    sum(ss_quantity) ss_qty,\n" +
						"    sum(ss_wholesale_cost) ss_wc,\n" +
						"    sum(ss_sales_price) ss_sp\n" +
						"   from tpcds.store_sales\n" +
						"   left join tpcds.store_returns on sr_ticket_number=ss_ticket_number and ss_item_sk=sr_item_sk\n" +
						"   join tpcds.date_dim on ss_sold_date_sk = d_date_sk\n" +
						"   where sr_ticket_number is null\n" +
						"   group by d_year, ss_item_sk, ss_customer_sk\n" +
						"   )\n" +
						"select \n" +
						"ss_sold_year, ss_item_sk, ss_customer_sk,\n" +
						"round(ss_qty/(coalesce(ws_qty+cs_qty,1)),2) ratio,\n" +
						"ss_qty store_qty, ss_wc store_wholesale_cost, ss_sp store_sales_price,\n" +
						"coalesce(ws_qty,0)+coalesce(cs_qty,0) other_chan_qty,\n" +
						"coalesce(ws_wc,0)+coalesce(cs_wc,0) other_chan_wholesale_cost,\n" +
						"coalesce(ws_sp,0)+coalesce(cs_sp,0) other_chan_sales_price\n" +
						"from ss\n" +
						"left join ws on (ws_sold_year=ss_sold_year and ws_item_sk=ss_item_sk and ws_customer_sk=ss_customer_sk)\n" +
						"left join cs on (cs_sold_year=ss_sold_year and cs_item_sk=cs_item_sk and cs_customer_sk=ss_customer_sk)\n" +
						"where coalesce(ws_qty,0)>0 and coalesce(cs_qty, 0)>0 and ss_sold_year=2000\n" +
						"order by \n" +
						"  ss_sold_year, ss_item_sk, ss_customer_sk,\n" +
						"  ss_qty desc, ss_wc desc, ss_sp desc,\n" +
						"  other_chan_qty,\n" +
						"  other_chan_wholesale_cost,\n" +
						"  other_chan_sales_price,\n" +
						"  round(ss_qty/(coalesce(ws_qty+cs_qty,1)),2)\n" +
						"limit 100"
		);
	}

	@Test
	public void testColInOrderByScopeWithoutSelectItem() {
		parse("select count(*) from test4dmp.test order by int_test");
		parse("select id from test4dmp.test group by int_test order by long_test");
	}

	//
//	@Test
//	public void testProjectNum() {
//		String sql = "insert into test4dmp.test(id, int_test, boolean_test, date_test, timestamp_test, float_test, short_test, long_test, string_test, MULTIVALUE_TEST) select t.id + 1, t.int_test + 2, true, '2018-09-21', '2018-09-21 00:00:00', 1.1, 1, 1, 'haha', 'dddd,111' from (select id + 1000 as id, sum(int_test) + 5000 as int_test from test4dmp.test group by id) t";
//
//		QueryPrepared prepared = parse(sql);
//		ProjectNumRelVisitor projectNumRelVisitor = new ProjectNumRelVisitor();
//
//		projectNumRelVisitor.go(prepared);
//
//		Assert.assertTrue(projectNumRelVisitor.getProjectNumber() == 2);
//	}

	@Test
	public void voidOnlyFullGroupBySupport() {
		parse("select id, string_test, sum(int_test) from test4dmp.test group by id");
		parse("select id, sum(int_test) from test4dmp.test group by id");


		try {
			parse("select id, string_test, sum(int_test) from test4dmp.test group by id");
			Assert.fail();
		} catch (Exception e) {
			//success
		}
		parse("select id, sum(int_test) from test4dmp.test group by id");


	}

	@Test
	public void testAliasError() {
		parse("select * from (select id, sum(int_test) from test4dmp.test group by id) t limit 5");
	}

	@Test
	public void testExecute() {
		parse("execute restart worker 'a123456'");

		parse("execute restart RC '123456' finish");
	}

	@Test
	public void testAvoidConvertInToOr() {
		parse("select * from test4dmp.test where id in (1,2,3)");

	}

	@Test
	public void testUnionWithCast() {
		parse("select * from (select int_test as a from " +
				"test4dmp.test union select '' as a from test4dmp.test)");
	}

	@Test
	public void testDual() {
		parse("select 1 + 1 from dual");
		parse("select now() from DUAL");
		parse("select * from (select id from test4dmp.test union (select 0 as id from " +
				"dual)) t join (select 2 from dual) limit 1");
	}

	@Test
	public void testLimit0Reduce() {
		parse("select * from test4dmp.test limit 0");

		parse("SELECT\n" +
				"    *\n" +
				"FROM\n" +
				"    (\n" +
				"        select\n" +
				"            *\n" +
				"        from\n" +
				"            test4dmp.test\n" +
				"            left join (\n" +
				"                select\n" +
				"                    id as policy_id,\n" +
				"                    id as od_id\n" +
				"                from\n" +
				"                    test4dmp.test\n" +
				"                where\n" +
				"                    string_test = 'policy'\n" +
				"                union all\n" +
				"                select\n" +
				"                    id,\n" +
				"                    id as od_id\n" +
				"                from\n" +
				"                    test4dmp.test a\n" +
				"                    left join test4dmp.test b on a.id = b.id\n" +
				"                where\n" +
				"                    string_test = 'golden_member'\n" +
				"                    and id > 0\n" +
				"            ) tttt\n" +
				"    )\n" +
				"LIMIT\n" +
				"    0");
	}

	@Test
	public void testTinyIntPolicy() {
		parse("create table t(id int, age tinyint) distribute by hash(id)");

		parse("create table t(id int, age tinyint unsigned) distribute by hash(id)");

		parse("create table t(id int, age tinyint(1) unsigned) distribute by hash(id)");

		parse("create table t(id int, age tinyint(2) unsigned) distribute by hash(id)");

		parse("create table t(id int, age tinyint(2)) distribute by hash(id)");
	}

	@Test
	public void testSignedKeyWord() {
		parse("select cast(float_test as signed) from test");
		parse("select cast(byte_test as signed) from test");
		parse("select id as signed from test");

		parse("select 1 as signed");
		parse("SELECT\n" +
				"                                    0 as TableId,\n" +
				"                                    TABLE_NAME as TableName, \n" +
				"                                    column_name AS DbColumnName,\n" +
				"                                    CASE WHEN  left(COLUMN_TYPE,LOCATE('(',COLUMN_TYPE)-1)='' THEN COLUMN_TYPE ELSE  left(COLUMN_TYPE,LOCATE('(',COLUMN_TYPE)-1) END   AS DataType,\n" +
				"                                    CAST(SUBSTRING(COLUMN_TYPE,LOCATE('(',COLUMN_TYPE)+1,LOCATE(')',COLUMN_TYPE)-LOCATE('(',COLUMN_TYPE)-1) AS signed) AS Length,\n" +
				"                                    column_default  AS  `DefaultValue`,\n" +
				"                                    column_comment  AS  `ColumnDescription`,\n" +
				"                                     CASE WHEN COLUMN_KEY = 'PRI'\n" +
				"                                    THEN true ELSE false END AS `IsPrimaryKey`,\n" +
				"                                    CASE WHEN EXTRA='auto_increment' THEN true ELSE false END as IsIdentity,\n" +
				"                                    CASE WHEN is_nullable = 'YES'\n" +
				"                                    THEN true ELSE false END AS `IsNullable`\n" +
				"                                    FROM\n" +
				"                                    Information_schema.columns \n" +
				"                                    where TABLE_NAME='Aad_Report_ReportId' and  TABLE_SCHEMA=(select database()) ORDER BY TABLE_NAME");
	}
}
