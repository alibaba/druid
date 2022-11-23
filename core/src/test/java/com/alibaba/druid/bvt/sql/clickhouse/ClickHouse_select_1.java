package com.alibaba.druid.bvt.sql.clickhouse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import junit.framework.TestCase;

import java.util.List;

public class ClickHouse_select_1 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT * FROM limit_by ORDER BY id, val LIMIT 2 BY id";

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
        assertEquals(3, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("limit_by"));

        // assertTrue(visitor.getColumns().contains(new Column("mytable", "last_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, DbType.clickhouse);
        assertEquals("SELECT *\n" +
                        "FROM limit_by\n" +
                        "ORDER BY id, val\n" +
                        "LIMIT 2 BY id", //
                output);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.clickhouse);
        assertEquals("SELECT *\n" +
                "FROM limit_by\n" +
                "ORDER BY id, val\n" +
                "LIMIT ? BY id", psql);
    }
}
