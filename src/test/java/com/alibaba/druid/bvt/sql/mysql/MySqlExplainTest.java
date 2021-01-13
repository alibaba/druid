/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExplainStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

/**
 * See https://dev.mysql.com/doc/refman/5.7/en/explain.html for test cases.
 */
public class MySqlExplainTest extends MysqlTest {

    public void testExplainTable() throws Exception {
        String sql = " EXPLAIN City";
        SQLStatement statement = testParseFormat(sql);

        MySqlSchemaStatVisitor stats = schemaStats(statement);
        assertHasSeenXTables(stats, 1);
        assertHasSeenXColumns(stats, 0);
        assertHasSeenXConditions(stats, 0);
        assertHasSeenTable(stats, "City");
    }

    public void testExplainTableWithColumn() throws Exception {
        String sql = " EXPLAIN City name";
        SQLStatement statement = testParseFormat(sql);

        MySqlSchemaStatVisitor stats = schemaStats(statement);
        assertHasSeenXTables(stats, 1);
        assertHasSeenXColumns(stats, 1);
        assertHasSeenXConditions(stats, 0);
        assertHasSeenTable(stats, "City");
        assertHasSeenTableColumn(stats, "City", "name");
    }

    public void testExplainTableWithWild() throws Exception {
        String sql = " EXPLAIN City '%ame'";
        SQLStatement statement = testParseFormat(sql);

        MySqlSchemaStatVisitor stats = schemaStats(statement);
        assertHasSeenXTables(stats, 1);
        assertHasSeenXColumns(stats, 0);
        assertHasSeenXConditions(stats, 0);
        assertHasSeenTable(stats, "City");
    }

    public void testExplainExplainableStatements() throws Exception {
        testExplainExplainableStatement("EXPLAIN SELECT * FROM City", "City", 1);
        testExplainExplainableStatement("EXPLAIN DELETE FROM City", "City", 0);
        testExplainExplainableStatement("EXPLAIN INSERT INTO City (name) VALUES ('Beijing')", "City", 1);
        testExplainExplainableStatement("EXPLAIN REPLACE INTO City (name) VALUES ('Beijing')", "City", 1);
        testExplainExplainableStatement("EXPLAIN UPDATE City SET name = 'Beijing'", "City", 1);
    }

    public void testExplainExtendedExplainableStatements() throws Exception {
        testExplainExplainableStatement("EXPLAIN EXTENDED SELECT * FROM City", "City", 1);
        testExplainExplainableStatement("EXPLAIN EXTENDED DELETE FROM City", "City", 0);
        testExplainExplainableStatement("EXPLAIN EXTENDED INSERT INTO City (name) VALUES ('Beijing')", "City", 1);
        testExplainExplainableStatement("EXPLAIN EXTENDED REPLACE INTO City (name) VALUES ('Beijing')", "City", 1);
        testExplainExplainableStatement("EXPLAIN EXTENDED UPDATE City SET name = 'Beijing'", "City", 1);
    }

    public void testExplainPartitionsExplainableStatements() throws Exception {
        testExplainExplainableStatement("EXPLAIN PARTITIONS SELECT * FROM City", "City", 1);
        testExplainExplainableStatement("EXPLAIN PARTITIONS DELETE FROM City", "City", 0);
        testExplainExplainableStatement("EXPLAIN PARTITIONS INSERT INTO City (name) VALUES ('Beijing')", "City", 1);
        testExplainExplainableStatement("EXPLAIN PARTITIONS REPLACE INTO City (name) VALUES ('Beijing')", "City", 1);
        testExplainExplainableStatement("EXPLAIN PARTITIONS UPDATE City SET name = 'Beijing'", "City", 1);
    }

    public void testExplainFormatTraditionalExplainableStatements() throws Exception {
        testExplainExplainableStatement("EXPLAIN FORMAT = TRADITIONAL SELECT * FROM City", "City", 1);
        testExplainExplainableStatement("EXPLAIN FORMAT = TRADITIONAL DELETE FROM City", "City", 0);
        testExplainExplainableStatement("EXPLAIN FORMAT = TRADITIONAL INSERT INTO City (name) VALUES ('Beijing')", "City", 1);
        testExplainExplainableStatement("EXPLAIN FORMAT = TRADITIONAL REPLACE INTO City (name) VALUES ('Beijing')", "City", 1);
        testExplainExplainableStatement("EXPLAIN FORMAT = TRADITIONAL UPDATE City SET name = 'Beijing'", "City", 1);
    }

