package com.alibaba.druid.sql.mysql.bvt;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ControlFlowFunctionsTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT CASE 1 WHEN 1 THEN 'one' WHEN 2 THEN 'two' ELSE 'more' END;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CASE 1 WHEN 1 THEN 'one' WHEN 2 THEN 'two' ELSE 'more' END;", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT IF(1>2,2,3);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT IF(1 > 2, 2, 3);", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT IF(1<2,'yes','no');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT IF(1 < 2, 'yes', 'no');", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT IF(STRCMP('test','test1'),'no','yes');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT IF(STRCMP('test', 'test1'), 'no', 'yes');", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT IFNULL(1,0);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT IFNULL(1, 0);", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT IFNULL(1/0,'yes');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT IFNULL(1 / 0, 'yes');", text);
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
