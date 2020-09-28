package com.alibaba.druid.bvt.sql.mysql.alter;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

/**
 * @author Dagon0577
 * @date 2019/11/7 10:54
 */
public class MySqlAlterTableTest39 extends TestCase {
    public void test_demo_0() {
        String sql = "ALTER TABLE `my_dev`.`test_info`\n" + "DROP COLUMN `b`,\n"
                + "MODIFY COLUMN `a` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '111' AFTER `d`, \n"
                + "ADD COLUMN `c` varchar(255) NULL COMMENT '333' AFTER `a`";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        SQLStatement stmt = stmtList.get(0);
        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER TABLE `my_dev`.`test_info`\n" + "\tDROP COLUMN `b`,\n"
                + "\tMODIFY COLUMN `a` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '111' AFTER `d`,\n"
                + "\tADD COLUMN `c` varchar(255) NULL COMMENT '333' AFTER `a`", output);

        sql = "ALTER TABLE `my_dev`.`test_info`\n" + "DROP COLUMN `b`,\n"
                + "RENAME TO `his_dev`";
        stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        stmt = stmtList.get(0);
        output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER TABLE `my_dev`.`test_info`\n" + "\tDROP COLUMN `b`,\n"
                + "\tRENAME TO `his_dev`",output);
    }

    public void test_demo_1() {
        String sql = "ALTER TABLE users ADD grade_id BIGINT DEFAULT NULL, DROP deletedAt, CHANGE username username VARCHAR(50) DEFAULT NULL, CHANGE password user_card_code VARCHAR(255) DEFAULT NULL";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        SQLStatement stmt = stmtList.get(0);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER TABLE users\n"
                + "\tADD COLUMN grade_id BIGINT DEFAULT NULL,\n" + "\tDROP COLUMN deletedAt,\n"
                + "\tCHANGE COLUMN username username VARCHAR(50) DEFAULT NULL,\n"
                + "\tCHANGE COLUMN password user_card_code VARCHAR(255) DEFAULT NULL",output);

        sql = "ALTER TABLE promotions_bargain_log CHANGE nickname nickname VARCHAR(255) NOT NULL COLLATE utf8mb4_unicode_ci COMMENT '用户昵称'";

        stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        stmt = stmtList.get(0);
        output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER TABLE promotions_bargain_log\n"
                + "\tCHANGE COLUMN nickname nickname VARCHAR(255) NOT NULL COLLATE utf8mb4_unicode_ci COMMENT '用户昵称'",output);
    }
}
