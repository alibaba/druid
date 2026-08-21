/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.create_function;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MySqlCreateFunctionMultiStatementTest {
    @Test
    public void testSimpleFormFunctionThenSelect() {
        String sql = "CREATE FUNCTION f() RETURNS INT RETURN 1;\nSELECT 1;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(2, stmts.size());
        assertTrue(stmts.get(0) instanceof SQLCreateFunctionStatement);
        assertTrue(stmts.get(1) instanceof SQLSelectStatement);
        String functionSql = SQLUtils.toMySqlString(stmts.get(0));
        assertTrue(functionSql.contains("CREATE FUNCTION"));
        assertTrue(functionSql.contains("RETURN 1"));
        assertFalse(functionSql.contains(";;"));
    }

    @Test
    public void testSimpleFormFunctionThenFunction() {
        String sql = "CREATE FUNCTION f() RETURNS INT RETURN 1;\nCREATE FUNCTION g() RETURNS INT RETURN 2;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(2, stmts.size());
        assertTrue(stmts.get(0) instanceof SQLCreateFunctionStatement);
        assertTrue(stmts.get(1) instanceof SQLCreateFunctionStatement);
    }

    @Test
    public void testBeginEndFunctionThenSelect() {
        String sql = "CREATE FUNCTION f() RETURNS INT BEGIN RETURN 1; END;\nSELECT 1;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(2, stmts.size());
        assertTrue(stmts.get(0) instanceof SQLCreateFunctionStatement);
        assertTrue(stmts.get(1) instanceof SQLSelectStatement);
    }
}
