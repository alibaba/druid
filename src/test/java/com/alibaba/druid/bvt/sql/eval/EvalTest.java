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
package com.alibaba.druid.bvt.sql.eval;

import java.math.BigDecimal;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalTest extends TestCase {

    public void testEval() throws Exception {
        Assert.assertEquals("A", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?", "A"));
        Assert.assertEquals(123, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?", 123));
    }

    public void testEval_1() throws Exception {
        Assert.assertEquals("AB", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", "A", "B"));
        Assert.assertEquals(234, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", 123, 111));
    }

    public void testEval_2() throws Exception {
        Assert.assertEquals(110, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? * ?", 10, 11));
    }

    public void testEval_3() throws Exception {
        Assert.assertEquals(new BigDecimal("110"), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? * ?",
                                                                                new BigDecimal("10"),
                                                                                new BigDecimal("11")));
    }

    public void testEval_4() throws Exception {
        Assert.assertEquals(new BigDecimal("110"),
                            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? * 11", new BigDecimal("10")));
    }

    public void testEval_5() throws Exception {
        Assert.assertEquals(new BigDecimal("110.0"),
                            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? * 11.0", new BigDecimal("10")));
    }

    public void testEval_6() throws Exception {
        Assert.assertEquals(new BigDecimal("110.0"),
                            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? * 11", new BigDecimal("10.0")));
    }

    public void testEval_7() throws Exception {
        Assert.assertEquals(new BigDecimal("110.0"),
                            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? * 11.0", "10"));
    }
}
