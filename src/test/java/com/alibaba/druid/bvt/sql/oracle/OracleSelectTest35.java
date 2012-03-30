package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest35 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "select typefuncti0_.id as id104_, typefuncti0_.function_id as function2_104_, " + //
                "typefuncti0_.in_container as in3_104_, typefuncti0_.inherited as inherited104_," + //
                "typefuncti0_.overriding as overriding104_, typefuncti0_.sn as sn104_, " + //
                "typefuncti0_.type_id as type7_104_ from com_function_ontype typefuncti0_ cross " + //
                " join com_function function1_ where typefuncti0_.function_id=function1_.id " + //
                "and (typefuncti0_.type_id in (? , ? , ? , ?)) and function1_.code=?"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("com_function_ontype")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("com_function")));

        Assert.assertEquals(9, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("ESCROW_LOGISTICS", "*")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
