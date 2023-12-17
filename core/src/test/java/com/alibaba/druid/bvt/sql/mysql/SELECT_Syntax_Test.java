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

    public void test_16() throws Exception {
        // test some keywords that MySQL allows them to be taken as field alias.
        String[] sqls = {
                "select a.da comment from (select a ad from a ) t",
                "select orderId, orderCode from ( select  pms_order.remark comment from pms_order) t_order",
                "select ada comment from (select a comment from a ) t",
                "select comment from (select a comment from a ) t",
                "select a TRUNCATE from  t",
                "select a view from  t",
                "select a SEQUENCE, (select 1) bb,(select id from tt limit 1) view from  (select a do from b) `truncate` ",
                "select a tablespace from  t",
                "select a do from  t",
                "select a any from  t limit 5",
                "select a close from  t limit 5",
                "select a , (select b SEQUENCE from demo limit 1) from t",
                "select a enable from t",
                "select a disable from t",
                "select a cast from t",
                "select a escape from t",
                "select a minus from t",
                "select a some from t",
                "select a compute from t",
                "select a until from t",
                "select a open from t"
        };

        String[] expectedSqls = {
                "SELECT a.da AS comment\nFROM (\n\tSELECT a AS ad\n\tFROM a\n) t",
                "SELECT orderId, orderCode\nFROM (\n\tSELECT pms_order.remark AS comment\n\tFROM pms_order\n) t_order",
                "SELECT ada AS comment\nFROM (\n\tSELECT a AS comment\n\tFROM a\n) t",
                "SELECT comment\nFROM (\n\tSELECT a AS comment\n\tFROM a\n) t",
                "SELECT a AS TRUNCATE\nFROM t",
                "SELECT a AS view\nFROM t",
                "SELECT a AS SEQUENCE\n\t, (\n\t\tSELECT 1\n\t) AS bb\n\t, (\n\t\tSELECT id\n\t\tFROM tt\n\t\tLIMIT 1\n\t) AS view\nFROM (\n\tSELECT a AS do\n\tFROM b\n) `truncate`",
                "SELECT a AS tablespace\nFROM t",
                "SELECT a AS do\nFROM t",
                "SELECT a AS any\nFROM t\nLIMIT 5",
                "SELECT a AS close\nFROM t\nLIMIT 5",
                "SELECT a\n\t, (\n\t\tSELECT b AS SEQUENCE\n\t\tFROM demo\n\t\tLIMIT 1\n\t)\nFROM t",
                "SELECT a AS enable\nFROM t",
                "SELECT a AS disable\nFROM t",
                "SELECT a AS cast\nFROM t",
                "SELECT a AS escape\nFROM t",
                "SELECT a AS minus\nFROM t",
                "SELECT a AS some\nFROM t",
                "SELECT a AS compute\nFROM t",
                "SELECT a AS until\nFROM t",
                "SELECT a AS open\nFROM t"
        };
       
        for (int i = 0; i < sqls.length; i++) {
            MySqlStatementParser parser = new MySqlStatementParser(sqls[i]);
            List<SQLStatement> parseStatementList = parser.parseStatementList();
            Assert.assertEquals(1, parseStatementList.size());
            Assert.assertEquals(expectedSqls[i], parseStatementList.get(0).toString());
        }
    }

    private String output(List<SQLStatement> stmtList) {
        return SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
    }
}
