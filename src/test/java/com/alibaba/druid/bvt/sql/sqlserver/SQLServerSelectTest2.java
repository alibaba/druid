package com.alibaba.druid.bvt.sql.sqlserver;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerSelectTest2 extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "SELECT TOP 1 name FROM (SELECT TOP 9 name FROM master..syslogins ORDER BY name ASC) sq ORDER BY name DESC ";

        String expect = "SELECT TOP 1 name\n" + //
                        "FROM (SELECT TOP 9 name\n" + //
                        "\tFROM master..syslogins\n" + //
                        "\tORDER BY name ASC\n" + //
                        "\t) sq\n" + //
                        "ORDER BY name DESC";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
