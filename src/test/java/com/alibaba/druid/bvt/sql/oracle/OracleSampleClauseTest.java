package com.alibaba.druid.bvt.sql.oracle;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class OracleSampleClauseTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT COUNT(*) * 10 FROM orders SAMPLE (10);";

        String expected = "SELECT COUNT(*) * 10\n" + "FROM orders\n" + "SAMPLE (10);\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expected, text);

        System.out.println(text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT COUNT(*) * 10 FROM orders SAMPLE (10) SEED (1);";

        String expected = "SELECT COUNT(*) * 10\n" + "FROM orders\n" + "SAMPLE (10) SEED (1);\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expected, text);

        System.out.println(text);
    }

}
