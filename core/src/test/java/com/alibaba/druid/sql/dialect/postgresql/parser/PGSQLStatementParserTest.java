package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author gaattc
 * @since 1.2.24
 */
public class PGSQLStatementParserTest {

    /**
     * ALTER COLUMN ... SET DATA TYPE ...
     */
    @Test
    public void testAlterColumnSetDataType() {
        String sql = "alter table if exists products alter column price set data type decimal(12,2)";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) statementList.get(0);
        assertEquals(SQLAlterTableStatement.class, stmt.getClass());

        assertEquals("products", stmt.getTableName());
        assertTrue(stmt.isIfExists());

        SQLAlterTableAlterColumn alterColumnItem = (SQLAlterTableAlterColumn) stmt.getItems().get(0);
        assertNotNull(alterColumnItem);

        assertEquals("price", alterColumnItem.getColumn().getName().getSimpleName());
        assertNotNull(alterColumnItem.getDataType());
        assertEquals("decimal", alterColumnItem.getDataType().getName().toLowerCase());
        assertEquals(2, alterColumnItem.getDataType().getArguments().size());
        assertEquals("12", alterColumnItem.getDataType().getArguments().get(0).toString());
        assertEquals("2", alterColumnItem.getDataType().getArguments().get(1).toString());

        String outputSql = stmt.toString();
        assertEquals("ALTER TABLE IF EXISTS products\n\tALTER COLUMN price SET DATA TYPE decimal(12, 2)", outputSql);
    }

    /**
     * ALTER COLUMN ... SET DEFAULT ...
     */
    @Test
    public void testAlterColumnSetDefault() {
        String sql = "ALTER TABLE products ALTER COLUMN price SET DEFAULT 7.77";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) statementList.get(0);

        SQLAlterTableAlterColumn alterColumnItem = (SQLAlterTableAlterColumn) stmt.getItems().get(0);

        assertEquals("price", alterColumnItem.getColumn().getName().getSimpleName());
        assertNotNull(alterColumnItem.getSetDefault());
        assertEquals("7.77", alterColumnItem.getSetDefault().toString());
        assertNull(alterColumnItem.getDataType());
    }

    /**
     * ALTER COLUMN ... SET NOT NULL
     */
    @Test
    public void testAlterColumnSetNotNull() {
        String sql = "ALTER TABLE products ALTER COLUMN price SET NOT NULL";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) statementList.get(0);

        SQLAlterTableAlterColumn alterColumnItem = (SQLAlterTableAlterColumn) stmt.getItems().get(0);

        assertEquals("price", alterColumnItem.getColumn().getName().getSimpleName());
        assertTrue(alterColumnItem.isSetNotNull());
        assertNull(alterColumnItem.getDataType());
    }

    /**
     * DROP
     */
    @Test
    public void testAlterColumnDropDefault() {
        String sql = "ALTER TABLE products ALTER COLUMN price DROP DEFAULT";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) statementList.get(0);

        SQLAlterTableAlterColumn alterColumnItem = (SQLAlterTableAlterColumn) stmt.getItems().get(0);

        assertEquals("price", alterColumnItem.getColumn().getName().getSimpleName());
        assertTrue(alterColumnItem.isDropDefault());
        assertNull(alterColumnItem.getDataType());
    }

    /**
     * 测试核心功能：ALTER COLUMN ... SET DATA TYPE ... USING ...
     */
    @Test
    public void testAlterColumnSetDataTypeWithUsing() {
        String sql = "ALTER TABLE my_table ALTER COLUMN my_column SET DATA TYPE integer USING my_column::integer";

        SQLAlterTableStatement stmt = parseSingleAlterStatement(sql);

        // 验证表名
        assertEquals("my_table", stmt.getTableName());

        SQLAlterTableAlterColumn item = (SQLAlterTableAlterColumn) stmt.getItems().get(0);

        // 验证列名
        assertEquals("my_column", item.getColumn().getName().getSimpleName());

        // 验证新数据类型
        assertEquals("integer", item.getDataType().getName().toLowerCase());

        // 验证 USING 子句
        assertNotNull("USING clause should be parsed", item.getUsing());
        assertTrue("USING expression should be a CAST expression", item.getUsing() instanceof SQLCastExpr);
        assertEquals("my_column::integer", SQLUtils.toPGString(item.getUsing()));
    }

    /**
     * 边界场景测试：测试带有 schema 的复杂类型名
     */
    @Test
    public void testAlterColumnWithSchemaQualifiedType() {
        String sql = "ALTER TABLE public.users ALTER COLUMN user_status SET DATA TYPE custom_schema.user_enum USING user_status::text::custom_schema.user_enum";

        SQLAlterTableStatement stmt = parseSingleAlterStatement(sql);

        // 验证带 schema 的表名
        assertEquals("public.users", stmt.getTableSource().toString());

        SQLAlterTableAlterColumn item = (SQLAlterTableAlterColumn) stmt.getItems().get(0);

        // 验证数据类型
        assertEquals("custom_schema.user_enum", item.getDataType().toString());

        // 验证 USING 子句
        assertNotNull(item.getUsing());
        assertEquals("custom_schema.user_enum(user_status::text)", SQLUtils.toPGString(item.getUsing()));
    }

    /**
     * 测试 Visitor 还原 PG 语法的完整性
     * 确保解析后的AST能够被 Visitor 正确地还原为原始 SQL 或等价的 PG SQL。
     */
    @Test
    public void testVisitorOutputCompleteness() {
        String originalSql = "ALTER TABLE my_table ALTER COLUMN my_column SET DATA TYPE integer USING my_column::integer";
        SQLStatement stmt = parseSingleAlterStatement(originalSql);

        // 使用 SQLUtils (其内部使用 Visitor) 来格式化 SQL
        String formattedSql = SQLUtils.toSQLString(stmt, DbType.postgresql, new SQLUtils.FormatOption(true, true));

        String expectedFormattedSql =
                "ALTER TABLE my_table\n" +
                        "\tALTER COLUMN my_column SET DATA TYPE integer USING my_column::integer";

        assertEquals(expectedFormattedSql, formattedSql);
    }

    /**
     * 测试 Visitor 统计功能的正确性
     */
    @Test
    public void testSchemaStatVisitorWithUsing() {
        String sql = "ALTER TABLE my_table ALTER COLUMN my_column SET DATA TYPE integer USING old_column::integer";

        // 使用 SQLUtils.parseStatements 来简化解析
        SQLStatement stmt = SQLUtils.parseStatements(sql, DbType.postgresql).get(0);

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

        // 验证表被识别
        assertTrue("The table 'my_table' should be present in the stats.",
                visitor.containsTable("my_table"));

        // 验证 ALTER COLUMN <column> 被识别
        TableStat.Column myColumnStat = visitor.getColumn("my_table", "my_column");
        assertNotNull("The altered column 'my_column' should be identified by the visitor.", myColumnStat);

        // 验证 USING <column> 被识别
        TableStat.Column oldColumnStat = visitor.getColumn("my_table", "old_column");
        assertNotNull("The column 'old_column' from the USING clause should be identified by the visitor.", oldColumnStat);
    }

    /**
     * 异常场景测试：测试非法的 'SET DATA' 语法
     */
    @Test(expected = ParserException.class)
    public void testInvalidSyntax_SetDataWithoutType() {
        String sql = "ALTER TABLE my_table ALTER COLUMN my_column SET DATA my_column";
        // 应该在解析 'my_column' 时报错，因为它期望 'TYPE'
        parseSingleAlterStatement(sql);
    }

    /**
     * 异常场景测试：测试 'SET DATA TYPE' 后缺少类型定义
     */
    @Test(expected = ParserException.class)
    public void testInvalidSyntax_SetDataTypeWithoutDefinition() {
        String sql = "ALTER TABLE my_table ALTER COLUMN my_column SET DATA TYPE";
        // 解析到末尾，发现缺少数据类型，应该报错
        parseSingleAlterStatement(sql);
    }

    /**
     * 异常场景测试：测试 'USING' 关键字后缺少表达式
     */
    @Test(expected = ParserException.class)
    public void testInvalidSyntax_UsingWithoutExpression() {
        String sql = "ALTER TABLE my_table ALTER COLUMN my_column SET DATA TYPE integer USING";
        // 解析到末尾，发现缺少 USING 表达式，应该报错
        parseSingleAlterStatement(sql);
    }

    /**
     * 辅助方法，用于解析单条 ALTER 语句并返回其 AST
     * @param sql SQL 字符串
     * @return SQLAlterTableStatement 对象
     */
    private SQLAlterTableStatement parseSingleAlterStatement(String sql) {
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        if (statementList.size() != 1) {
            Assert.fail("Expected a single statement, but got " + statementList.size());
        }

        SQLStatement stmt = statementList.get(0);
        if (!(stmt instanceof SQLAlterTableStatement)) {
            Assert.fail("Expected SQLAlterTableStatement, but got " + stmt.getClass().getName());
        }
        return (SQLAlterTableStatement) stmt;
    }

}