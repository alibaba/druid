package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveInsert_3 extends TestCase {
    public void test_select() throws Exception {
        String sql = "FROM sale_detail\n" +
                "INSERT OVERWRITE TABLE sale_detail_multi partition (sale_date='2010', region='china' )\n" +
                "SELECT shop_name, customer_id, total_price\n" +
                "INSERT OVERWRITE TABLE sale_detail_multi partition (sale_date='2010', region='china' )\n" +
                "SELECT shop_name, customer_id, total_price;";//

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        assertEquals("FROM sale_detail\n" +
                "INSERT OVERWRITE TABLE sale_detail_multi PARTITION (sale_date='2010', region='china')\n" +
                "SELECT shop_name, customer_id, total_price\n" +
                "INSERT OVERWRITE TABLE sale_detail_multi PARTITION (sale_date='2010', region='china')\n" +
                "SELECT shop_name, customer_id, total_price;", SQLUtils.formatHive(sql));

        assertEquals("from sale_detail\n" +
                "insert overwrite table sale_detail_multi partition (sale_date='2010', region='china')\n" +
                "select shop_name, customer_id, total_price\n" +
                "insert overwrite table sale_detail_multi partition (sale_date='2010', region='china')\n" +
                "select shop_name, customer_id, total_price;", SQLUtils.formatHive(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));


        System.out.println("Tables : " + visitor.getTables());
      System.out.println("fields : " + visitor.getColumns());
      System.out.println("coditions : " + visitor.getConditions());
      System.out.println("orderBy : " + visitor.getOrderByColumns());



        assertEquals(2, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsColumn("sale_detail_multi", "sale_date"));
        assertTrue(visitor.containsColumn("sale_detail_multi", "region"));
        assertTrue(visitor.containsColumn("sale_detail", "shop_name"));
        assertTrue(visitor.containsColumn("sale_detail", "customer_id"));
        assertTrue(visitor.containsColumn("sale_detail", "total_price"));


    }
}
