package com.alibaba.druid.bvt.sql.eval;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalSelectTest extends TestCase {
    public void test_select() throws Exception {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        List<Object> row = new ArrayList<Object>();
        row.add(1);
        rows.add(row);
        Assert.assertEquals(rows, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "SELECT 1"));
    }
}
