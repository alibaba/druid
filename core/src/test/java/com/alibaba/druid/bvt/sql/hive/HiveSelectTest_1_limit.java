package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class HiveSelectTest_1_limit extends TestCase {
    public void test_select() throws Exception {
        String sql = "SELECT * FROM customers ORDER BY create_date LIMIT 2,5";//
        assertEquals("SELECT *\n" +
                "FROM customers\n" +
                "ORDER BY create_date\n" +
                "LIMIT 2, 5", SQLUtils.formatHive(sql));
        assertEquals("select *\n" +
                "from customers\n" +
                "order by create_date\n" +
                "limit 2, 5", SQLUtils.formatHive(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(2, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsColumn("customers", "create_date"));
        assertTrue(visitor.containsColumn("customers", "*"));
    }
}
