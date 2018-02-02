package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveSelectTest_2_lateralview extends TestCase {
    public void test_select() throws Exception {
        String sql = "SELECT pageid, adid\n" +
                "FROM pageAds LATERAL VIEW explode(adid_list) adTable AS adid;";//
        assertEquals("SELECT pageid, adid\n" +
                "FROM pageAds\n" +
                "\tLATERAL VIEW explode(adid_list) adTable AS adid;", SQLUtils.formatHive(sql));
        assertEquals("select pageid, adid\n" +
                "from pageAds\n" +
                "\tlateral view explode(adid_list) adTable as adid;", SQLUtils.formatHive(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(2, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsColumn("pageAds", "adid_list"));
        assertTrue(visitor.containsColumn("pageAds", "pageid"));
    }
}
