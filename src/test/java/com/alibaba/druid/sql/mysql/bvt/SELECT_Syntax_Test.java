package com.alibaba.druid.sql.mysql.bvt;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SELECT_Syntax_Test extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT year, SUM(profit) FROM sales GROUP BY year WITH ROLLUP;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT year, SUM(profit)\nFROM sales\nGROUP BY year WITH ROLLUP;", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT * FROM T FOR UPDATE;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT *\nFROM T\nFOR UPDATE;", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT * FROM T LOCK IN SHARE MODE;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT *\nFROM T\nLOCK IN SHARE MODE;", text);
    }

    public void test_3() throws Exception {
        String sql =
                "SELECT a,b,a+b INTO OUTFILE '/tmp/result.txt' FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\\n' FROM test_table;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT a, b, a + b\nINTO OUTFILE '/tmp/result.txt' COLUMNS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\n'\nFROM test_table;", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT 1 + 1 FROM DUAL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 + 1\nFROM DUAL;", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT 1 + 1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 + 1;", text);
    }

    public void test_6() throws Exception {
        String sql = "SELECT * FROM t1 WHERE column1 = (SELECT column1 FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT *\nFROM t1\nWHERE column1 = (SELECT column1\n\tFROM t2);", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT column1 FROM t1 WHERE EXISTS (SELECT * FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT column1\nFROM t1\nWHERE EXISTS (SELECT *\n\tFROM t2);", text);
    }

    public void test_8() throws Exception {
        String sql =
                "SELECT DISTINCT store_type FROM stores WHERE NOT EXISTS (SELECT * FROM cities_stores WHERE cities_stores.store_type = stores.store_type);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DISTINCT store_type\nFROM stores\nWHERE NOT EXISTS (SELECT *\n\tFROM cities_stores\n\tWHERE cities_stores.store_type = stores.store_type);", text);
    }

    public void test_9() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 = SOME (SELECT s1 FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT s1\nFROM t1\nWHERE s1 = SOME (SELECT s1\n\tFROM t2);", text);
    }

    public void test_10() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 = ANY (SELECT s1 FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT s1\nFROM t1\nWHERE s1 = ANY (SELECT s1\n\tFROM t2);", text);
    }

    public void test_11() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 > ALL (SELECT s1 FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT s1\nFROM t1\nWHERE s1 > ALL (SELECT s1\n\tFROM t2);", text);
    }

    public void test_12() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 NOT IN (SELECT s1 FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT s1\nFROM t1\nWHERE s1 NOT IN (SELECT s1\n\tFROM t2);", text);
    }

    private String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();

        for (SQLStatement stmt : stmtList) {
            stmt.accept(new MySqlOutputVisitor(out));
            out.append(";");
        }

        return out.toString();
    }
}
