package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

public class ADB30_MySqlDDLTest extends MysqlTest {

    public void test_archive_table() throws Exception {
        parseTrue("archive table a.b 1:100, 2:2, 3:3", "ARCHIVE TABLE a.b 1:100, 2:2, 3:3");
        parseTrue("archive table a.b 1:100, 2 : 2, 3 :3", "ARCHIVE TABLE a.b 1:100, 2:2, 3:3");
    }

    public void test_alter_table() throws Exception {
        parseTrue("alter table ddlDb.tbl rename column col1 to col2",
                "ALTER TABLE ddlDb.tbl\n" +
                "\tRENAME COLUMN col1 TO col2");

        parseTrue("alter table ddlDb.tbl rename tbl2","RENAME TABLE ddlDb.tbl TO tbl2");
        parseTrue("alter table ddlDb.addColumn add column c1 double(2,4)",
                "ALTER TABLE ddlDb.addColumn\n" +
                "\tADD COLUMN c1 double(2, 4)");
        parseTrue("alter table ddlDb.tbl drop column col1","ALTER TABLE ddlDb.tbl\n" +
                "\tDROP COLUMN col1");
        parseTrue("alter table ddlDb.tbl drop column col1","ALTER TABLE ddlDb.tbl\n" +
                "\tDROP COLUMN col1");


        parseTrue("alter table ddlDb.addColumn add column c1 double(2,4)","ALTER TABLE ddlDb.addColumn\n" +
                "\tADD COLUMN c1 double(2, 4)");
        parseTrue("alter table ddlDb.addColumn add column c2 int(3) NOT NULL DEFAULT 123","ALTER TABLE ddlDb.addColumn\n" +
                "\tADD COLUMN c2 int(3) NOT NULL DEFAULT 123");
        parseTrue("alter table ddlDb.addColumn add column c1 double(4, 5) NOT NULL DEFAULT 1.23 AUTO_INCREMENT ENCODE='AUTO' COMPRESSION='SNAPPY'",
                "ALTER TABLE ddlDb.addColumn\n" +
                "\tADD COLUMN c1 double(4, 5) NOT NULL DEFAULT 1.23 AUTO_INCREMENT ENCODE='AUTO' COMPRESSION='SNAPPY'");

        parseTrue("alter table ddlDb.addColumn add column c1 multivalue NOT NULL AUTO_INCREMENT ENCODE='AUTO' COMPRESSION='SNAPPY' delimiter_tokenizer ': ,' value_type 'varchar int'"
        ,"ALTER TABLE ddlDb.addColumn\n" +
                        "\tADD COLUMN c1 multivalue NOT NULL AUTO_INCREMENT DELIMITER_TOKENIZER ': ,' VALUE_TYPE 'varchar int' ENCODE='AUTO' COMPRESSION='SNAPPY'");

    }

    public void test_backup() throws Exception {
        parseTrue("backup data into 'sdfwefi', 'fefwefoij'", "BACKUP DATA INTO 'sdfwefi','fefwefoij'");
        parseTrue("backup log into 'adb','sdfe'", "BACKUP LOG INTO 'adb','sdfe'");
        parseTrue("backup log list_logs   ", "BACKUP LOG LIST_LOGS");
        parseTrue("backup log status  ", "BACKUP LOG STATUS");
        parseTrue("backup cancel 'sdfwef'", "BACKUP CANCEL 'sdfwef'");
    }

    public void test_truncate() {
        parseTrue("TRUNCATE table ddlDb.truncateTable","TRUNCATE TABLE ddlDb.truncateTable");
        parseTrue("TRUNCATE table ddlDb.truncateTable partition all","TRUNCATE TABLE ddlDb.truncateTable PARTITION ALL");
        parseTrue("TRUNCATE table ddlDb.truncateTable partition 1,4,5","TRUNCATE TABLE ddlDb.truncateTable PARTITION 1, 4, 5");
    }

    public void test_restore() throws Exception {
        parseTrue("restore data from 'dfefw', 'wefeoif'", "RESTORE DATA FROM 'dfefw','wefeoif'");
        parseTrue("restore log from 'dfefw', 'wefeoif'", "RESTORE LOG FROM 'dfefw','wefeoif'");
    }

    public void test_restore_2() throws Exception {
        SQLStatement parse = parse("restore data from '[{\"dbname\":\"backup\",\"url\":\"testBackup/BACKUP_20_20180614172606\",\"upload_id\":20,\"check_type\":\"MD5\",\"checksum\":\"\",\"log_pos\":0,\"file_size\":83717}]','oss://test:test@oss-cn-hangzhou-zmf.aliyuncs.com/039696/testBackup/'");
        System.out.println(parse);
    }

