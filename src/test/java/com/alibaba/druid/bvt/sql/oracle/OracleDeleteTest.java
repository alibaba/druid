package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleDeleteTest extends OracleTest {

    public void test_0() throws Exception {
        String sql = "delete from BILLING_LOG_MONITOR log where log.guid in (" + //
                     "'wb_xinmin.zhao_test121','wb_xinmin.zhao_test122'" + //
                     ",'wb_xinmin.zhao_test123','wb_xinmin.zhao_test124'" + ")";

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

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(1, visitor.getColumns().size());

         Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("BILLING_LOG_MONITOR")));
        // Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
        //
         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("BILLING_LOG_MONITOR", "guid")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "commission_pct")));
    }

}
