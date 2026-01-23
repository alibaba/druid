package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class EvalSelectTest extends TestCase {
    public void test_select() throws Exception {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        List<Object> row = new ArrayList<Object>();
        row.add(1);
        rows.add(row);
        assertEquals(rows, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "SELECT 1"));
    }
}
