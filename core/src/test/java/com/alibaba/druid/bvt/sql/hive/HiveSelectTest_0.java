package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class HiveSelectTest_0 extends TestCase {
    public void test_select() throws Exception {
        String sql = "SELECT page_views.*\n" +
                "FROM page_views\n" +
                "WHERE page_views.date >= '2008-03-01' AND page_views.date <= '2008-03-31'";//
        Assert.assertEquals("SELECT page_views.*\n" +
                "FROM page_views\n" +
                "WHERE page_views.date >= '2008-03-01'\n" +
                "\tAND page_views.date <= '2008-03-31'", SQLUtils.formatHive(sql));
        Assert.assertEquals("select page_views.*\n" +
                "from page_views\n" +
                "where page_views.date >= '2008-03-01'\n" +
                "\tand page_views.date <= '2008-03-31'", SQLUtils.formatHive(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.containsColumn("page_views", "date"));
        Assert.assertTrue(visitor.containsColumn("page_views", "*"));
    }
}
