package com.alibaba.druid.bvt.sql.transform.datatype.oracle2pg;

import com.alibaba.druid.sql.SQLTransformUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class Oracle2MySql_method_sys_guid extends TestCase {
    public void test_oracle2pg_int_1() throws Exception {
        String sql = "sys_guid()";
        SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).expr();
        SQLExpr targetExpr = SQLTransformUtils.transformOracleToPostgresql(expr);
        assertEquals("uuid_generate_v4()", targetExpr.toString());
    }
}
