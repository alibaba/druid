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
public class SplitTest2 extends TestCase {
    public void test_0() throws Exception {
        int count = 10;
        String sql = "";

        for (int i = 0; i < count; ++i) {
            if (sql.length() != 0) {
                sql += " + ";
            }

            sql += Integer.toString(i);
        }
//        sql = "(1 + 2) + (3 + 4)";
        SQLBinaryOpExpr expr = (SQLBinaryOpExpr) new SQLExprParser(sql).expr();

        List<SQLExpr> items = SQLBinaryOpExpr.split(expr);

        System.out.println(sql);
        System.out.println(items);

        assertEquals(count, items.size());
        for (int i = 0; i < count; ++i) {
            SQLExpr item = items.get(i);
            assertEquals(Integer.toString(i ), SQLUtils.toSQLString(item));
        }
    }
}
