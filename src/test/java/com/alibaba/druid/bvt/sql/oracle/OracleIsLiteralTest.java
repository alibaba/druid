package com.alibaba.druid.bvt.sql.oracle;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class OracleIsLiteralTest extends TestCase {

    public void test_oracle() throws Exception {
        String sql = "SELECT FROM_TZ(TIMESTAMP '2007-11-20 08:00:00', '3:00') FROM DUAL;";

        String expect = "SELECT FROM_TZ(TIMESTAMP '2007-11-20 08:00:00', '3:00')\n" + "FROM DUAL;\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }

}
