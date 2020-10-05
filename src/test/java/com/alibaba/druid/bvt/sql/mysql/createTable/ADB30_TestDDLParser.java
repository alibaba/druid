package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.junit.Assert;
import org.junit.Test;

public class ADB30_TestDDLParser extends MysqlTest {

	@Test
	public void testRenameTable() {
		{
			String sql = "alter table ddlDb.tbl rename tbl2";
			parse(sql);
		}

		{
			String sql = "alter table ddlDb.tbl rename to ddlDb2.tbl2";
			parse(sql);
		}
	}

	@Test
	public void testRenameColumn() {
		{
			String sql = "alter table ddlDb.tbl rename column col1 to col2";
			parse(sql);
		}
	}

	@Test
	public void testComment() {
		{
			String sql = "alter table test_table_comment comment = 'mycomment111'";
			SQLStatement stmt = parse(sql);
			System.out.println(stmt);
		}
	}
	@Test
	public void testrename() {
		{
			String sql = "alter table t1 rename to t2";
			SQLStatement stmt = parse(sql);
			System.out.println(stmt);
		}
	}



	@Test
	public void testDropColumn() {
		{
			String sql = "alter table ddlDb.tbl drop column col1";
			parse(sql);
			
		}
		{
			String sql = "alter table ddlDb.tbl drop col2";
			parse(sql);
		}
	}

	@Test
	public void testTruncate() {
		String sql = "TRUNCATE table ddlDb.truncateTable";

		sql = "TRUNCATE table ddlDb.truncateTable partition all";
		SQLStatement stmt = parse(sql);
		System.out.println(stmt);

		sql = "TRUNCATE table ddlDb.truncateTable partition 1,4,5";
		parse(sql);
	}

	@Test
	public void testAddColumn() {
		String sql = "alter table ddlDb.addColumn add column c1 double(2,4)";
		parse(sql);

		sql = "alter table ddlDb.addColumn add column c2 int(3) NOT NULL DEFAULT 123";
		parse(sql);
		sql = "alter table ddlDb.addColumn add column c1 double(4, 5) NOT NULL DEFAULT 1.23 AUTO_INCREMENT ENCODE='AUTO' COMPRESSION='SNAPPY'";
		parse(sql);

		sql = "alter table ddlDb.addColumn add column c1 multivalue NOT NULL AUTO_INCREMENT ENCODE='AUTO' COMPRESSION='SNAPPY' delimiter_tokenizer ': ,' value_type 'varchar int'";
		parse(sql);
	}

	@Test
	public void testDropKey() {
		String sql = "alter table ddlDB.test_dropKey drop key col1_Index";
		parse(sql);
	}

	@Test
	public void testTableMultiColumn() {
		String sql1 = "create table test__1.t1(c1 multivalue delimiter_tokenizer 'x' value_type 'varchar') distribute by hash(c1) engine='analysis'";
		parse(sql1);
	}

	@Test
	public void testTableControl() {
		String build1 = "build table testdb.test4dmp version=10000";
		String build2 = "build table testdb.test4dmp";
		String flush1 = "flush table testdb.test4dmp version=10000";
		String flush2 = "flush table testdb.test4dmp";
		parse(build1);
		parse(build2);
		parse(flush1);
		parse(flush2);
	}

	@Test
	public void testMigrateSynctax() {
		String migrate = "migrate database test4dmp shards = 'test4dmp__1' group from '55156' to '55157'";
		parse(migrate);
	}
	@Test
	public void testMigrateNewSynctax() {
		String migrate = "migrate database test4dmp shards = 'test4dmp__1' group from '55156':'192.68.1.1':1200:'offline' to '55157':'10.12.1.1':3433:'online'";
		parse(migrate);
	}

	@Test
	public void testImportSynctax() {
		String migrate = "import database test4dmp";
		parse(migrate);

	}

	@Test
	public void testCreateTableWithoutDB() {
		String ddl = "create table t1(c1 int, primary key(c1)) distribute by hash(c1)";
		parse(ddl);
	}

