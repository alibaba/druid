package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalConcatTest extends TestCase {
    public void test_concat() throws Exception {
        assertEquals("abcd", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "concat(?, ?)", "ab", "cd"));
        assertEquals("abcdef", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "concat(?, ?, ?)", "ab", "cd", "ef"));
    }
}
