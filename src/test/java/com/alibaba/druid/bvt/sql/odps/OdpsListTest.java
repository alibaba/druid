package com.alibaba.druid.bvt.sql.odps;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OdpsListTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "list roles";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("LIST roles", output);
    }

    public void test_1() throws Exception {
        String sql = "list users";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("LIST users", output);
    }

    public void test_2() throws Exception {
        String sql = "list functions";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("LIST functions", output);
    }

    public void test_3() throws Exception {
        String sql = "list resources";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("LIST resources", output);
    }

    public void test_4() throws Exception {
        String sql = "list accountproviders";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("LIST accountproviders", output);
    }

    public void test_5() throws Exception {
        String sql = "list jobs";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("LIST jobs", output);
    }

    public void test_6() throws Exception {
        String sql = "list trustedprojects";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("LIST trustedprojects", output);
    }

}