	@Test
	public void testCreateDB() {
		String ddl = "create database test";
		parse(ddl);
	}

	@Test
	public void testCreateSchema() {
		String ddl1 = "create schema test";
		parse(ddl1);
	}

	@Test
	public void testCreatePhysicalDB() {
		String ddl = "create physical database test shards = 1 replication = 1";
		parse(ddl);
	}

	@Test
	public void testCreatePhysicalSchema() {
		String ddl = "create physical schema test shards = 1 replication = 1";
		parse(ddl);
	}

	@Test
	public void testCreatePhysicalSchema2() {
		String ddl = "CREATE PHYSICAL DATABASE IF NOT EXISTS shardname\n" +
				"STORAGE_DEPENDENCY = 'INTERNAL'\n" +
				"SHARD_ID = 1\n" +
				"REPLICATION = 3\n" +
				"REPLICA_TYPE = 'DATA'\n" +
				"DATA_REPLICATION = 3\n";
		SQLStatement stmt = parse(ddl);
		assertEquals("CREATE PHYSICAL DATABASE IF NOT EXISTS shardname OPTIONS (REPLICATION=3 REPLICA_TYPE='DATA' STORAGE_DEPENDENCY='INTERNAL' DATA_REPLICATION=3 SHARD_ID=1 )", stmt.toString());
	}

	@Test
	public void testDropDB() {
		String ddl = "drop database test";
		parse(ddl);
	}

	@Test
	public void testDropSchema() {
		String ddl = "drop schema test";
		parse(ddl);
	}

	@Test
	public void testDropPhysicalDB() {
		String ddl = "drop physical database test";
		parse(ddl);
	}

	@Test
	public void testDropPhysicalSchema() {
		String ddl = "drop physical schema test";
		parse(ddl);
	}

	@Test
	public void testCreateTableWithClustered() {
		String sql = "CREATE TABLE IF NOT EXISTS `configs` ("
				+ "`CLUSTER_NAME` varchar(128) NOT NULL,"
				+ "`KEY` varchar(255) NOT NULL,"
				+ "`VALUE` text NOT NULL,"
				+ "`DESCRIPTION` text NULL,"
				+ "`CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
				+ " CLUSTERED INDEX idx_1 (col1,col2),"
				+ " CLUSTERED KEY idx_2 (col1,col2),"
				+ "PRIMARY KEY (`CLUSTER_NAME`,`KEY`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        MySqlCreateTableStatement statement = (MySqlCreateTableStatement) parse(sql);
		assertEquals("CREATE TABLE IF NOT EXISTS `configs` (\n" +
                "\t`CLUSTER_NAME` varchar(128) NOT NULL,\n" +
                "\t`KEY` varchar(255) NOT NULL,\n" +
                "\t`VALUE` text NOT NULL,\n" +
                "\t`DESCRIPTION` text NULL,\n" +
                "\t`CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "\tCLUSTERED INDEX idx_1(col1, col2),\n" +
                "\tCLUSTERED KEY idx_2 (col1, col2),\n" +
                "\tPRIMARY KEY (`CLUSTER_NAME`, `KEY`)\n" +
                ") ENGINE = InnoDB CHARSET = utf8", statement.toString());
        assertEquals(1, statement.getMysqlIndexes().size());
        assertEquals(2, statement.getMysqlKeys().size());
	}

