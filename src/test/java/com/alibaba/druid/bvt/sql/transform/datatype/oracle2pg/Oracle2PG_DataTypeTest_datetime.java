package com.alibaba.druid.bvt.sql.transform.datatype.oracle2pg;

import com.alibaba.druid.sql.SQLTransformUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class Oracle2PG_DataTypeTest_datetime extends TestCase {
    public void test_oracle2pg_timestamp() throws Exception {
        String sql = "timestamp";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("TIMESTAMP", pgDataType.toString());
    }

    public void test_oracle2pg_timestamp_arg() throws Exception {
        String sql = "timestamp(2)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("TIMESTAMP(2)", pgDataType.toString());
    }

    public void test_oracle2pg_date() throws Exception {
        String sql = "date";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("TIMESTAMP(0)", pgDataType.toString());
    }

    public void test_oracle2pg_datetime() throws Exception {
        String sql = "datetime";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("TIMESTAMP", pgDataType.toString());
    }

    public void test_oracle2pg_timestamp2() throws Exception {
        String sql = "TIMESTAMP WITH TIME ZONE";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("TIMESTAMP WITH TIME ZONE", pgDataType.toString());
    }
}
