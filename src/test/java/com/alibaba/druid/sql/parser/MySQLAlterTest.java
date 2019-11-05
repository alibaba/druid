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
    public void test_demo_0() throws Exception {
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
}
