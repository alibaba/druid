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
package com.alibaba.druid.sql.ast;

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
}
