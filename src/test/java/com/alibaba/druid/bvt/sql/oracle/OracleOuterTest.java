package com.alibaba.druid.bvt.sql.oracle;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class OracleOuterTest extends TestCase {

    public void test_oracle() throws Exception {
        String sql = "SELECT employee_id, manager_id\n" + "FROM employees\n"
                     + "WHERE employees.manager_id(+) = employees.employee_id;";

        String expect = "SELECT employee_id, manager_id\n" + "FROM employees\n"
                + "WHERE employees.manager_id(+) = employees.employee_id;\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }

}
