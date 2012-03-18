package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class OracleNumberLiteralTest extends TestCase {

    public void test_number_literal() throws Exception {
        String sql = "SELECT 7, +255, 0.5, +6.34,25e-03, +6.34F, 0.5d, -1D FROM DUAL";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals("SELECT 7, 255, 0.5, 6.34, 0.025\n\t, 6.34F, 0.5D, -1.0D\nFROM DUAL;\n", text);

        System.out.println(text);
    }

    public void test_number_literal_2() throws Exception {
        String sql = "SELECT BINARY_FLOAT_INFINITY FROM DUAL";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals("SELECT BINARY_FLOAT_INFINITY\nFROM DUAL;\n", text);

        System.out.println(text);
    }
}
