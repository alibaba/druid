package com.alibaba.druid.bvt.sql.impala;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class ImpalaCreateTableTest_0 extends TestCase {
    public void test_create() throws Exception {
        String sql = "CREATE TABLE IF NOT Exists pk(col1 INT, col2 STRING, PRIMARY KEY(col1, col2))";//
        assertEquals("CREATE TABLE IF NOT EXISTS pk (\n" +
            "\tcol1 INT,\n" +
            "\tcol2 STRING,\n" +
            "\tPRIMARY KEY (col1, col2)\n" +
            ");", SQLUtils.formatImpala(sql));
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.IMPALA);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.IMPALA);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
    }
}