    public void test_build() throws Exception {
        parseTrue("build table a.b ", "BUILD TABLE a.b");
        parseTrue("build table a.b version = 1000 with split", "BUILD TABLE a.b VERSION = 1000 WITH SPLIT");
        parseTrue("build table a.b version = 1000 ", "BUILD TABLE a.b VERSION = 1000");
        parseTrue("build table a.b with split ", "BUILD TABLE a.b WITH SPLIT");
    }

    public void test_cancel() throws Exception {
        parseTrue("cancel  JOB '1'", "CANCEL JOB '1'");
        parseTrue("cancel  LOAD_JOB  '1'", "CANCEL JOB '1'");
        parseTrue("cancel  SYNC_JOB '1'", "CANCEL JOB '1'");
    }

    public void test_database() throws Exception {
        parseTrue("create database if not exists test__1 options(shards=1 shard_id=1 replication=1 storage_dependency='INTERNAL')", "CREATE DATABASE IF NOT EXISTS test__1 OPTIONS (replication=1 shards=1 storage_dependency='INTERNAL' shard_id=1 )");
        parseTrue("create schema if not exists test__1 options(shards=1 shard_id=1 replication=1 storage_dependency='INTERNAL')", "CREATE DATABASE IF NOT EXISTS test__1 OPTIONS (replication=1 shards=1 storage_dependency='INTERNAL' shard_id=1 )");
        parseTrue("create schema if not exists test__1  shards=1 shard_id=1 replication=1 storage_dependency='INTERNAL'", "CREATE DATABASE IF NOT EXISTS test__1 OPTIONS (replication=1 shards=1 storage_dependency='INTERNAL' shard_id=1 )");
    }

    public void test_create_table2() {
        parse("create table testBlockSize_beforeAddColumn(c1 int , c2 int) distribute by hash(c1) " +
                " partition by value(c2) partitions 99 " +
                " block_size " + 1 + " engine='analysis'");
    }

    public void test_database2() throws Exception {
        SQLStatement create_database_test = parse("create database test");
        System.out.println(create_database_test);
    }

    public void test_event() throws Exception {
        parseTrue("CREATE EVENT e1 on schedule at '2018-12-12 11:12:21' do select 1",
                "CREATE EVENT e1 ON SCHEDULE AT '2018-12-12 11:12:21'\n" +
                "DO\n" +
                "SELECT 1");
        parseTrue("CREATE EVENT e2 on schedule every 5 minute comment 'e2 as test' do select 1,2,3,4,5",
                "CREATE EVENT e2 ON SCHEDULE EVERY 5 MINUTECOMMENT 'e2 as test'\n" +
                "DO\n" +
                "SELECT 1, 2, 3, 4, 5");
    }

    public void test_procedure() throws Exception {
        parseTrue("CREATE procedure pdb.p2() COMMENT 'COMMENT...' LANGUAGE SQL SQL SECURITY DEFINER ( select 1 ) union all (select 2)",
                "CREATE PROCEDURE pdb.p2 ()\n" +
                        "COMMENT 'COMMENT...'\n" +
                        "LANGUAGE SQL\n" +
                        "SQL SECURITY DEFINER\n" +
                        "(SELECT 1)\n" +
                        "UNION ALL\n" +
                        "(SELECT 2)");
    }

    public void test_crate_user() throws Exception {
        parseTrue("create user if not exists 'test1'@'%' identified by 'aa2',   'test2'@'%' identified by password 'bb', test3;",
                "CREATE USER IF NOT EXISTS 'test1'@'%' IDENTIFIED BY 'aa2', 'test2'@'%' IDENTIFIED BY PASSWORD 'bb', test3;");
    }

    public void test_drop_db() throws Exception {
        parseTrue("drop database if exists db1", "DROP DATABASE IF EXISTS db1");
    }

    public void test_drop_event() throws Exception {
        parseTrue("DROP EVENT IF EXISTS edb.e1", "DROP EVENT IF EXISTS edb.e1");
    }

    public void test_drop_procedure() throws Exception {
        parseTrue("DROP procedure IF EXISTS pdb.p1", "DROP PROCEDURE IF EXISTS pdb.p1");
    }

    public void test_drop_table() throws Exception {
        parseTrue("DROP table IF EXISTS pdb.p1", "DROP TABLE IF EXISTS pdb.p1");
    }

