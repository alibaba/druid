package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleInsertTest9 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "INSERT  INTO \"ZFJ_EN_CLICK\" \"A1\" (" + //
                     "\"P_M\",\"KEYWORD\",\"REGION_NAME\"," + //
                     "\"COUNTRY_ACCORD_NAME\",\"P_NAME\",\"P_VALUE\",\"CLICK_CNT\"" + //
                     ") " + "SELECT \"A2\".\"P_M\",\"A2\".\"KEYWORD\",\"A2\".\"REGION_NAME\"" + //
                     "  ,\"A2\".\"COUNTRY_NAME\",\"A2\".\"P_NAME\",\"A2\".\"P_VALUE\",\"A2\".\"CLICK_CNT\" " + //
                     "FROM \"ZFJ_EN_CLICK_201202\"@! \"A2\"";

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

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(14, visitor.getColumns().size());

         Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ZFJ_EN_CLICK")));
         Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ZFJ_EN_CLICK_201202@!")));
        
         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("ZFJ_EN_CLICK", "P_M")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "commission_pct")));
    }

}
