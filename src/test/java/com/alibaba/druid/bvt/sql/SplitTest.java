package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.parser.SQLExprParser;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 04/06/2017.
 */
public class SplitTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "((1 + 2) + (3 + 4) + 5) + ((6 + 7) + (8 + 9) + 10)";
//        sql = "(1 + 2) + (3 + 4)";
        SQLBinaryOpExpr expr = (SQLBinaryOpExpr) new SQLExprParser(sql).expr();

        List<SQLExpr> items = SQLBinaryOpExpr.split(expr);

        System.out.println(sql);
        System.out.println(items);

        assertEquals(10, items.size());
        for (int i = 0; i < 10; ++i) {
            SQLExpr item = items.get(i);
            assertEquals(Integer.toString(i + 1), SQLUtils.toSQLString(item));
        }
    }
}
