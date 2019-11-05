package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

/**
 * @author Dagon0577
 * @date 2019/11/5 12:57
 */
public class MySQLAlterTest extends TestCase {
    public void test_demo_0() {
        String sql_1 = "ALTER TABLE `my_dev`.`test_info`\n" + "DROP COLUMN `b`,\n"
                + "MODIFY COLUMN `a` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '111' AFTER `d`, \n"
                + "ADD COLUMN `c` varchar(255) NULL COMMENT '333' AFTER `a`";

        String sql_2 = "ALTER TABLE `my_dev`.`test_info`\n" + "DROP COLUMN `b`,\n"
                + "RENAME TO `his_dev`";

        SQLStatementParser parser = new MySqlStatementParser(sql_1);
        List<SQLStatement> stmtList = parser.parseStatementList();

        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
            out.append(";");
        }

        Assert.assertEquals(out.toString(),"ALTER TABLE `my_dev`.`test_info`\n"
                + "\tDROP COLUMN `b`,\n"
                + "\tMODIFY COLUMN `a` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '111' AFTER `d`,\n"
                + "\tADD COLUMN `c` varchar(255) NULL COMMENT '333' AFTER `a`;");

        parser = new MySqlStatementParser(sql_2);
        stmtList = parser.parseStatementList();

        out = new StringBuilder();
        visitor = new MySqlOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
            out.append(";");
        }

        Assert.assertEquals(out.toString(),"ALTER TABLE `my_dev`.`test_info`\n"
                + "\tDROP COLUMN `b`,\n" + "\tRENAME TO `his_dev`;");
    }

    public void test_demo_1() {
        String sql = "ALTER TABLE users ADD grade_id BIGINT DEFAULT NULL, DROP deletedAt, CHANGE username username VARCHAR(50) DEFAULT NULL, CHANGE password user_card_code VARCHAR(255) DEFAULT NULL";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList(); //

        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
            out.append(";");
        }
        Assert.assertEquals(out.toString(),"ALTER TABLE users\n"
                + "\tADD COLUMN grade_id BIGINT DEFAULT NULL,\n" + "\tDROP COLUMN deletedAt,\n"
                + "\tCHANGE COLUMN username username VARCHAR(50) DEFAULT NULL,\n"
                + "\tCHANGE COLUMN password user_card_code VARCHAR(255) DEFAULT NULL;");

        sql = "ALTER TABLE promotions_bargain_log CHANGE nickname nickname VARCHAR(255) NOT NULL COLLATE utf8mb4_unicode_ci COMMENT '用户昵称'";
        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();

        out = new StringBuilder();
        visitor = new MySqlOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
            out.append(";");
        }
        Assert.assertEquals(out.toString(),"ALTER TABLE promotions_bargain_log\n"
                + "\tCHANGE COLUMN nickname nickname VARCHAR(255) NOT NULL COLLATE utf8mb4_unicode_ci COMMENT '用户昵称';");
    }
}
