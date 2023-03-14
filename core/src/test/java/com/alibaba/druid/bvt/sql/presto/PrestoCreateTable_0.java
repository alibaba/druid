package com.alibaba.druid.bvt.sql.presto;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PrestoCreateTable_0 {
    private final DbType dbType = DbType.presto;

    @Test
    public void test_create_table_with() {
        String sql = "CREATE TABLE orders (\n" +
                "  orderkey bigint,\n" +
                "  orderstatus varchar,\n" +
                "  totalprice double,\n" +
                "  orderdate date\n" +
                ")\n" +
                "WITH (format = 'ORC')";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertEquals("CREATE TABLE orders (\n" +
                "\torderkey bigint,\n" +
                "\torderstatus varchar,\n" +
                "\ttotalprice double,\n" +
                "\torderdate date\n" +
                ")\n" +
                "WITH (format = 'ORC')", stmt.toString());
    }

    @Test
    public void test_create_table_comment() {
        String sql = "CREATE TABLE IF NOT EXISTS orders (\n" +
                "  orderkey bigint,\n" +
                "  orderstatus varchar,\n" +
                "  totalprice double COMMENT 'Price in cents.',\n" +
                "  orderdate date\n" +
                ")\n" +
                "COMMENT 'A table to keep track of orders.'";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        SQLStatement stmt = parser.parseStatement();

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertEquals("CREATE TABLE IF NOT EXISTS orders (\n" +
                "\torderkey bigint,\n" +
                "\torderstatus varchar,\n" +
                "\ttotalprice double COMMENT 'Price in cents.',\n" +
                "\torderdate date\n" +
                ")\n" +
                "COMMENT 'A table to keep track of orders.'", stmt.toString());
    }

    @Test
    public void test_create_table_column_like_table() {
        String sql = "CREATE TABLE bigger_orders (\n" +
                "  another_orderkey bigint,\n" +
                "  LIKE orders,\n" +
                "  another_orderdate date\n" +
                ")";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        SQLStatement stmt = parser.parseStatement();

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertEquals("CREATE TABLE bigger_orders (\n" +
                "\tanother_orderkey bigint,\n" +
                "\tLIKE orders,\n" +
                "\tanother_orderdate date\n" +
                ")", stmt.toString());
    }

    @Test
    public void test_create_table_as_select_0() {
        String sql = "CREATE TABLE orders_column_aliased (order_date, total_price)\n" +
                "AS\n" +
                "SELECT orderdate, totalprice\n" +
                "FROM orders";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        SQLStatement stmt = parser.parseStatement();

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertEquals("CREATE TABLE orders_column_aliased (\n" +
                "\torder_date,\n" +
                "\ttotal_price\n" +
                ")\n" +
                "AS\n" +
                "SELECT orderdate, totalprice\n" +
                "FROM orders", stmt.toString());
    }

    @Test
    public void test_create_table_as_select_1() {
        String sql = "CREATE TABLE orders_by_date\n" +
                "COMMENT 'Summary of orders by date'\n" +
                "WITH (format = 'ORC')\n" +
                "AS\n" +
                "SELECT orderdate, sum(totalprice) AS price\n" +
                "FROM orders\n" +
                "GROUP BY orderdate";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        SQLStatement stmt = parser.parseStatement();

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertEquals("CREATE TABLE orders_by_date\n" +
                "COMMENT 'Summary of orders by date'\n" +
                "WITH (format = 'ORC')\n" +
                "AS\n" +
                "SELECT orderdate, sum(totalprice) AS price\n" +
                "FROM orders\n" +
                "GROUP BY orderdate", stmt.toString());
    }
}
