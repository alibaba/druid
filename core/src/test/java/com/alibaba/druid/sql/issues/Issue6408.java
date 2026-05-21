package com.alibaba.druid.sql.issues;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import junit.framework.TestCase;
/**
 * @auther nojavacc
 * @see <a href="https://github.com/alibaba/druid/issues/6408">...</a >
 */
public class Issue6408 extends TestCase {
    public void testSQLBuild() {

// 加法表达式 5+3
        SQLBinaryOpExpr addExpr = new SQLBinaryOpExpr();
        addExpr.setLeft(new SQLIntegerExpr(5));
        addExpr.setOperator(SQLBinaryOperator.Add);
        addExpr.setRight(new SQLIntegerExpr(3));

//除法表达式 (5 + 3) / 2
        SQLBinaryOpExpr deviceExpr = new SQLBinaryOpExpr();
        deviceExpr.setLeft(addExpr);
        deviceExpr.setOperator(SQLBinaryOperator.Divide);
        deviceExpr.setRight(new SQLIntegerExpr(2));

// 期望输出(5 + 3) / 2，实际输出为 5 + 3 / 2
        System.out.println(deviceExpr);


    }
}
