package com.alibaba.druid.bvt.sql.yashandb;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ExportParameterVisitorUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class YashanDbExportParameterVisitorTest {
    @Test
    public void test_export_parameter_select() throws Exception {
        String sql = "SELECT * FROM t WHERE id = 1 AND name = 'test'";
        DbType dbType = DbType.yashandb;

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();

        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = (ExportParameterVisitor) ExportParameterVisitorUtils.createExportParameterVisitor(out, dbType);
        assertNotNull(visitor);

        stmt.accept(visitor);
        assertEquals(2, visitor.getParameters().size());
    }

    @Test
    public void test_export_parameter_insert() throws Exception {
        String sql = "INSERT INTO t (id, name) VALUES (1, 'hello')";
        DbType dbType = DbType.yashandb;

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();

        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = (ExportParameterVisitor) ExportParameterVisitorUtils.createExportParameterVisitor(out, dbType);
        stmt.accept(visitor);
        assertEquals(2, visitor.getParameters().size());
    }

    @Test
    public void test_export_parameter_update() throws Exception {
        String sql = "UPDATE t SET name = 'new' WHERE id = 1";
        DbType dbType = DbType.yashandb;

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();

        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = (ExportParameterVisitor) ExportParameterVisitorUtils.createExportParameterVisitor(out, dbType);
        stmt.accept(visitor);
        assertEquals(2, visitor.getParameters().size());
    }

    @Test
    public void test_export_parameter_delete() throws Exception {
        String sql = "DELETE FROM t WHERE id = 1 AND status = 'inactive'";
        DbType dbType = DbType.yashandb;

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();

        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = (ExportParameterVisitor) ExportParameterVisitorUtils.createExportParameterVisitor(out, dbType);
        stmt.accept(visitor);
        assertEquals(2, visitor.getParameters().size());
    }
}
