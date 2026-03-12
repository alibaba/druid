package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import org.junit.jupiter.api.Test;

/**
 * 验证 {@link SQLAggregateExpr#toString()} 与 {@link SQLIdentifierExpr#toString()} 的兼容性
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5553">...</a>
 */
public class OracleAnalyticTest {
    @Test
    public void testToString() {
        SQLAggregateExpr sqlAggregateExpr = new SQLAggregateExpr("count");
        OracleAnalytic oracleAnalytic = new OracleAnalytic();
        SQLOrderBy sqlOrderBy = new SQLOrderBy();
        sqlOrderBy.addItem(new SQLIdentifierExpr("aaa"));
        oracleAnalytic.setOrderBy(sqlOrderBy);
        sqlAggregateExpr.setFilter(oracleAnalytic);
        System.out.println(sqlAggregateExpr.toString());
    }
}