	@Test
	public void testCreateTableWithClustered3() {
		String sql = "CREATE TABLE IF NOT EXISTS `configs` ("
				+ "`CLUSTER_NAME` varchar(128) NOT NULL,"
				+ "`KEY` varchar(255) NOT NULL,"
				+ "`VALUE` text NOT NULL,"
				+ "`DESCRIPTION` text NULL,"
				+ "`CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
				+ " FULLTEXT KEY idx_1 (col1,col2),"
				+ " FULLTEXT INDEX idx_2 (col1,col2),"
				+ "PRIMARY KEY (`CLUSTER_NAME`,`KEY`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        MySqlCreateTableStatement statement = (MySqlCreateTableStatement)parse(sql);
		assertEquals("CREATE TABLE IF NOT EXISTS `configs` (\n" +
				"\t`CLUSTER_NAME` varchar(128) NOT NULL,\n" +
				"\t`KEY` varchar(255) NOT NULL,\n" +
				"\t`VALUE` text NOT NULL,\n" +
				"\t`DESCRIPTION` text NULL,\n" +
				"\t`CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
				"\tFULLTEXT KEY idx_1 (col1, col2),\n" +
				"\tFULLTEXT INDEX idx_2(col1, col2),\n" +
				"\tPRIMARY KEY (`CLUSTER_NAME`, `KEY`)\n" +
				") ENGINE = InnoDB CHARSET = utf8", statement.toString());

		assertEquals(1, statement.getMysqlIndexes().size());
		assertEquals(2, statement.getMysqlKeys().size());
	}

	@Test
	public void testCreateTableWithClustered4() {
		String sql = "CREATE TABLE IF NOT EXISTS `configs` ("
				+ "`CLUSTER_NAME` varchar(128) NOT NULL,"
				+ "`KEY` varchar(255) NOT NULL,"
				+ "`VALUE` text NOT NULL,"
				+ "`DESCRIPTION` text NULL,"
				+ "`CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
				+ " CLUSTERING KEY idx_1 (col1,col2),"
				+ " CLUSTERING INDEX idx_2 (col1,col2),"
				+ "PRIMARY KEY (`CLUSTER_NAME`,`KEY`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
		SQLStatement statement = parse(sql);
		assertEquals("CREATE TABLE IF NOT EXISTS `configs` (\n" +
				"\t`CLUSTER_NAME` varchar(128) NOT NULL,\n" +
				"\t`KEY` varchar(255) NOT NULL,\n" +
				"\t`VALUE` text NOT NULL,\n" +
				"\t`DESCRIPTION` text NULL,\n" +
				"\t`CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
				"\tCLUSTERING KEY idx_1 (col1, col2),\n" +
				"\tCLUSTERING INDEX idx_2(col1, col2),\n" +
				"\tPRIMARY KEY (`CLUSTER_NAME`, `KEY`)\n" +
				") ENGINE = InnoDB CHARSET = utf8", statement.toString());
	}

	@Test
	public void testAlterClusteredIndex() {
		String ddl = "alter table t29_kernel ADD CLUSTERED  INDEX index07 (col1);";
		SQLStatement statement = parse(ddl);
		Assert.assertEquals("ALTER TABLE t29_kernel\n" +
				"\tADD CLUSTERED INDEX index07 (col1);", statement.toString());

		ddl = "alter table t29_kernel ADD CLUSTERED  KEY index07 (col1);";
		statement = parse(ddl);
		Assert.assertEquals("ALTER TABLE t29_kernel\n" +
				"\tADD CLUSTERED KEY index07 (col1);", statement.toString());
	}

	@Test
	public void testCreateClusteredIndex() {
		String ddl = "CREATE  CLUSTERED index index10 on t29_kernel(col1);";
		SQLStatement statement = parse(ddl);
		Assert.assertEquals("CREATE CLUSTERED INDEX index10 ON t29_kernel (col1);", statement.toString());
	}

	/**
	 * 一级分区
	 */
	@Test
	public void testCreateTable1() {
		String ddl = "CREATE TABLE db_name.table_name ( "
							+ "col1 boolean, "
							+ "col2 tinyint(1),"
							+ "col3 smallint(2),"
							+ "col4 int(3),"
							+ "col5 integer(4),"
							+ "col6 bigint(5),"
							+ "col7 float(6,7),"
							+ "col8 double(7,8),"
							+ "col9 varchar(9),"
							+ "col10 date,"
							+ "col11 time,"
							+ "col12 timestamp,"
							+ "col13 geo2d delimiter_tokenizer ',',"
							+ "col14 geo2d delimiter_tokenizer ':',"
							+ "col15 mediumint(8),"
							+ "col16 decimal(10,11),"
							+ "col17 datetime,"
							+ "primary key (col1,col6),"
							+ "key col4_index (col4),"
							+ "key col6_index (col6),"
							+ "clustering key col1_col2_index (col1,col2)"
//							+ "clustering key col5_col7_col9_index (col5,col7,col9)"
							+ ")"
							+ "DISTRIBUTE BY HASH(col1)";
		parse(ddl);
	}

