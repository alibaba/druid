package com.alibaba.druid.bvt.sql.transform.datatype.oracle2pg;

import com.alibaba.druid.sql.SQLTransformUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class Oracle2PG_DataTypeTest_number_int extends TestCase {
    public void test_oracle2pg_int_1() throws Exception {
        String sql = "number(1, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("SMALLINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_2() throws Exception {
        String sql = "number(2, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("SMALLINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_3() throws Exception {
        String sql = "number(3, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("SMALLINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_4() throws Exception {
        String sql = "number(4, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("SMALLINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_5() throws Exception {
        String sql = "number(5, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("INT", pgDataType.toString());
    }

    public void test_oracle2pg_int_6() throws Exception {
        String sql = "number(6, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("INT", pgDataType.toString());
    }

    public void test_oracle2pg_int_7() throws Exception {
        String sql = "number(7, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("INT", pgDataType.toString());
    }

    public void test_oracle2pg_int_8() throws Exception {
        String sql = "number(8, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("INT", pgDataType.toString());
    }

    public void test_oracle2pg_int_9() throws Exception {
        String sql = "number(9, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_10() throws Exception {
        String sql = "number(10, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_11() throws Exception {
        String sql = "number(11, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_12() throws Exception {
        String sql = "number(12, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_13() throws Exception {
        String sql = "number(13, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_14() throws Exception {
        String sql = "number(14, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_15() throws Exception {
        String sql = "number(15, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_16() throws Exception {
        String sql = "number(16, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_17() throws Exception {
        String sql = "number(17, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_18() throws Exception {
        String sql = "number(18, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_19() throws Exception {
        String sql = "number(19, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_20() throws Exception {
        String sql = "number(20, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_int_20_x() throws Exception {
        String sql = "number(20)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("BIGINT", pgDataType.toString());
    }

    public void test_oracle2pg_star() throws Exception {
        String sql = "number(*)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("DOUBLE PRECISION", pgDataType.toString());
    }

    public void test_oracle2pg_int_21() throws Exception {
        String sql = "number(21, 0)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("DECIMAL(21)", pgDataType.toString());
    }

    public void test_oracle2pg_decimal() throws Exception {
        String sql = "decimal(19,1)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("DECIMAL(19, 1)", pgDataType.toString());
    }

    public void test_oracle2pg_dec() throws Exception {
        String sql = "dec(19,1)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("DECIMAL(19, 1)", pgDataType.toString());
    }
}
