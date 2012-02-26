package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest8 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "SELECT d.department_id, e.last_name" + //
                     "   FROM departments d, employees e" + //
                     "   WHERE d.department_id = e.department_id(+)" + //
                     "   ORDER BY d.department_id, e.last_name;";

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
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("departments")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));

        Assert.assertEquals(3, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("departments", "department_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "department_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
