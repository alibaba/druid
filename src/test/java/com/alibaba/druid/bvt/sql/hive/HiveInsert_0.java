package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveInsert_0 extends TestCase {
    public void test_select() throws Exception {
        String sql = "INSERT INTO TABLE students\n" +
                "  VALUES ('fred flintstone', 35, 1.28), ('barney rubble', 32, 2.32);";//

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        assertEquals("INSERT INTO TABLE students\n" +
                "VALUES ('fred flintstone', 35, 1.28), ('barney rubble', 32, 2.32);", SQLUtils.formatHive(sql));

        assertEquals("insert into table students\n" +
                "values ('fred flintstone', 35, 1.28), ('barney rubble', 32, 2.32);", SQLUtils.formatHive(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));


//        System.out.println("Tables : " + visitor.getTables());
//      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

    }
}
