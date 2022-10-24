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

import static org.junit.Assert.*;

public class PrestoSelect_0 {
    protected final DbType dbType = DbType.presto;

    @Test
    public void test_with() throws Exception {
        String sql = "WITH RECURSIVE t(n) AS (\n" +
                "    VALUES (1)\n" +
                "    UNION ALL\n" +
                "    SELECT n + 1 FROM t WHERE n < 4\n" +
                ")\n" +
                "SELECT sum(n) FROM t;";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertTrue(tableMap.isEmpty());
        assertEquals("WITH RECURSIVE t (n) AS (\n" +
                "\t\tVALUES (1)\n" +
                "\t\tUNION ALL\n" +
                "\t\tSELECT n + 1\n" +
                "\t\tFROM t\n" +
                "\t\tWHERE n < 4\n" +
                "\t)\n" +
                "SELECT sum(n)\n" +
                "FROM t", stmt.toString());
    }

    @Test
    public void test_unnest_0() throws Exception {
        String sql = "SELECT student, score\n" +
                "FROM tests\n" +
                "CROSS JOIN UNNEST(scores) AS t (score);";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertEquals("SELECT student, score\n" +
                "FROM tests\n" +
                "\tCROSS JOIN UNNEST(scores) AS t (score)", stmt.toString());
    }

    @Test
    public void test_unnest_1() throws Exception {
        String sql = "SELECT numbers, animals, n, a\n" +
                "FROM (\n" +
                "  VALUES\n" +
                "    (ARRAY[2, 5], ARRAY['dog', 'cat', 'bird']),\n" +
                "    (ARRAY[7, 8, 9], ARRAY['cow', 'pig'])\n" +
                ") AS x (numbers, animals)\n" +
                "CROSS JOIN UNNEST(numbers, animals) AS t (n, a);";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertTrue(tableMap.isEmpty());
        assertEquals("SELECT numbers, animals, n, a\n" +
                "FROM (VALUES (ARRAY[2, 5], ARRAY['dog', 'cat', 'bird']), (ARRAY[7, 8, 9], ARRAY['cow', 'pig'])) AS x (numbers, animals)\n" +
                "\tCROSS JOIN UNNEST(numbers, animals) AS t (n, a)", stmt.toString());
    }

    @Test
    public void test_unnest_2() throws Exception {
        String sql = "SELECT numbers, n, a\n" +
                "FROM (\n" +
                "  VALUES\n" +
                "    (ARRAY[2, 5]),\n" +
                "    (ARRAY[7, 8, 9])\n" +
                ") AS x (numbers)\n" +
                "CROSS JOIN UNNEST(numbers) WITH ORDINALITY AS t (n, a);";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertTrue(tableMap.isEmpty());
        assertEquals("SELECT numbers, n, a\n" +
                "FROM (VALUES (ARRAY[2, 5]), (ARRAY[7, 8, 9])) AS x (numbers)\n" +
                "\tCROSS JOIN UNNEST(numbers) WITH ORDINALITY AS t (n, a)", stmt.toString());
    }

    @Test
    public void test_lateral() throws Exception {
        String sql = "SELECT name, x, y\n" +
                "FROM nation\n" +
                "CROSS JOIN LATERAL (SELECT name || ' :-' AS x)\n" +
                "CROSS JOIN LATERAL (SELECT x || ')' AS y);";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertEquals("SELECT name, x, y\n" +
                "FROM nation\n" +
                "\tCROSS JOIN LATERAL(\n" +
                "\t\tSELECT name || ' :-' AS x\n" +
                "\t)\n" +
                "\tCROSS JOIN LATERAL(\n" +
                "\t\tSELECT x || ')' AS y\n" +
                "\t)", stmt.toString());
    }

    @Test
    public void test_exists() throws Exception {
        String sql = "SELECT name\n" +
                "FROM nation\n" +
                "WHERE EXISTS (\n" +
                "     SELECT *\n" +
                "     FROM region\n" +
                "     WHERE region.regionkey = nation.regionkey\n" +
                ");";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertEquals("SELECT name\n" +
                "FROM nation\n" +
                "WHERE EXISTS (\n" +
                "\tSELECT *\n" +
                "\tFROM region\n" +
                "\tWHERE region.regionkey = nation.regionkey\n" +
                ")", stmt.toString());
    }

    @Test
    public void test_in() throws Exception {
        String sql = "SELECT name\n" +
                "FROM nation\n" +
                "WHERE regionkey IN (\n" +
                "     SELECT regionkey\n" +
                "     FROM region\n" +
                "     WHERE name = 'AMERICA' OR name = 'AFRICA'\n" +
                ");";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertEquals("SELECT name\n" +
                "FROM nation\n" +
                "WHERE regionkey IN (\n" +
                "\tSELECT regionkey\n" +
                "\tFROM region\n" +
                "\tWHERE name = 'AMERICA'\n" +
                "\t\tOR name = 'AFRICA'\n" +
                ")", stmt.toString());
    }

    @Test
    public void test_scalar() throws Exception {
        String sql = "SELECT name\n" +
                "FROM nation\n" +
                "WHERE regionkey = (SELECT max(regionkey) FROM region);";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertEquals("SELECT name\n" +
                "FROM nation\n" +
                "WHERE regionkey = (\n" +
                "\tSELECT max(regionkey)\n" +
                "\tFROM region\n" +
                ")", stmt.toString());
    }

}