	/**
	 * 二级分区
	 */
	@Test
	public void testCreateTable2() {
		String ddl = "CREATE TABLE DB_NAME.TABLE_NAME ( "
						+ "COL1 BOOLEAN, "
						+ "COL2 TINYINT,"
						+ "COL3 SMALLINT,"
						+ "COL4 INT,"
						+ "COL5 INTEGER,"
						+ "COL6 BIGINT,"
						+ "COL7 FLOAT,"
						+ "COL8 DOUBLE,"
						+ "COL9 VARCHAR,"
						+ "COL10 DATE,"
						+ "COL11 TIME,"
						+ "COL12 TIMESTAMP,"
						+ "KEY COL4_INDEX (COL4),"
						+ "KEY COL6_INDEX (COL6),"
						+ "CLUSTERING KEY COL1_COL2_INDEX (COL1,COL2),"
						+ "PRIMARY KEY (COL1,COL6,COL11)"
					+ ")"
					+ "DISTRIBUTE BY HASH(COL1)"
					+ "PARTITION BY VALUE(COL11)"
					+ "PARTITIONS 365";
		parse(ddl);
	}




	/**
	 * 维度表
	 */
	@Test
	public void testCreateTable3() {
		String ddl = "CREATE TABLE DB_NAME.TABLE_NAME ( " + "COL1 BOOLEAN, " + "COL2 TINYINT," + "COL3 SMALLINT,"
				+ "COL4 INT," + "COL5 INTEGER," + "COL6 BIGINT," + "COL7 FLOAT," + "COL8 DOUBLE," + "COL9 VARCHAR,"
				+ "COL10 DATE," + "COL11 TIME," + "COL12 TIMESTAMP," + "PRIMARY KEY (COL1,COL6)" + ")"
				+ "DISTRIBUTE BY BROADCAST";
		parse(ddl);

		ddl = "CREATE TABLE DB_NAME.TABLE_NAME ( " + "COL1 BOOLEAN, " + "COL2 TINYINT," + "COL3 SMALLINT,"
				+ "COL4 INT," + "COL5 INTEGER," + "COL6 BIGINT," + "COL7 FLOAT," + "COL8 DOUBLE," + "COL9 VARCHAR,"
				+ "COL10 DATE," + "COL11 TIME," + "COL12 TIMESTAMP," + "PRIMARY KEY (COL1,COL6)" + ")"
				+ "DISTRIBUTE BY BROADCAST PARTITION BY VALUE(`COL12`) PARTITIONS 30";

		parse(ddl);
	}

	// test subpartition and archive
	@Test
	public void testCreateTable4() {
		String ddl = "CREATE TABLE DB_NAME.TABLE_NAME ( "
				+ "COL1 BOOLEAN, "
				+ "COL2 TINYINT,"
				+ "COL3 SMALLINT,"
				+ "COL4 INT,"
				+ "COL5 INTEGER,"
				+ "COL6 BIGINT,"
				+ "COL7 FLOAT,"
				+ "COL8 DOUBLE,"
				+ "COL9 VARCHAR,"
				+ "COL10 DATE,"
				+ "COL11 TIME,"
				+ "COL12 TIMESTAMP"
				+ ")"
				+ "DISTRIBUTE BY HASH(COL1) "
				+ "PARTITION BY VALUE(COL11) PARTITIONS -1 "
				+ "SUBPARTITION BY VALUE(COL6) PARTITIONS 1 "
				+ "ARCHIVE BY OSS";
		parse(ddl);
	}

