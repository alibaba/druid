package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnnestTableSource;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UnnestTest {
    @Test
    public void test_0() throws Exception {
        String sql = "SELECT 1\n" +
                "FROM t1 pt\n" +
                "LEFT OUTER JOIN t2 cl\n" +
                "ON pt.driver_id = cl.driver_id\n" +
                "    AND pt.order_id = cl.order_no\n" +
                "LEFT OUTER JOIN UNNEST(v_list) v_type\n" +
                "LEFT OUTER JOIN UNNEST(r_list) d_id";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);
        SQLTableSource from = stmt.getSelect().getQueryBlock().getFrom();
        assertTrue(from instanceof SQLJoinTableSource);
        SQLJoinTableSource join = (SQLJoinTableSource) from;
        assertTrue(join.getRight() instanceof SQLUnnestTableSource);

        SQLJoinTableSource left = (SQLJoinTableSource) join.getLeft();
        assertTrue(left.getRight() instanceof SQLUnnestTableSource);
    }
}
