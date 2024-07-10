package com.alibaba.druid.bvt.sql.clickhouse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import junit.framework.TestCase;

import java.util.List;

public class CK_select_3 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT name, c FROM test WHERE c NOT ILIKE 'a%'";

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
        assertEquals(2, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("test"));

        // assertTrue(visitor.getColumns().contains(new Column("mytable", "last_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, DbType.clickhouse);
        assertEquals("SELECT name, c\n"+
        "FROM test\n"+
        "WHERE c NOT ILIKE 'a%'", output);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.clickhouse);
        assertEquals("SELECT name, c\n"+
        "FROM test\n"+
        "WHERE c NOT ILIKE ?", psql);
    }

    public void test_1() throws Exception {
        String sql = "SELECT name, c FROM test WHERE c ILIKE 'a%'";

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
        assertEquals(2, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("test"));

        // assertTrue(visitor.getColumns().contains(new Column("mytable", "last_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, DbType.clickhouse);
        assertEquals("SELECT name, c\n"+
        "FROM test\n"+
        "WHERE c ILIKE 'a%'", output);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.clickhouse);
        assertEquals("SELECT name, c\n"+
        "FROM test\n"+
        "WHERE c ILIKE ?", psql);
    }
}
