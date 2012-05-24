package com.alibaba.druid.bvt.sql.oracle;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.stat.TableStat.Condition;

public class OracleSchemaStatVisitorTest extends OracleTest {

    public void test_0() throws Exception {
        String sql = "SELECT id, name name from department d" + //
                     "   WHERE d.id = ? order by name desc";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        List<Object> parameters = new ArrayList<Object>();
        parameters.add(23456);
        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        visitor.setParameters(parameters);
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("department")));

        Assert.assertEquals(2, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("department", "id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("department", "name")));
        
        Assert.assertEquals(1, visitor.getConditions().size());
        
        Condition condition = visitor.getConditions().get(0);
        Assert.assertSame(parameters.get(0), condition.getValues().get(0));
        
        Column orderByColumn = visitor.getOrderByColumns().iterator().next();
        Assert.assertEquals(SQLOrderingSpecification.DESC, orderByColumn.getAttributes().get("orderBy.type"));
    }
}
