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
package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest59_lateral extends TestCase {
    private final DbType dbType = DbType.postgresql;

    // Official doc example: LEFT JOIN LATERAL with function
    public void test_lateral_left_join_function() throws Exception {
        String sql = "SELECT m.name AS mname, pname\n" +
                "FROM manufacturers m LEFT JOIN LATERAL get_product_names(m.id) pname ON true";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("LATERAL"));
        assertTrue(result.contains("LEFT JOIN"));
        assertTrue(result.contains("get_product_names"));
    }

    // Official doc example: CROSS JOIN LATERAL with subquery
    public void test_lateral_cross_join_subquery() throws Exception {
        String sql = "SELECT * FROM t1 CROSS JOIN LATERAL (SELECT * FROM t2 WHERE t2.id = t1.id) AS sub";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("LATERAL"));
        assertTrue(result.contains("CROSS JOIN"));
    }

    // Official doc example: comma-style LATERAL subquery
    public void test_lateral_comma_subquery() throws Exception {
        String sql = "SELECT * FROM foo, LATERAL (SELECT * FROM bar WHERE bar.id = foo.bar_id) ss";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("LATERAL"));
    }

    // Official doc example: LATERAL with set-returning function (comma-style)
    public void test_lateral_comma_function() throws Exception {
        String sql = "SELECT p1.id, v1\n" +
                "FROM polygons p1, LATERAL vertices(p1.poly) v1";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("LATERAL"));
        assertTrue(result.contains("vertices"));
    }

    // INNER JOIN LATERAL
    public void test_lateral_inner_join() throws Exception {
        String sql = "SELECT * FROM orders o INNER JOIN LATERAL (SELECT * FROM order_items WHERE order_id = o.id LIMIT 3) items ON true";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("LATERAL"));
        assertTrue(result.contains("INNER JOIN"));
    }

    // LEFT JOIN LATERAL with WHERE filter (doc example pattern)
    public void test_lateral_left_join_where_null() throws Exception {
        String sql = "SELECT m.name\n" +
                "FROM manufacturers m LEFT JOIN LATERAL get_product_names(m.id) pname ON true\n" +
                "WHERE pname IS NULL";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("LATERAL"));
        assertTrue(result.contains("IS NULL"));
    }

    // LATERAL standalone at FROM level
    public void test_lateral_standalone() throws Exception {
        String sql = "SELECT * FROM LATERAL generate_series(1, 5) AS s";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("LATERAL"));
        assertTrue(result.contains("generate_series"));
    }

    // LATERAL subquery without JOIN (standalone)
    public void test_lateral_standalone_subquery() throws Exception {
        String sql = "SELECT * FROM LATERAL (SELECT 1 AS a) sub";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("LATERAL"));
    }
}
