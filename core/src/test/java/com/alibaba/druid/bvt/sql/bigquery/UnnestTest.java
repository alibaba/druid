package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnnestTableSource;
import org.junit.Test;

import static org.junit.Assert.*;

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

    // https://github.com/alibaba/druid/issues/6547
    @Test
    public void test_unnest_with_offset_no_alias() throws Exception {
        String sql = "SELECT * FROM UNNEST ([10,20,30]) as numbers WITH OFFSET";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);
        SQLTableSource from = stmt.getSelect().getQueryBlock().getFrom();
        assertTrue(from instanceof SQLUnnestTableSource);
        SQLUnnestTableSource unnest = (SQLUnnestTableSource) from;
        assertEquals("numbers", unnest.getAlias());
        assertTrue(unnest.isWithOffset());
        assertNull(unnest.getOffset());

        String output = SQLUtils.toSQLString(stmt, DbType.bigquery);
        assertTrue(output.toUpperCase().contains("WITH OFFSET"));
        assertFalse(output.toUpperCase().contains("WITH OFFSET AS"));
    }

    // https://github.com/alibaba/druid/issues/6547
    @Test
    public void test_unnest_with_offset_as_alias() throws Exception {
        String sql = "SELECT * FROM UNNEST ([10,20,30]) as numbers WITH OFFSET AS off";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);
        SQLTableSource from = stmt.getSelect().getQueryBlock().getFrom();
        assertTrue(from instanceof SQLUnnestTableSource);
        SQLUnnestTableSource unnest = (SQLUnnestTableSource) from;
        assertEquals("numbers", unnest.getAlias());
        assertTrue(unnest.isWithOffset());
        assertNotNull(unnest.getOffset());

        String output = SQLUtils.toSQLString(stmt, DbType.bigquery);
        assertTrue(output.toUpperCase().contains("WITH OFFSET AS"));
    }

    // https://github.com/alibaba/druid/issues/6547
    @Test
    public void test_unnest_with_offset_implicit_alias() throws Exception {
        // BigQuery allows: WITH OFFSET offset_alias (without AS keyword)
        String sql = "SELECT * FROM UNNEST ([10,20,30]) as numbers WITH OFFSET off";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);
        SQLTableSource from = stmt.getSelect().getQueryBlock().getFrom();
        assertTrue(from instanceof SQLUnnestTableSource);
        SQLUnnestTableSource unnest = (SQLUnnestTableSource) from;
        assertEquals("numbers", unnest.getAlias());
        assertTrue(unnest.isWithOffset());
        assertNotNull(unnest.getOffset());
    }
}
