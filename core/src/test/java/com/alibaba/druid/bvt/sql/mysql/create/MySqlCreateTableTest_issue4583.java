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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MySqlCreateTableTest_issue4583 {
    @Test
    public void test_single_string_default() {
        String sql = "CREATE TABLE t (s_id varchar(64) NOT NULL DEFAULT '0' COMMENT 'note')";
        assertDefaultAndComment(sql);
    }

    @Test
    public void test_adjacent_string_default() {
        String sql = "CREATE TABLE t (s_id varchar(64) NOT NULL DEFAULT '' '0' COMMENT 'note')";
        assertDefaultAndComment(sql);
    }

    @Test
    public void test_empty_string_default() {
        assertStringDefault("CREATE TABLE t (c varchar(64) DEFAULT '')", "");
    }

    @Test
    public void test_single_string_default_before_right_paren() {
        assertStringDefault("CREATE TABLE t (c varchar(64) DEFAULT 'abc')", "abc");
    }

    @Test
    public void test_adjacent_string_default_before_right_paren() {
        assertStringDefault("CREATE TABLE t (c varchar(64) DEFAULT 'a' 'b')", "ab");
    }

    @Test
    public void test_multiple_string_default_before_next_column() {
        SQLCreateTableStatement stmt = assertStringDefault(
                "CREATE TABLE t (c varchar(64) DEFAULT 'a' 'b' 'c', id int)", "abc");
        assertEquals(2, stmt.getTableElementList().size());
        SQLColumnDefinition id = stmt.findColumn("id");
        assertNotNull(id);
        assertEquals("int", id.getDataType().getName());
        assertNull(id.getDefaultExpr());
    }

    @Test
    public void test_adjacent_empty_string_default() {
        assertStringDefault("CREATE TABLE t (c varchar(64) DEFAULT '' '')", "");
    }

    @Test
    public void test_multiple_string_default_with_empty_segments() {
        assertStringDefault("CREATE TABLE t (c varchar(64) DEFAULT '' 'a' '' 'b' '')", "ab");
    }

    @Test
    public void test_adjacent_string_select() {
        String sql = "SELECT '' '0'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals(1, stmt.getSelect().getQueryBlock().getSelectList().size());
        SQLSelectItem item = stmt.getSelect().getQueryBlock().getSelectItem(0);
        assertEquals("0", ((SQLCharExpr) item.getExpr()).getText());
        assertNull(item.getAlias());
    }

    private SQLCreateTableStatement assertStringDefault(String sql, String expected) {
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);
        SQLColumnDefinition column = stmt.findColumn("c");
        assertNotNull(column);
        assertEquals(expected, ((SQLCharExpr) column.getDefaultExpr()).getText());
        assertNull(column.getComment());
        return stmt;
    }

    private void assertDefaultAndComment(String sql) {
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);
        SQLColumnDefinition column = stmt.findColumn("s_id");
        assertNotNull(column);
        assertEquals("0", ((SQLCharExpr) column.getDefaultExpr()).getText());
        assertEquals("note", ((SQLCharExpr) column.getComment()).getText());
    }
}
