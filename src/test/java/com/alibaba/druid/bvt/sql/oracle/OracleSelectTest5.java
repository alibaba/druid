package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest5 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "SELECT t1.department_id, t2.* FROM hr_info t1, TABLE(t1.people) t2" + //
                     "   WHERE t2.department_id = t1.department_id;";

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

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("hr_info")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("hr_info.people")));

        Assert.assertEquals(3, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("hr_info", "department_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("hr_info.people", "*")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("hr_info.people", "department_id")));
    }
}
