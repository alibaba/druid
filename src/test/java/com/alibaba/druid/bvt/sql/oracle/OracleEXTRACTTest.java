package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class OracleEXTRACTTest extends TestCase {

    public void test_Extract() throws Exception {
        String sql = "SELECT EXTRACT(YEAR FROM DATE '1998-03-07') FROM DUAL;";

        String expect = "SELECT EXTRACT(YEAR FROM DATE '1998-03-07')\n" + "FROM DUAL;\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
