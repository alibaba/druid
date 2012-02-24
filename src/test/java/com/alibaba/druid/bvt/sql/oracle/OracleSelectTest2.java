package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "WITH " + //
                     "   dept_costs AS (" + //
                     "      SELECT department_name, SUM(salary) dept_total" + //
                     "         FROM employees e, departments d" + //
                     "         WHERE e.department_id = d.department_id" + //
                     "      GROUP BY department_name)," + //
                     "   avg_cost AS (" + //
                     "      SELECT SUM(dept_total)/COUNT(*) avg" + //
                     "      FROM dept_costs)" + //
                     "SELECT * FROM dept_costs" + //
                     "   WHERE dept_total >" + //
                     "      (SELECT avg FROM avg_cost)" + //
                     "      ORDER BY department_name;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        System.out.println(output(statementList));

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertEquals(2, visitor.getTables().size());
        
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("departments")));

        Assert.assertEquals(0, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getFields().contains(new TableStat.Column("films", "producer_id")));
    }

}
