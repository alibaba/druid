package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveInsert_1 extends TestCase {
    public void test_select() throws Exception {
        String sql = "FROM page_view_stg pvs\n" +
                "INSERT OVERWRITE TABLE page_view PARTITION(dt='2008-06-08', country)\n" +
                "       SELECT pvs.viewTime, pvs.userid, pvs.page_url, pvs.referrer_url, null, null, pvs.ip, pvs.cnt";//

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        assertEquals("FROM page_view_stg pvs\n" +
                "INSERT OVERWRITE TABLE page_view PARTITION (dt='2008-06-08', country)\n" +
                "SELECT pvs.viewTime, pvs.userid, pvs.page_url, pvs.referrer_url, NULL\n" +
                "\t, NULL, pvs.ip, pvs.cnt", SQLUtils.formatHive(sql));

        assertEquals("from page_view_stg pvs\n" +
                "insert overwrite table page_view partition (dt='2008-06-08', country)\n" +
                "select pvs.viewTime, pvs.userid, pvs.page_url, pvs.referrer_url, null\n" +
                "\t, null, pvs.ip, pvs.cnt", SQLUtils.formatHive(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));


//        System.out.println("Tables : " + visitor.getTables());
//      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(8, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

    }
}
