package com.alibaba.druid.bvt.sql.odps;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OdpsDescTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "desc coupon_dataset_4_feature";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        stmt.toString();
        System.out.println(output);
        Assert.assertEquals("DESC coupon_dataset_4_feature", output);
    }

    public void test_1() throws Exception {
        String sql = "desc role admin";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        System.out.println(output);
        Assert.assertEquals("DESC ROLE admin", output);
    }

    public void test_2() throws Exception {
        String sql = "desc instance 20150715103441522gond1qa2";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        System.out.println(output);
        Assert.assertEquals("DESC INSTANCE 20150715103441522gond1qa2", output);
    }

    public void test_3() throws Exception {
        String sql = "desc idl_cheka_ent_sql_fht partition (ds='20151010',hh='10')";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        System.out.println(output);
        Assert.assertEquals("DESC idl_cheka_ent_sql_fht PARTITION (ds = '20151010', hh = '10')", output);
    }

    public void test_4() throws Exception {
        String sql = "desc idl_cheka_ent_sql_fdt partition (ds='20151010')";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        System.out.println(output);
        Assert.assertEquals("DESC idl_cheka_ent_sql_fdt PARTITION (ds = '20151010')", output);
    }

}
