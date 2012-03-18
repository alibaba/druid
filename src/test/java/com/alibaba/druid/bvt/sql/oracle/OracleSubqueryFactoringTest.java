package com.alibaba.druid.bvt.sql.oracle;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class OracleSubqueryFactoringTest extends TestCase {

    public void test_interval() throws Exception {
        String sql = "WITH dept_costs AS (\n"
                     + "SELECT department_name, SUM(salary) dept_total\n"
                     + "FROM employees e, departments d\n"
                     + "WHERE e.department_id = d.department_id\n"
                     + "GROUP BY department_name), "
                     + "avg_cost AS (SELECT SUM(dept_total)/COUNT(*) avg FROM dept_costs)\n"
                     + "SELECT * FROM dept_costs WHERE dept_total > (SELECT avg FROM avg_cost) ORDER BY department_name;";

        String expected = "WITH\n" + "\tdept_costs\n" + "\tAS\n" + "\t(\n"
                          + "\t\tSELECT department_name, SUM(salary) AS dept_total\n"
                          + "\t\tFROM employees e, departments d\n" + "\t\tWHERE e.department_id = d.department_id\n"
                          + "\t\tGROUP BY department_name\n" + "\t), \n" + "\tavg_cost\n" + "\tAS\n" + "\t(\n"
                          + "\t\tSELECT SUM(dept_total) / COUNT(*) AS avg\n" + "\t\tFROM dept_costs\n" + "\t)\n"
                          + "SELECT *\n" + "FROM dept_costs\n" + "WHERE dept_total > (\n" + "\tSELECT avg\n"
                          + "\tFROM avg_cost\n" + "\t)\n" + "ORDER BY department_name;\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expected, text);

        System.out.println(text);
    }

}
