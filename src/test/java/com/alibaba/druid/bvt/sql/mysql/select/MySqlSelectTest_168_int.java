package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_168_int extends MysqlTest {

    public void test_1() throws Exception {
        String sql = "/*+engine=MPP*/ SELECT  ceil(SMALLINT'123')";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" + "SELECT ceil(SMALLINT '123')", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "/*+engine=MPP*/ SELECT floor(SMALLINT'123')";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.PipesAsConcat);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" + "SELECT floor(SMALLINT '123')", stmt.toString());
    }

    public void test_tiny_1() throws Exception {
        String sql = "/*+engine=MPP*/ SELECT  ceil(TINYINT'123')";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" + "SELECT ceil(TINYINT '123')", stmt.toString());
    }

    public void test_tiny_2() throws Exception {
        String sql = "/*+engine=MPP*/ SELECT floor(TINYINT'123')";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.PipesAsConcat);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" + "SELECT floor(TINYINT '123')", stmt.toString());
    }

    public void test_big_1() throws Exception {
        String sql = "/*+engine=MPP*/ SELECT  ceil(BIGINT'123')";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" + "SELECT ceil(BIGINT '123')", stmt.toString());
    }

    public void test_big_2() throws Exception {
        String sql = "/*+engine=MPP*/ SELECT floor(BIGINT'123')";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.PipesAsConcat);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" + "SELECT floor(BIGINT '123')", stmt.toString());
    }
    public void test_real_1() throws Exception {
        String sql = "/*+engine=MPP*/ SELECT floor(REAL '-123.0')";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.PipesAsConcat);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" + "SELECT floor(REAL '-123.0')", stmt.toString());
    }
    public void test_real_2() throws Exception {
        String sql = "/*+engine=MPP*/ SELECT ceil(REAL '-123.0')";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.PipesAsConcat);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" + "SELECT ceil(REAL '-123.0')", stmt.toString());
    }
    public void test_double_3() throws Exception {
        String sql = "/*+engine=MPP*/ SELECT floor(CAST(NULL as DOUBLE))";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.PipesAsConcat);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" + "SELECT floor(CAST(NULL AS DOUBLE))", stmt.toString());
    }
    public void test_double_4() throws Exception {
        String sql = "/*+engine=MPP*/ SELECT floor(CAST(NULL as DECIMAL(25,5)))";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.PipesAsConcat);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" + "SELECT floor(CAST(NULL AS DECIMAL(25, 5)))", stmt.toString());
    }


}