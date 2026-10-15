package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvalSelectTest {
    @Test
    public void test_select() throws Exception {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        List<Object> row = new ArrayList<Object>();
        row.add(1);
        rows.add(row);
        assertEquals(rows, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "SELECT 1"));
    }
}