    public void test_drop_user() throws Exception {
        parseTrue("drop user if exists 'test',  'test1',  'test2',  'test3';",
                "DROP USER IF EXISTS 'test', 'test1', 'test2', 'test3';");
    }

    public void test_drop_view() throws Exception {
        parseTrue("DROP VIEW IF EXISTS viewdb.v1, test4dmp.v1",
                "DROP VIEW IF EXISTS viewdb.v1, test4dmp.v1");
    }

    public void test_execute() throws Exception {
        parseTrue("execute restart RC '123456' finish", "EXECUTE restart RC '123456' finish");
    }

    public void test_export_db() throws Exception {
        parseTrue("export database test__1 realtime = 'y'", "EXPORT DATABASE test__1 REALTIME = 'Y'");
        parseTrue("export database test__1 realtime = 'n'", "EXPORT DATABASE test__1 REALTIME = 'N'");
    }

    public void test_export_table() throws Exception {
        parseTrue("export table test__1.t1", "EXPORT TABLE test__1.t1");
    }

    public void test_flush() throws Exception {
        parseTrue("flush table test__1.t1 version=4", "FLUSH TABLES test__1.t1 VERSION = 4");
    }

    public void test_grant() throws Exception {
        parseTrue("grant select on test4p.tt3 to 'test'@'%' identified by 'ccc';",
                "GRANT SELECT ON test4p.tt3 TO 'test'@'%' IDENTIFIED BY 'ccc';");

        parseTrue("grant select(id) on test4dmp.grade to test;",
                "GRANT SELECT(id) ON test4dmp.grade TO test;");
    }

    public void test_import_db() throws Exception {
        parseTrue("IMPORT DATABASE test4dmp__2  Status=''", "IMPORT DATABASE test4dmp__2 STATUS = ''");
    }

    public void test_import_table() throws Exception {
        parseTrue("import table tablename version = 123", "IMPORT TABLE tablename VERSIOIN = 123");
        parseTrue("import table tablename version = 123 build='Y'", "IMPORT TABLE tablename VERSIOIN = 123 BUILD = 'Y'");
    }

    public void test_manage_instance_group() throws Exception {
        parseTrue("add INSTANCE_GROUP 1 REPLICATION = 3\n", "ADD INSTANCE_GROUP 1 REPLICATION = 3");
        parseTrue("drop INSTANCE_GROUP 2,3", "DROP INSTANCE_GROUP 2,3");
    }

    public void test_migrate() throws Exception {
        parseTrue("migrate database test4dmp shards='test4dmp__1,test4dmp__3' host from '301':'127.0.0.1':1123:'offline' to '304':'30.40.43.23':4309:'online'",
                "MIGRATE DATABASE test4dmp SHARDS='test4dmp__1,test4dmp__3' HOST FROM '301':'127.0.0.1':1123:'offline' TO '304':'30.40.43.23':4309:'online'");
    }

    public void test_revoke() throws Exception {
        parseTrue("revoke update,delete on aa.bb from test@'%' ",
                "REVOKE UPDATE, DELETE ON aa.bb FROM 'test'@'%'");
    }

    public void test_raft() throws Exception {
        parseTrue("sync raft_leader_transfer shard='shardname' from='fromMemberAddr' to='toMemberAddr' timeout=10000",
                "SYNC RAFT_LEADER_TRANSFER SHARD='shardname' FROM='fromMemberAddr' TO='toMemberAddr' TIMEOUT=10000");

        parseTrue("SYNC RAFT_MEMBER_CHANGE noleader SHARD='shardName'\n" +
                " HOST='hostId'\n" +
                " STATUS='ACTIVE'\n" +
                "FORCE", "SYNC RAFT_MEMBER_CHANGE NOLEADER SHARD='shardName' HOST='hostId' STATUS='ACTIVE' FORCE");
    }

    public void test_rename_user() throws Exception {
        parseTrue("rename user test@'%' to test1@'%'", "RENAME USER 'test'@'%' TO 'test1'@'%'");
        parseTrue("rename user 'test1' to 'test2'", "RENAME USER 'test1' TO 'test2'");
    }

    public void test_show_command() throws Exception {
        parseTrue("show cluster name", "SHOW CLUSTER NAME");
        parseTrue("Show Migrate task status where 1=1", "SHOW MIGRATE TASK STATUS WHERE 1 = 1");
    }

    public void test_submit_job() throws Exception {
        parseTrue("submit job await select 1", "SUBMIT JOB AWAIT SELECT 1");
    }


}