package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class MySQLSelectModifierTest extends TestCase {

    public void testTwoSelectModifiers() {
        String targetSql = "select distinct SQL_CALC_FOUND_ROWS id from goods";

        String resultSql = "SELECT DISTINCT SQL_CALC_FOUND_ROWS id\nFROM goods";

        equal(targetSql, resultSql);
    }

    public void testMultiSelectModifiers() {
        String targetSql = "select STRAIGHT_JOIN  distinctrow SQL_CALC_FOUND_ROWS SQL_BUFFER_RESULT distinct  HIGH_PRIORITY SQL_BIG_RESULT SQL_SMALL_RESULT id from goods where id=2";
        String resultSql = "SELECT DISTINCT HIGH_PRIORITY STRAIGHT_JOIN SQL_SMALL_RESULT SQL_BIG_RESULT SQL_BUFFER_RESULT SQL_CALC_FOUND_ROWS id\n" +
                "FROM goods\n" +
                "WHERE id = 2";

        equal(targetSql, resultSql);
    }

    public void testDistinctAndAllExclusion() {
        String allDistinctSql = "select ALL DISTINCT id from goods";
        String allDistinctRowSql = "select ALL  DISTINCTROW id from goods";

        try {
            MySqlStatementParser parser = new MySqlStatementParser(allDistinctSql);
            parser.parseStatementList();

            throw new RuntimeException("can't check the ALL and DISTINCT/DISTINCTROW exclusive!");
        } catch (Exception e) {
            if (!(e instanceof ParserException)) {
                throw e;
            }
        }

        try {
            MySqlStatementParser parser = new MySqlStatementParser(allDistinctRowSql);
            parser.parseStatementList();

            throw new RuntimeException("can't check the ALL and DISTINCT/DISTINCTROW exclusive!");
        } catch (Exception e) {
            if (!(e instanceof ParserException)) {
                throw e;
            }
        }
    }

    public void testSqlCacheOrNoCache(){
        String allDistinctSql = "select  SQL_NO_CACHE SQL_CACHE id from goods";

        try {
            MySqlStatementParser parser = new MySqlStatementParser(allDistinctSql);
            parser.parseStatementList();

            throw new RuntimeException("can't check the ALL and DISTINCT/DISTINCTROW exclusive!");
        } catch (Exception e) {
            if (!(e instanceof ParserException)) {
                throw e;
            }
        }

    }

    private void equal(String targetSql, String resultSql) {
        MySqlStatementParser parser = new MySqlStatementParser(targetSql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        System.out.println(sqlStatements.get(0).toString());
        Assert.assertTrue(sqlStatements.get(0).toString().equals(resultSql));

    }
}
