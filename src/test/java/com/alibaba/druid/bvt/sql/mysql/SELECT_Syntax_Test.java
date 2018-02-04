/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import junit.framework.TestCase;

public class SELECT_Syntax_Test extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT year, SUM(profit) FROM sales GROUP BY year WITH ROLLUP;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLStatement stmt = stmtList.get(0);

        Assert.assertEquals("SELECT year, SUM(profit)\nFROM sales\nGROUP BY year WITH ROLLUP;", SQLUtils.toMySqlString(stmt));
        Assert.assertEquals("select year, sum(profit)\nfrom sales\ngroup by year with rollup;", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

    public void test_1() throws Exception {
        String sql = "SELECT * FROM T FOR UPDATE;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT *\nFROM T\nFOR UPDATE;", text);
    }
    
    public void test_with_cube() throws Exception {
        String sql = "SELECT year, SUM(profit) FROM sales GROUP BY year WITH CUBE;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLStatement stmt = stmtList.get(0);

        Assert.assertEquals("SELECT year, SUM(profit)\nFROM sales\nGROUP BY year WITH CUBE;", SQLUtils.toMySqlString(stmt));
        Assert.assertEquals("select year, sum(profit)\nfrom sales\ngroup by year with cube;", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

    public void test_2() throws Exception {
        String sql = "SELECT * FROM T LOCK IN SHARE MODE;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT *\nFROM T\nLOCK IN SHARE MODE;", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT a,b,a+b INTO OUTFILE '/tmp/result.txt' FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\\n' FROM test_table;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT a, b, a + b\nINTO OUTFILE '/tmp/result.txt' COLUMNS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\n'\nFROM test_table;",
                            text);
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

        assertEquals("SELECT *\n" +
                "FROM t1\n" +
                "WHERE column1 = (\n" +
                "\tSELECT column1\n" +
                "\tFROM t2\n" +
                ");", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT column1 FROM t1 WHERE EXISTS (SELECT * FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT column1" //
                + "\nFROM t1" //
                + "\nWHERE EXISTS (" //
                + "\n\tSELECT *" //
                + "\n\tFROM t2"
                + "\n);", text);
    }

    public void test_8() throws Exception {
        String sql = "SELECT DISTINCT store_type FROM stores WHERE NOT EXISTS (SELECT * FROM cities_stores WHERE cities_stores.store_type = stores.store_type);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DISTINCT store_type" //
                + "\nFROM stores" //
                + "\nWHERE NOT EXISTS (" //
                + "\n\tSELECT *"
                + "\n\tFROM cities_stores"
                + "\n\tWHERE cities_stores.store_type = stores.store_type"
                + "\n);",
                            text);
    }

    public void test_9() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 = SOME (SELECT s1 FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        assertEquals("SELECT s1\n" +
                "FROM t1\n" +
                "WHERE s1 = SOME (\n" +
                "\tSELECT s1\n" +
                "\tFROM t2\n" +
                ");", text);
    }

    public void test_10() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 = ANY (SELECT s1 FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT s1\n" +
                "FROM t1\n" +
                "WHERE s1 = ANY (\n" +
                "\tSELECT s1\n" +
                "\tFROM t2\n" +
                ");", text);
    }

    public void test_11() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 > ALL (SELECT s1 FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        assertEquals("SELECT s1\n" +
                "FROM t1\n" +
                "WHERE s1 > ALL (\n" +
                "\tSELECT s1\n" +
                "\tFROM t2\n" +
                ");", text);
    }

    public void test_12() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 NOT IN (SELECT s1 FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT s1\n" +
                "FROM t1\n" +
                "WHERE s1 NOT IN (\n" +
                "\tSELECT s1\n" +
                "\tFROM t2\n" +
                ");", SQLUtils.toMySqlString(stmt));
        assertEquals("select s1\n" +
                "from t1\n" +
                "where s1 not in (\n" +
                "\tselect s1\n" +
                "\tfrom t2\n" +
                ");", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
    
    public void test_13() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 IN (SELECT s1 FROM t2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT s1\n" +
                "FROM t1\n" +
                "WHERE s1 IN (\n" +
                "\tSELECT s1\n" +
                "\tFROM t2\n" +
                ");", SQLUtils.toMySqlString(stmt));
        assertEquals("select s1\n" +
                "from t1\n" +
                "where s1 in (\n" +
                "\tselect s1\n" +
                "\tfrom t2\n" +
                ");", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
    
    public void test_14() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 IN (?, ?, ?);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);

        Assert.assertEquals("SELECT s1\nFROM t1\nWHERE s1 IN (?, ?, ?);", SQLUtils.toMySqlString(stmt));
        Assert.assertEquals("select s1\nfrom t1\nwhere s1 in (?, ?, ?);", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
    
    public void test_15() throws Exception {
        String sql = "SELECT s1 FROM t1 WHERE s1 NOT IN (?, ?, ?);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);

        Assert.assertEquals("SELECT s1\nFROM t1\nWHERE s1 NOT IN (?, ?, ?);", SQLUtils.toMySqlString(stmt));
        Assert.assertEquals("select s1\nfrom t1\nwhere s1 not in (?, ?, ?);", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

    private String output(List<SQLStatement> stmtList) {
        return SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
    }
}
