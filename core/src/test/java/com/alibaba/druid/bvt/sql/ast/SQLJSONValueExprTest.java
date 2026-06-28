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
package com.alibaba.druid.bvt.sql.ast;

import com.alibaba.druid.sql.ast.SQLJSONValueExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLJSONValueExprTest {
    @Test
    public void test_equals_reflexivity() {
        // Per the Java Object.equals contract: x.equals(x) must be true (reflexivity)
        SQLJSONValueExpr expr = new SQLJSONValueExpr();
        assertTrue(expr.equals(expr), "An object must be equal to itself (reflexivity)");
    }

    @Test
    public void test_equals_null() {
        // Per the Java Object.equals contract: x.equals(null) must be false
        SQLJSONValueExpr expr = new SQLJSONValueExpr();
        assertFalse(expr.equals(null), "x.equals(null) must be false");
    }

    @Test
    public void test_hashCode_consistency() {
        // Per the Java Object contract: multiple calls to hashCode must return the same value
        SQLJSONValueExpr expr = new SQLJSONValueExpr();
        int h1 = expr.hashCode();
        int h2 = expr.hashCode();
        assertEquals(h1, h2, "hashCode must be consistent across calls");
    }

    @Test
    public void test_equals_with_populated_fields() {
        SQLJSONValueExpr a = new SQLJSONValueExpr();
        a.setJson(new SQLIdentifierExpr("col1"));
        a.setPath(new SQLCharExpr("$.key"));

        SQLJSONValueExpr b = new SQLJSONValueExpr();
        b.setJson(new SQLIdentifierExpr("col1"));
        b.setPath(new SQLCharExpr("$.key"));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void test_not_equals_different_path() {
        // Same json, different path: must not be equal (guards against equals ignoring path)
        SQLJSONValueExpr a = new SQLJSONValueExpr();
        a.setJson(new SQLIdentifierExpr("col1"));
        a.setPath(new SQLCharExpr("$.key1"));

        SQLJSONValueExpr b = new SQLJSONValueExpr();
        b.setJson(new SQLIdentifierExpr("col1"));
        b.setPath(new SQLCharExpr("$.key2"));

        assertNotEquals(a, b);
    }

    @Test
    public void test_not_equals_different_json() {
        // Same path, different json: must not be equal (guards against equals ignoring json)
        SQLJSONValueExpr a = new SQLJSONValueExpr();
        a.setJson(new SQLIdentifierExpr("col1"));
        a.setPath(new SQLCharExpr("$.key"));

        SQLJSONValueExpr b = new SQLJSONValueExpr();
        b.setJson(new SQLIdentifierExpr("col2"));
        b.setPath(new SQLCharExpr("$.key"));

        assertNotEquals(a, b);
    }
}
