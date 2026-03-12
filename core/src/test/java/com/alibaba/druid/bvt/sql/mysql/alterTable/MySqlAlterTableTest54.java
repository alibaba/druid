package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.ParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAlterTableTest54 {
    @Test
    public void test_0() throws Exception {
        String sql = "alter table event_log storage_policy = 'HOT'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatementList().get(0);
        assertEquals(1, stmt.getTableOptions().size());
        String formatSql = SQLUtils.toSQLString(stmt);
        assertEquals("ALTER TABLE event_log\n" +
                "\tSTORAGE_POLICY = 'HOT'", formatSql);
    }

    @Test
    public void test_1() throws Exception {
        String sql = "alter table event_log storage_policy = 'COLD'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        String formatSql = SQLUtils.toSQLString(stmt);
        assertEquals("ALTER TABLE event_log\n" +
                "\tSTORAGE_POLICY = 'COLD'", formatSql);
    }

    @Test
    public void test_2() throws Exception {
        String sql = "alter table event_log storage_policy = 'MIXED' hot_partition_count = 10;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatementList().get(0);
        assertEquals(2, stmt.getTableOptions().size());
        String formatSql = SQLUtils.toSQLString(stmt);
        assertEquals("ALTER TABLE event_log\n" +
                "\tSTORAGE_POLICY = 'MIXED' HOT_PARTITION_COUNT = 10;", formatSql);
    }

    @Test
    public void test_3() throws Exception {
        String sql = "alter table event_log hot_partition_count = 10;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatementList().get(0);
        assertEquals(1, stmt.getTableOptions().size());
        String formatSql = SQLUtils.toSQLString(stmt);
        assertEquals("ALTER TABLE event_log\n" +
                "\tHOT_PARTITION_COUNT = 10;", formatSql);
    }

    @Test
    public void test_4() throws Exception {
        String sql = "alter table event_log hot_partition_count = 'abc';";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        try {
            SQLStatement stmt = parser.parseStatementList().get(0);
            fail();
        } catch (ParserException e) {
            //do nothing
        }
    }

    @Test
    public void test_5() throws Exception {
        String sql = "alter table event_log hot_partition_count = '10';";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        try {
            SQLStatement stmt = parser.parseStatementList().get(0);
            fail();
        } catch (ParserException e) {
            //do nothing
        }
    }

    @Test
    public void test_6() throws Exception {
        String sql = "alter table event_log storage_policy = HOT";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        try {
            SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatementList().get(0);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void test_7() throws Exception {
        String sql = "alter table event_log storage_policy = COLD";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        try {
            SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatementList().get(0);
            fail();
        } catch (Exception e) {
        }
    }
}
