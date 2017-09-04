package com.alibaba.druid.bvt.sql.mysql.transform;

import com.alibaba.druid.sql.SQLTransformUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class DecodeTest_0 extends TestCase {
    public void test_transform() throws Exception {
        SQLMethodInvokeExpr decodeExpr = (SQLMethodInvokeExpr) SQLUtils.toSQLExpr("decode(a, b, c, d)", JdbcConstants.ORACLE);
        SQLExpr expr = SQLTransformUtils.transformDecode(decodeExpr);
        String targetSql = SQLUtils.toSQLString(expr, JdbcConstants.MYSQL);
        assertEquals("if(a = b, c, d)", targetSql);
    }

    public void test_transform_null() throws Exception {
        SQLMethodInvokeExpr decodeExpr = (SQLMethodInvokeExpr) SQLUtils.toSQLExpr("decode(a, null, c, d)", JdbcConstants.ORACLE);
        SQLExpr expr = SQLTransformUtils.transformDecode(decodeExpr);
        String targetSql = SQLUtils.toSQLString(expr, JdbcConstants.MYSQL);
        assertEquals("if(a IS NULL, c, d)", targetSql);
    }
}
