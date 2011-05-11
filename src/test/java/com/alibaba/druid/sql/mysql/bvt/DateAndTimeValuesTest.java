package com.alibaba.druid.sql.mysql.bvt;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DateAndTimeValuesTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT '2008-12-31 23:59:59' + INTERVAL 1 SECOND;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT '2008-12-31 23:59:59' + INTERVAL 1 SECOND;", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT '2008-02-31' + INTERVAL 0 DAY";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT '2008-02-31' + INTERVAL 0 DAY;", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT '2008-02-31' + INTERVAL 0 MONTH";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT '2008-02-31' + INTERVAL 0 MONTH;", text);
    }

    private String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();

        for (SQLStatement stmt : stmtList) {
            stmt.accept(new MySqlOutputVisitor(out));
            out.append(";");
        }

        return out.toString();
    }
}