	// test rt_index_all
	@Test
	public void testCreateTable5() {
		String ddl = "CREATE TABLE DB_NAME.TABLE_NAME ( "
				+ "COL1 BOOLEAN, "
				+ "COL2 TINYINT,"
				+ "COL3 SMALLINT,"
				+ "COL4 INT,"
				+ "COL5 INTEGER,"
				+ "COL6 BIGINT,"
				+ "COL7 FLOAT,"
				+ "COL8 DOUBLE,"
				+ "COL9 VARCHAR,"
				+ "COL10 DATE,"
				+ "COL11 TIME,"
				+ "COL12 TIMESTAMP"
				+ ")"
				+ "DISTRIBUTE BY HASH(COL1) "
				+ "INDEX_ALL='Y' RT_INDEX_ALL='Y'";
		parse(ddl);
	}

	// test rt_index auto added for cluster key
	@Test
	public void testCreateTable6() {
		String ddl = "CREATE TABLE DB_NAME.TABLE_NAME ( "
				+ "COL1 BOOLEAN, "
				+ "COL2 TINYINT, "
				+ "clustering key col2_presort_idx(col2)"
				+ ")"
				+ "DISTRIBUTE BY HASH(COL1) ";
		parse(ddl);
	}
	// test rt_index auto added for cluster key
	@Test
	public void testCreateTable7() {
		String ddl = "create table test_charset11(charset int) distribute by hash(charset) engine='analysis' default charset='utf8' collate utf8_bin";
		SQLStatement parse = parse(ddl);
		System.out.println(parse);
	}

	@Test
	public void testDrop() {
		String ddl = "DROP TABLE db_name.table_name";
		parse(ddl);
	}

	@Test
	public void testCreateTableStringType() {
		String ddl = "CREATE TABLE baofeng (col1 varchar, col2 string) DISTRIBUTE BY BROADCAST";
		parse(ddl);
	}



	@Test
	public void testCreateTableWithTwoClusterIndex() {
		String ddl = "CREATE TABLE DB_NAME.TABLE_NAME ( "
				+ "COL1 BOOLEAN, "
				+ "COL2 TINYINT,"
				+ "COL3 SMALLINT,"
				+ "COL4 INT,"
				+ "COL5 INTEGER,"
				+ "COL6 BIGINT,"
				+ "COL7 FLOAT,"
				+ "COL8 DOUBLE,"
				+ "COL9 VARCHAR,"
				+ "COL10 DATE,"
				+ "COL11 TIME,"
				+ "COL12 TIMESTAMP,"
				+ "KEY COL4_INDEX (COL4),"
				+ "KEY COL6_INDEX (COL6),"
				+ "CLUSTERING KEY COL1_COL2_INDEX (COL1,COL2),"
				+ "CLUSTERING KEY COL5_COL7_COL9_INDEX (COL5,COL7,COL9),"
				+ "PRIMARY KEY (COL1,COL6,COL11)"
				+ ")"
				+ "DISTRIBUTE BY HASH(COL1)"
				+ "PARTITION BY VALUE(COL11)"
				+ "PARTITIONS 365";

		parse(ddl);

	}

	@Test
	public void testCreateBroadcastTableWithPartitionAndPrimayKey() {
		String ddl = "CREATE TABLE if not exists hehe (col1 varchar, col2 string, primary key(col2)) DISTRIBUTE BY BROADCAST PARTITION BY VALUE(YEARMONTHDAY(col2))";
		parse(ddl);
	}


	@Test
	public void testCreateTableIfNotExists() {
		String ddl = "CREATE TABLE if not exists baofeng (col1 varchar, col2 string) DISTRIBUTE BY BROADCAST";
		parse(ddl);
	}

	@Test
	public void testCreateTableWithComment() {
		String ddl = "CREATE TABLE baofeng (col1 varchar COMMENT '', col2 string COMMENT 'it is string') DISTRIBUTE BY BROADCAST";
		parse(ddl);
	}

