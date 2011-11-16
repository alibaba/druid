package com.alibaba.druid.bvt.sql.oracle;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class CursorTest extends TestCase {

    public void test_cursor() throws Exception {
        String sql = "SELECT department_name, CURSOR(SELECT salary, commission_pct FROM employees e WHERE e.department_id = d.department_id) "
                     + "FROM departments d;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals("SELECT department_name, CURSOR(SELECT salary, commission_pct\n" + "\tFROM employees e\n"
                            + "\tWHERE e.department_id = d.department_id)\n" + "FROM departments d;\n", text);

        System.out.println(text);
    }
}
