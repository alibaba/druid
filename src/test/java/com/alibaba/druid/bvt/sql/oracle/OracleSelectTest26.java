package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest26 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "select * from ( select rownum rnm, z.* from (select " +
        "   o.id,o.gmt_create as \"gm_create\",o.country as \"country\",o.status as \"status\"," +
        "   o.logistics_company as \"company\",o.type as \"type\",o.freight/100 as \"feight\"," +
        "   d.gmt_create as \"gm_create_1\",d.logistics_company as \"company_1\",d.package_num as \" \",d.declare_amount as \" \"," +
        "   i.gmt_stockin as \" \",i.package_amount as \" \",i.weight as \" \"," +
        "   u.gmt_create as \" \",u.logistics_company as \" \",u.package_amount as \" \"" +
        " from wl_wh_order o left join wl_domestic_send d on d.wh_order_id=o.id left join wl_wh_in i on i.wh_order_id=o.id left join wl_wh_out u on u.out_order_id=o.id" +
        " where o.id=100120667) z where rownum < :1 ) where rnm >= :2 ";

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

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("\"DUAL\"")));

        Assert.assertEquals(0, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
