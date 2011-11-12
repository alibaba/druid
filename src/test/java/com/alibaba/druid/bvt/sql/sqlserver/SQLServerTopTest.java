package com.alibaba.druid.bvt.sql.sqlserver;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerTopTest extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "SELECT TOP 10 * FROM T";

        String expect = "SELECT TOP 10 *\nFROM T";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
