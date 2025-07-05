package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
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


}