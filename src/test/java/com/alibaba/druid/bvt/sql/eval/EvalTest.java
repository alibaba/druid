/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.eval;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcUtils;

public class EvalTest extends TestCase {

    public void testEval() throws Exception {
        Assert.assertEquals("A", SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "?", "A"));
        Assert.assertEquals(123, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "?", 123));
    }

    public void testEval_1() throws Exception {
        Assert.assertEquals("AB", SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", "A", "B"));
        Assert.assertEquals(234, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", 123, 111));
    }
}
