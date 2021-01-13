package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest153_collate extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `t_file` (\n" +
                "`id` BIGINT(20) NOT NULL AUTO_INCREMENT,\n" +
                "`account_id` VARCHAR(32) NULL DEFAULT '0' COMMENT '账户id' COLLATE 'utf8mb4_bin',\n" +
                "`bussiness_id` VARCHAR(32) NOT NULL DEFAULT '0' COMMENT '业务id' COLLATE 'utf8mb4_bin',\n" +
                "`target_id` VARCHAR(48) NULL DEFAULT '0' COLLATE 'utf8mb4_bin',\n" +
                "`file_id` VARCHAR(32) NOT NULL DEFAULT '0' COMMENT '文件id' COLLATE 'utf8mb4_bin',\n" +
                "`cloud_id` INT(11) NOT NULL DEFAULT '0' COMMENT '三方云id',\n" +
                "`src_file_id` VARCHAR(32) NOT NULL DEFAULT '0' COMMENT '源文件id' COLLATE 'utf8mb4_bin',\n" +
                "`file_name` VARCHAR(128) NOT NULL DEFAULT '0' COMMENT '文件名称' COLLATE 'utf8mb4_bin',\n" +
                "`etag` VARCHAR(256) NULL DEFAULT '0' COMMENT '文件hash' COLLATE 'utf8mb4_bin',\n" +
                "`object_name` VARCHAR(512) NOT NULL DEFAULT '0' COMMENT '云objectName' COLLATE 'utf8mb4_bin',\n" +
                "`file_type` VARCHAR(20) NOT NULL DEFAULT '0' COMMENT '文件类型' COLLATE 'utf8mb4_bin',\n" +
                "`access_mode` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '0-私有，1-公有',\n" +
                "`file_size` BIGINT(20) NULL DEFAULT '0' COMMENT '文件大小字节数',\n" +
                "`meta` TEXT NULL COMMENT '文件meta信息' COLLATE 'utf8mb4_bin',\n" +
                "`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "PRIMARY KEY (`id`),\n" +
                "UNIQUE INDEX `file_id` (`file_id`),\n" +
                "INDEX `account_id` (`account_id`),\n" +
                "INDEX `src_file_id` (`src_file_id`, `bussiness_id`),\n" +
                "INDEX `target_id` (`target_id`)\n" +
                ")\n" +
                "COLLATE='utf8mb4_bin'\n" +
                "ENGINE=InnoDB\n" +
                "dbpartition by hash(`src_file_id`) tbpartition by hash(`src_file_id`) tbpartitions 2";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE `t_file` (\n" +
                "\t`id` BIGINT(20) NOT NULL AUTO_INCREMENT,\n" +
                "\t`account_id` VARCHAR(32) COLLATE 'utf8mb4_bin' NULL DEFAULT '0' COMMENT '账户id',\n" +
                "\t`bussiness_id` VARCHAR(32) COLLATE 'utf8mb4_bin' NOT NULL DEFAULT '0' COMMENT '业务id',\n" +
                "\t`target_id` VARCHAR(48) COLLATE 'utf8mb4_bin' NULL DEFAULT '0',\n" +
                "\t`file_id` VARCHAR(32) COLLATE 'utf8mb4_bin' NOT NULL DEFAULT '0' COMMENT '文件id',\n" +
                "\t`cloud_id` INT(11) NOT NULL DEFAULT '0' COMMENT '三方云id',\n" +
                "\t`src_file_id` VARCHAR(32) COLLATE 'utf8mb4_bin' NOT NULL DEFAULT '0' COMMENT '源文件id',\n" +
                "\t`file_name` VARCHAR(128) COLLATE 'utf8mb4_bin' NOT NULL DEFAULT '0' COMMENT '文件名称',\n" +
                "\t`etag` VARCHAR(256) COLLATE 'utf8mb4_bin' NULL DEFAULT '0' COMMENT '文件hash',\n" +
                "\t`object_name` VARCHAR(512) COLLATE 'utf8mb4_bin' NOT NULL DEFAULT '0' COMMENT '云objectName',\n" +
                "\t`file_type` VARCHAR(20) COLLATE 'utf8mb4_bin' NOT NULL DEFAULT '0' COMMENT '文件类型',\n" +
                "\t`access_mode` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '0-私有，1-公有',\n" +
                "\t`file_size` BIGINT(20) NULL DEFAULT '0' COMMENT '文件大小字节数',\n" +
                "\t`meta` TEXT COLLATE 'utf8mb4_bin' NULL COMMENT '文件meta信息',\n" +
                "\t`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "\t`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tUNIQUE INDEX `file_id` (`file_id`),\n" +
                "\tINDEX `account_id`(`account_id`),\n" +
                "\tINDEX `src_file_id`(`src_file_id`, `bussiness_id`),\n" +
                "\tINDEX `target_id`(`target_id`)\n" +
                ") COLLATE = 'utf8mb4_bin' ENGINE = InnoDB\n" +
                "DBPARTITION BY hash(`src_file_id`)\n" +
                "TBPARTITION BY hash(`src_file_id`) TBPARTITIONS 2", stmt.toString());

        assertEquals("create table `t_file` (\n" +
                "\t`id` BIGINT(20) not null auto_increment,\n" +
                "\t`account_id` VARCHAR(32) collate 'utf8mb4_bin' null default '0' comment '账户id',\n" +
                "\t`bussiness_id` VARCHAR(32) collate 'utf8mb4_bin' not null default '0' comment '业务id',\n" +
                "\t`target_id` VARCHAR(48) collate 'utf8mb4_bin' null default '0',\n" +
                "\t`file_id` VARCHAR(32) collate 'utf8mb4_bin' not null default '0' comment '文件id',\n" +
                "\t`cloud_id` INT(11) not null default '0' comment '三方云id',\n" +
                "\t`src_file_id` VARCHAR(32) collate 'utf8mb4_bin' not null default '0' comment '源文件id',\n" +
                "\t`file_name` VARCHAR(128) collate 'utf8mb4_bin' not null default '0' comment '文件名称',\n" +
                "\t`etag` VARCHAR(256) collate 'utf8mb4_bin' null default '0' comment '文件hash',\n" +
                "\t`object_name` VARCHAR(512) collate 'utf8mb4_bin' not null default '0' comment '云objectName',\n" +
                "\t`file_type` VARCHAR(20) collate 'utf8mb4_bin' not null default '0' comment '文件类型',\n" +
                "\t`access_mode` TINYINT(4) not null default '0' comment '0-私有，1-公有',\n" +
                "\t`file_size` BIGINT(20) null default '0' comment '文件大小字节数',\n" +
                "\t`meta` TEXT collate 'utf8mb4_bin' null comment '文件meta信息',\n" +
                "\t`create_time` DATETIME not null default current_timestamp,\n" +
                "\t`update_time` DATETIME not null default current_timestamp on update current_timestamp,\n" +
                "\tprimary key (`id`),\n" +
                "\tunique index `file_id` (`file_id`),\n" +
                "\tindex `account_id`(`account_id`),\n" +
                "\tindex `src_file_id`(`src_file_id`, `bussiness_id`),\n" +
                "\tindex `target_id`(`target_id`)\n" +
                ") collate = 'utf8mb4_bin' engine = InnoDB\n" +
                "dbpartition by hash(`src_file_id`)\n" +
                "tbpartition by hash(`src_file_id`) tbpartitions 2", stmt.toLowerCaseString());

    }





}