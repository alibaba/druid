package com.alibaba.druid.bvt.sql.oracle;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;

public class LargeOrTest extends TestCase {

    public void test_largeOr() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("SELECT 1 FROM T WHERE ID = ?");
        for (int i = 0; i < 10000; ++i) {
            buf.append(" OR ID = ?");
        }
        String sql = buf.toString();
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);
        SQLSelectQueryBlock select = (SQLSelectQueryBlock) stmt.getSelect().getQuery();
        SQLBinaryOpExpr where = (SQLBinaryOpExpr) select.getWhere();
        SQLBinaryOpExpr last = (SQLBinaryOpExpr) where.getRight();
        Assert.assertEquals(SQLBinaryOperator.Equality, last.getOperator());
    }

    public void test_largeAnd() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("SELECT 1 FROM T WHERE ID = ?");
        for (int i = 0; i < 10000; ++i) {
            buf.append(" AND ID = ?");
        }
        String sql = buf.toString();
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);
        SQLSelectQueryBlock select = (SQLSelectQueryBlock) stmt.getSelect().getQuery();
        SQLBinaryOpExpr where = (SQLBinaryOpExpr) select.getWhere();
        SQLBinaryOpExpr last = (SQLBinaryOpExpr) where.getRight();
        Assert.assertEquals(SQLBinaryOperator.Equality, last.getOperator());
    }
}
