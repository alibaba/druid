package com.alibaba.druid.bvt.sql.oracle;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class OracleSubqueryFactoringTest2 extends TestCase {

    public void test_interval() throws Exception {
        String sql = "WITH org_chart (eid, emp_last, mgr_id, reportLevel, salary, job_id) AS\n" +
        		"(\n" +
        		"SELECT employee_id, last_name, manager_id, 0 reportLevel, salary, job_id\n" +
        		"FROM employees\n" +
        		"WHERE manager_id is null\n" +
        		"UNION ALL\n" +
        		"SELECT e.employee_id, e.last_name, e.manager_id, r.reportLevel+1 reportLevel, e.salary, e.job_id\n" +
        		"FROM org_chart r, employees e\n" +
        		"WHERE r.eid = e.manager_id\n" +
        		")\n" +
        		"SEARCH DEPTH FIRST BY emp_last SET order1\n" +
        		"CYCLE hire_date SET is_cycle TO 'Y' DEFAULT 'N'" +
        		"SELECT lpad(' ',2*reportLevel)||emp_last emp_name, eid, mgr_id, salary, job_id\n" +
        		"FROM org_chart\n" +
        		"ORDER BY order1;\n";

        String expected = "WITH\n" +
        		"\torg_chart (eid, emp_last, mgr_id, reportLevel, salary, job_id)\n" +
        		"\tAS\n" +
                "\t(\n" +
                "\t\tSELECT employee_id, last_name, manager_id, 0 AS reportLevel, salary\n" +
                "\t\t\t, job_id\n" +
                "\t\tFROM employees\n" +
                "\t\tWHERE manager_id IS NULL\n" +
                "\t\tUNION ALL\n" +
                "\t\tSELECT e.employee_id, e.last_name, e.manager_id, r.reportLevel + 1 AS reportLevel, e.salary\n" +
                "\t\t\t, e.job_id\n" +
                "\t\tFROM org_chart r, employees e\n" +
                "\t\tWHERE r.eid = e.manager_id\n" +
                "\t)\n" +
                "\tSEARCH DEPTH FIRST BY emp_last SET order1\n" +
                "\tCYCLE hire_date SET is_cycle TO 'Y' DEFAULT 'N'\n" +
                "SELECT lpad(' ', 2 * reportLevel) || emp_last AS emp_name, eid, mgr_id, salary, job_id\n" +
                "FROM org_chart\n" +
                "ORDER BY order1;\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expected, text);

        System.out.println(text);
    }

}