	@Test
	public void testCreateTableWithNumberColumn() {
		String ddl = "CREATE TABLE baofeng (123_col1 varchar, a234 string) DISTRIBUTE BY BROADCAST";
		parse(ddl);
	}

	@Test
	public void testPartitionFunction() {
		String ddl = "CREATE TABLE baofeng (col1 varchar COMMENT '', col2 string COMMENT 'it is string') DISTRIBUTE BY HASH(col1) PARTITION BY VALUE(DATE_FORMAT(col2, '%Y%m%d')) PARTITIONS 365";
		parse(ddl);
		ddl = "CREATE TABLE baofeng (col1 varchar COMMENT '', col2 string COMMENT 'it is string') DISTRIBUTE BY HASH(col1) PARTITION BY VALUE(YEARMONTHDAY(col2)) PARTITIONS 365";
		parse(ddl);
		ddl = "CREATE TABLE baofeng (col1 varchar COMMENT '', col2 string COMMENT 'it is string') DISTRIBUTE BY HASH(col1) PARTITION BY VALUE(col2) PARTITIONS 365";
		parse(ddl);
	}

	@Test
	public void testPkDistributeKey() {
		String ddl = "CREATE TABLE shiyuan (col1 varchar, col2 string, col3 int, primary key(col2,col3,col1)) distribute by hash(col1, col2)";
		parse(ddl);

		ddl = "CREATE TABLE shiyuan (col1 varchar, col2 string, col3 int, primary key(col2,col1)) distribute by hash(col1, col2)";
		parse(ddl);

		try {
			ddl = "CREATE TABLE shiyuan (col1 varchar, col2 string, col3 int, primary key(col2)) distribute by hash(col1, col2)";
			parse(ddl);
			Assert.fail();
		} catch(Throwable t) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testAlterPartitionNum() {
		String sql = "alter table ddlDb.t1 partitions 10";
		SQLStatement stmt = parse(sql);
		System.out.println(stmt);

		sql = "alter table ddlDb.t partitions 11";
		parse(sql);
	}

	@Test
	public void testAlterBlockSize() {
		String sql = "alter table ddlDb.t1 block_size 1000";
		parseTrue(sql,"ALTER TABLE ddlDb.t1\n" +
				"\tBLOCK_SIZE 1000");
	}

	@Test
	public void testTableOptions() {
		String sql = "alter table ddlDb.t compression 'snappy'";
		parseTrue(sql,"ALTER TABLE ddlDb.t\n" +
				"\tCOMPRESSION = 'snappy'");

		sql = "alter table ddlDb.t compression = 'zstd'";
		parseTrue(sql, "ALTER TABLE ddlDb.t\n" +
				"\tCOMPRESSION = 'zstd'");
	}

	@Test
	public void testCreateOSSTableWithEndpointCheck() {
		String ddl = "Create Table `bf_rds_logs_oss_dump` (\n" + "`INS_IP` varchar NOT NULL,\n" + "`INS_PORT` int NOT NULL,\n" + "`CID` int,\n" + "`TID` int,\n" + "`TS` timestamp NOT NULL,\n" + "`ORIGIN_TIME` bigint,\n" + "`USER_IP` varchar,\n" + "`USER` varchar,\n" + "`DB` varchar,\n" + "`FAIL` varchar,\n" + "`LATENCY` bigint,\n" + "`RETURN_ROWS` bigint,\n" + "`UPDATE_ROWS` bigint,\n" + "`CHECK_ROWS` bigint,\n" + "`ISBIND` int,\n" + "`S_HASH` bigint,\n" + "`LOG` varchar,\n" + "`INS_NAME` varchar,\n" + "`DB_TYPE` varchar,\n" + "`EXTENSION` varchar,\n" + "`SQL_TYPE` varchar\n" + ") ENGINE='OSS'\n" + "TABLE_PROPERTIES='{\n" + "\"URL\":\"oss://079903/rdslog_hz/oss_dump\",\n" + "\"endpoint\":\"oss-cn-hangzhou-zmf.aliyuncs.com\",\n" + "\"accessid\":\"3P555zrtaSpb8enU\",\n" + "\"accesskey\":\"DueU9NxvLo4PcOBGL40FzBQlrMjzeu\",\n" + "\"delimiter\":\"|\"\n" + "}'";
		
		parseTrue(ddl,"CREATE TABLE `bf_rds_logs_oss_dump` (\n" +
				"\t`INS_IP` varchar NOT NULL,\n" +
				"\t`INS_PORT` int NOT NULL,\n" +
				"\t`CID` int,\n" +
				"\t`TID` int,\n" +
				"\t`TS` timestamp NOT NULL,\n" +
				"\t`ORIGIN_TIME` bigint,\n" +
				"\t`USER_IP` varchar,\n" +
				"\t`USER` varchar,\n" +
				"\t`DB` varchar,\n" +
				"\t`FAIL` varchar,\n" +
				"\t`LATENCY` bigint,\n" +
				"\t`RETURN_ROWS` bigint,\n" +
				"\t`UPDATE_ROWS` bigint,\n" +
				"\t`CHECK_ROWS` bigint,\n" +
				"\t`ISBIND` int,\n" +
				"\t`S_HASH` bigint,\n" +
				"\t`LOG` varchar,\n" +
				"\t`INS_NAME` varchar,\n" +
				"\t`DB_TYPE` varchar,\n" +
				"\t`EXTENSION` varchar,\n" +
				"\t`SQL_TYPE` varchar\n" +
				") ENGINE = 'OSS' TABLE_PROPERTIES = '{\n" +
				"\"URL\":\"oss://079903/rdslog_hz/oss_dump\",\n" +
				"\"endpoint\":\"oss-cn-hangzhou-zmf.aliyuncs.com\",\n" +
				"\"accessid\":\"3P555zrtaSpb8enU\",\n" +
				"\"accesskey\":\"DueU9NxvLo4PcOBGL40FzBQlrMjzeu\",\n" +
				"\"delimiter\":\"|\"\n" +
				"}'");

//		String ddl2 = "Create Table `bf_rds_logs_oss_dump` (\n" + "`INS_IP` varchar NOT NULL,\n" + "`INS_PORT` int NOT NULL,\n" + "`CID` int,\n" + "`TID` int,\n" + "`TS` timestamp NOT NULL,\n" + "`ORIGIN_TIME` bigint,\n" + "`USER_IP` varchar,\n" + "`USER` varchar,\n" + "`DB` varchar,\n" + "`FAIL` varchar,\n" + "`LATENCY` bigint,\n" + "`RETURN_ROWS` bigint,\n" + "`UPDATE_ROWS` bigint,\n" + "`CHECK_ROWS` bigint,\n" + "`ISBIND` int,\n" + "`S_HASH` bigint,\n" + "`LOG` varchar,\n" + "`INS_NAME` varchar,\n" + "`DB_TYPE` varchar,\n" + "`EXTENSION` varchar,\n" + "`SQL_TYPE` varchar\n" + ") ENGINE='OSS'\n" + "TABLE_PROPERTIES='{\n" + "\"URL\":\"oss://079903/rdslog_hz/oss_dump\",\n" + "\"endpoint\":\"oss-ap-southeast-1-internal.aliyuncs.com\",\n" + "\"accessid\":\"3P555zrtaSpb8enU\",\n" + "\"accesskey\":\"DueU9NxvLo4PcOBGL40FzBQlrMjzeu\",\n" + "\"delimiter\":\"|\"\n" + "}'";
//		try {
//			parse(ddl2);
//			Assert.assertTrue(false);
//		} catch (Throwable e) {
//			// oss-ap-southeast-1-internal.aliyuncs.com not reachable
//			Assert.assertTrue(true);
//		}

	}
}