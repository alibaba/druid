package com.alibaba.druid.bvt.sql.transform;

import com.alibaba.druid.sql.SQLTransformUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class Oracle2PG_DataTypeTest_double extends TestCase {
    public void test_oracle2pg_float() throws Exception {
        String sql = "float";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("double precision", pgDataType.toString());
    }

    public void test_oracle2pg_double() throws Exception {
        String sql = "double";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("double precision", pgDataType.toString());
    }

    public void test_oracle2pg_real() throws Exception {
        String sql = "real";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("double precision", pgDataType.toString());
    }

    public void test_oracle2pg_binary_float() throws Exception {
        String sql = "BINARY_FLOAT";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("real", pgDataType.toString());
    }

    public void test_oracle2pg_binary_double() throws Exception {
        String sql = "BINARY_DOUBLE";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("double precision", pgDataType.toString());
    }

    public void test_oracle2pg_binary_double_precision() throws Exception {
        String sql = "double precision";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("double precision", pgDataType.toString());
    }
}

