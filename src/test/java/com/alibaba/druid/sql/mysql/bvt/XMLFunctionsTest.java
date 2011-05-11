package com.alibaba.druid.sql.mysql.bvt;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import junit.framework.Assert;
import junit.framework.TestCase;

public class XMLFunctionsTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "SET @xml = '<a><b>X</b><b>Y</b></a>';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SET @xml = '<a><b>X</b><b>Y</b></a>';", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT @i, ExtractValue(@xml, '//b[$@i]');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT @i, ExtractValue(@xml, '//b[$@i]');", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT @j, ExtractValue(@xml, '//b[$@j]');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT @j, ExtractValue(@xml, '//b[$@j]');", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT @k, ExtractValue(@xml, '//b[$@k]');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT @k, ExtractValue(@xml, '//b[$@k]');", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT ExtractValue('<a><b/></a>', '/a/b');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ExtractValue('<a><b/></a>', '/a/b');", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT UpdateXML('<a><b>ccc</b><d></d></a>', '/a', '<e>fff</e>') AS val1";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT UpdateXML('<a><b>ccc</b><d></d></a>', '/a', '<e>fff</e>') AS val1;", text);
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
