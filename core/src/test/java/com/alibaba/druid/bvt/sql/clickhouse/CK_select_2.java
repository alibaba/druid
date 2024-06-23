package com.alibaba.druid.bvt.sql.clickhouse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import junit.framework.TestCase;

import java.util.List;

public class CK_select_2 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select a from cluster('test', view( select a from t1))";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.clickhouse);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("t1"));

        // assertTrue(visitor.getColumns().contains(new Column("mytable", "last_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, DbType.clickhouse);
        assertEquals("SELECT a\n" +
                        "FROM cluster('test', view(\n" +
                        "\tSELECT a\n" +
                        "\tFROM t1\n" +
                        "))", //
                output);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.clickhouse);
        assertEquals("SELECT a\n" +
                "FROM cluster(?, view(\n" +
                "\tSELECT a\n" +
                "\tFROM t1\n" +
                "))", psql);
    }
}