    public void testExplainFormatJsonExplainableStatements() throws Exception {
        testExplainExplainableStatement("EXPLAIN FORMAT = JSON SELECT * FROM City", "City", 1);
        testExplainExplainableStatement("EXPLAIN FORMAT = JSON DELETE FROM City", "City", 0);
        testExplainExplainableStatement("EXPLAIN FORMAT = JSON INSERT INTO City (name) VALUES ('Beijing')", "City", 1);
        testExplainExplainableStatement("EXPLAIN FORMAT = JSON REPLACE INTO City (name) VALUES ('Beijing')", "City", 1);
        testExplainExplainableStatement("EXPLAIN FORMAT = JSON UPDATE City SET name = 'Beijing'", "City", 1);
    }

    public void testExplainForConnections() throws Exception {
        testExplainForConnection("EXPLAIN FOR CONNECTION 1");
        testExplainForConnection("EXPLAIN EXTENDED FOR CONNECTION 1");
        testExplainForConnection("EXPLAIN PARTITIONS FOR CONNECTION 1");
        testExplainForConnection("EXPLAIN FORMAT = TRADITIONAL FOR CONNECTION 1");
        testExplainForConnection("EXPLAIN FORMAT = JSON FOR CONNECTION 1");
    }

    public void testExplainForConnection(String sql) throws Exception {
        SQLStatement statement = testParseFormat(sql);

        MySqlSchemaStatVisitor stats = schemaStats(statement);
        assertHasSeenXTables(stats, 0);
        assertHasSeenXColumns(stats, 0);
        assertHasSeenXConditions(stats, 0);
    }

    private SQLStatement testParseFormat(String sql) {
        List<SQLStatement> statements = parseList(sql);
        assertStatements(statements);

        SQLStatement statement = statements.get(0);
        assertStatement(statement);

        String output = format(statement);
        assertSqlEquals(sql, output);

        return statement;
    }

    private void testExplainExplainableStatement(String sql, String tableName, int columns) {
        SQLStatement statement = testParseFormat(sql);

        MySqlSchemaStatVisitor stats = schemaStats(statement);
        assertHasSeenXTables(stats, 1);
        assertHasSeenXColumns(stats, columns);
        assertHasSeenXConditions(stats, 0);

        assertHasSeenTable(stats, tableName);
    }

    private String format(SQLStatement statement) {
        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);
        statement.accept(visitor);

        return out.toString();
    }

    private MySqlSchemaStatVisitor schemaStats(SQLStatement stmt) {
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        return visitor;
    }

    private void assertSqlEquals(String sql, String output) {
        assertEquals(sql.trim(), output.replaceAll("\\s+", " "));
    }

    private void assertStatement(SQLStatement statement) {
        assertTrue(statement instanceof MySqlExplainStatement);
    }

    private void assertStatements(List<SQLStatement> statements) {
        assertEquals(1, statements.size());
    }

    private void assertHasSeenXTables(MySqlSchemaStatVisitor visitor, int x) {
        assertEquals(x, visitor.getTables().size());
    }

    private void assertHasSeenXColumns(MySqlSchemaStatVisitor visitor, int x) {
        assertEquals(x, visitor.getColumns().size());
    }

    private void assertHasSeenXConditions(MySqlSchemaStatVisitor visitor, int x) {
        assertEquals(x, visitor.getConditions().size());
    }

    private static void assertHasSeenTable(MySqlSchemaStatVisitor visitor, String tableName) {
        assertTrue(visitor.getTables().containsKey(new TableStat.Name(tableName)));
    }

    private static void assertHasSeenTableColumn(MySqlSchemaStatVisitor visitor, String tableName, String columnName) {
        assertTrue(visitor.getColumns().contains(new TableStat.Column(tableName, columnName)));
    }

}
