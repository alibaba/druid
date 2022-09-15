package com.alibaba.druid.bvt.sql.transform.datatype.oracle2pg;

import com.alibaba.druid.sql.SQLTransformUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class Oracle2PG_DataTypeTest_chars extends TestCase {
    public void test_oracle2pg_char() throws Exception {
        String sql = "CHAR(10)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("CHAR(10)", pgDataType.toString());
    }

    public void test_oracle2pg_char_2000() throws Exception {
        String sql = "char(2000)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("CHAR(2000)", pgDataType.toString());
    }

    public void test_oracle2pg_char_4000() throws Exception {
        String sql = "char(4000)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("TEXT", pgDataType.toString());
    }

    public void test_oracle2pg_nchar() throws Exception {
        String sql = "nchar(10)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("NCHAR(10)", pgDataType.toString());
    }

    public void test_oracle2pg_varchar() throws Exception {
        String sql = "varchar(10)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("VARCHAR(10)", pgDataType.toString());
    }

    public void test_oracle2pg_varchar_2000() throws Exception {
        String sql = "varchar(2000)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("VARCHAR(2000)", pgDataType.toString());
    }

    public void test_oracle2pg_varchar_4000() throws Exception {
        String sql = "varchar(4000)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("VARCHAR(4000)", pgDataType.toString());
    }

    public void test_oracle2pg_varchar_8000() throws Exception {
        String sql = "varchar(8000)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("TEXT", pgDataType.toString());
    }

    public void test_oracle2pg_nvarchar() throws Exception {
        String sql = "nvarchar(10)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("VARCHAR(10)", pgDataType.toString());
    }

    public void test_urowid() throws Exception {
        String sql = "urowid(100)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("VARCHAR(100)", pgDataType.toString());
    }

    public void test_bfile() throws Exception {
        String sql = "bfile";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("VARCHAR(255)", pgDataType.toString());
    }

    public void test_oracle2pg_varchar2_8000() throws Exception {
        String sql = "varchar2(8000)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("TEXT", pgDataType.toString());
    }


    public void test_oracle2pg_varchar2_10000() throws Exception {
        String sql = "varchar2(10000)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("TEXT", pgDataType.toString());
    }


    public void test_oracle2pg_bfile() throws Exception {
        String sql = "bfile";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        assertEquals("VARCHAR(255)", pgDataType.toString());
    }
}
