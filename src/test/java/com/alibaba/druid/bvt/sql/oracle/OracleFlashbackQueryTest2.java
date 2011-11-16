package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class OracleFlashbackQueryTest2 extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "SELECT salary FROM employees\n"
                     + "VERSIONS BETWEEN TIMESTAMP SYSTIMESTAMP - INTERVAL '10' MINUTE AND SYSTIMESTAMP - INTERVAL '1' MINUTE\n"
                     + "WHERE last_name = 'Chung';";

        String expect = "SELECT salary\n"
                        + "FROM employees\n"
                        + "VERSIONS BETWEEN TIMESTAMP SYSTIMESTAMP - INTERVAL '10' MINUTE AND SYSTIMESTAMP - INTERVAL '1' MINUTE\n"
                        + "WHERE last_name = 'Chung';\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
