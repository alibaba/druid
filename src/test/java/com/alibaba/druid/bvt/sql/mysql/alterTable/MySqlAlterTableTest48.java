package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author shicai.xsc 2018/9/13 下午3:35
 * @desc
 * @since 5.0.0.0
 */
public class MySqlAlterTableTest48 extends TestCase {
    public void test_0() throws Exception {
        String sql = "ALTER TABLE `aop_sample_pool`\n" +
                "  ADD COLUMN `properties` json NULL COMMENT '样本属性',\n" +
                "  ADD COLUMN `type` int unsigned NULL DEFAULT 1 COMMENT '1: aop样本 2:odps样本 3:swift样本',\n" +
                "  ADD COLUMN `odps_project` varchar(200) GENERATED ALWAYS AS (properties->'$.odpsProject') VIRTUAL NULL COMMENT 'odps project',\n" +
                "  ADD COLUMN `odps_table` varchar(200) GENERATED ALWAYS AS (properties->'$.odpsTable') VIRTUAL NULL COMMENT 'odps表名',\n" +
                "  ADD KEY `idx_type` (`type`),\n" +
                "  ADD KEY `idx_type_name` (`type`,`name`),\n" +
                "  ADD KEY `idx_owner` (`owner`),\n" +
                "  ADD KEY `idx_odps_project` (`odps_project`(20)),\n" +
                "  ADD KEY `idx_odps_table` (`odps_table`(20));";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        SQLStatement stmt = stmtList.get(0);
        String output = stmt.toString();
        assertEquals("ALTER TABLE `aop_sample_pool`\n" +
                "\tADD COLUMN `properties` json NULL COMMENT '样本属性',\n" +
                "\tADD COLUMN `type` int UNSIGNED NULL DEFAULT 1 COMMENT '1: aop样本 2:odps样本 3:swift样本',\n" +
                "\tADD COLUMN `odps_project` varchar(200) GENERATED ALWAYS AS (properties -> '$.odpsProject') VIRTUAL NULL COMMENT 'odps project',\n" +
                "\tADD COLUMN `odps_table` varchar(200) GENERATED ALWAYS AS (properties -> '$.odpsTable') VIRTUAL NULL COMMENT 'odps表名',\n" +
                "\tADD KEY `idx_type` (`type`),\n" +
                "\tADD KEY `idx_type_name` (`type`, `name`),\n" +
                "\tADD KEY `idx_owner` (`owner`),\n" +
                "\tADD KEY `idx_odps_project` (`odps_project`(20)),\n" +
                "\tADD KEY `idx_odps_table` (`odps_table`(20));", output);
    }

}
