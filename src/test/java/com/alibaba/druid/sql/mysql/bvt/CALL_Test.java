package com.alibaba.druid.sql.mysql.bvt;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import junit.framework.Assert;
import junit.framework.TestCase;

public class CALL_Test extends TestCase {
    public void test_0() throws Exception {
        String sql = "CALL p(@version, @increment);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("CALL p(@version, @increment);", text);
    }

    public void test_1() throws Exception {
        String sql = "PREPARE s FROM 'CALL p(?, ?)';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("PREPARE s FROM 'CALL p(?, ?)';", text);
    }

    public void test_2() throws Exception {
        String sql = "EXECUTE s USING @version, @increment;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("EXECUTE s USING @version, @increment;", text);
    }

    public void test_3() throws Exception {
        String sql = "EXECUTE s";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("EXECUTE s;", text);
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
