package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class AggregateTest {
    @Test
    public void test_agg() {
        String sql = "SELECT lag(date(d,'Asia/Jakarta')) over(partition by id order by n) FROM t1";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        SQLAggregateExpr expr = (SQLAggregateExpr) queryBlock.getSelectList().get(0).getExpr();
        assertSame(expr, expr.getArgument(0).getParent());
    }

    @Test
    public void test_agg_1() {
        String sql = "SELECT xx(date(d,'Asia/Jakarta')) over(partition by id order by n) FROM t1";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        SQLAggregateExpr expr = (SQLAggregateExpr) queryBlock.getSelectList().get(0).getExpr();
        assertSame(expr, expr.getArgument(0).getParent());
    }
}
