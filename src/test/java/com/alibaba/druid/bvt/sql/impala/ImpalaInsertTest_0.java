package com.alibaba.druid.bvt.sql.impala;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class ImpalaInsertTest_0 extends TestCase {
    public void test_create() throws Exception {
        String sql = "INSERT INTO t1 PARTITION (x=10, y='a') SELECT c1 FROM some_other_table;";//
        assertEquals("INSERT INTO TABLE t1 PARTITION (x=10, y='a')\n" +
            "SELECT c1\n" +
            "FROM some_other_table;", SQLUtils.formatImpala(sql));
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.IMPALA);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.IMPALA);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
    }
}
