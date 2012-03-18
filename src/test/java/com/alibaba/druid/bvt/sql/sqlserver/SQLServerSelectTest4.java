package com.alibaba.druid.bvt.sql.sqlserver;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerSelectTest4 extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "SELECT name, password_hash FROM master.sys.sql_logins";

        String expect = "SELECT name, password_hash\n" + //
                        "FROM master.sys.sql_logins";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
