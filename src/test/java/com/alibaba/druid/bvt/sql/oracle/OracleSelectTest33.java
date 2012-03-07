package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest33 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "SELECT /*+ Q1647000 NO_EXPAND ROWID(A1) */ " + //
        "A1.\"PRODUCT_ID\",A1.\"SUMMARY\",A1.\"DESCRIPTION\",A1.\"DESCRIPTION2\" " + //
        "FROM \"ALIBABA1949\".\"WS_PRODUCT_DETAIL\" A1"; //

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

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ALIBABA1949.WS_PRODUCT_DETAIL")));

        Assert.assertEquals(4, visitor.getColumns().size());

//         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("ESCROW_LOGISTICS", "*")));
         
//         Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
