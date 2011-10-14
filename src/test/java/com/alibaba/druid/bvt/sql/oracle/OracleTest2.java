package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class OracleTest2 extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "SELECT NAME FROM V$ARCHIVED_LOG;";

        String expect = "SELECT NAME\nFROM V$ARCHIVED_LOG;\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.output(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
