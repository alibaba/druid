package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;

public class MySqlCreateTableTest156_ob
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table `ar_ranking_001_t` (\n" +
                "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `user_id` varchar(32) NOT NULL COMMENT '用户ID',\n" +
                "  `scene_code` varchar(64) NOT NULL COMMENT '业务场景码',\n" +
                "  `record_type` varchar(16) NOT NULL COMMENT '记录类型 分数/等级',\n" +
                "  `record_value` int(11) NOT NULL COMMENT '记录值',\n" +
                "  `record_date` varchar(8) NOT NULL COMMENT '记录日期',\n" +
                "  `gmt_create` datetime NOT NULL COMMENT '创建时间',\n" +
                "  `gmt_modified` datetime NOT NULL COMMENT '修改时间',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `idx_ranking` (`user_id`, `scene_code`, `record_type`, `record_date`) BLOCK_SIZE 16384\n" +
                ") AUTO_INCREMENT = 1000001 DEFAULT CHARSET = utf8mb4 \n" +
                "COMPRESSION = 'lz4_1.0' REPLICA_NUM = 3 BLOCK_SIZE = 16384 USE_BLOOM_FILTER = FALSE TABLET_SIZE = 134217728 PCTFREE = 10 COMMENT = 'ar平台数据排行榜';\n";

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);
        assertEquals("CREATE TABLE `ar_ranking_001_t` (\n" +
                "\t`id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "\t`user_id` varchar(32) NOT NULL COMMENT '用户ID',\n" +
                "\t`scene_code` varchar(64) NOT NULL COMMENT '业务场景码',\n" +
                "\t`record_type` varchar(16) NOT NULL COMMENT '记录类型 分数/等级',\n" +
                "\t`record_value` int(11) NOT NULL COMMENT '记录值',\n" +
                "\t`record_date` varchar(8) NOT NULL COMMENT '记录日期',\n" +
                "\t`gmt_create` datetime NOT NULL COMMENT '创建时间',\n" +
                "\t`gmt_modified` datetime NOT NULL COMMENT '修改时间',\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tKEY `idx_ranking` (`user_id`, `scene_code`, `record_type`, `record_date`) KEY_BLOCK_SIZE = 16384\n" +
                ") AUTO_INCREMENT = 1000001 CHARSET = utf8mb4 COMPRESSION = 'lz4_1.0' REPLICA_NUM = 3 BLOCK_SIZE = 16384 USE_BLOOM_FILTER = false TABLET_SIZE = 134217728 PCTFREE = 10 COMMENT 'ar平台数据排行榜';", stmt.toString());
    }
}