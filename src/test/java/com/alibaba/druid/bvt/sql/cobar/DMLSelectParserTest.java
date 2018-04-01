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
package com.alibaba.druid.bvt.sql.cobar;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class DMLSelectParserTest extends TestCase {

    public void test_union_0() throws Exception {
        String sql = "(select id from t1) union all (select id from t2) union all (select id from t3) ordeR By d desC limit 1 offset ?";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("(SELECT id\n" +
                "FROM t1)\n" +
                "UNION ALL\n" +
                "(SELECT id\n" +
                "FROM t2)\n" +
                "UNION ALL\n" +
                "(SELECT id\n" +
                "FROM t3)\n" +
                "ORDER BY d DESC\n" +
                "LIMIT ?, 1", output);
    }

    public void test_union_1() throws Exception {
        String sql = "(select id from t1) union  select id from t2 order by id union aLl (select id from t3) ordeR By d asC";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("(SELECT id\n" +
                "FROM t1)\n" +
                "UNION\n" +
                "SELECT id\n" +
                "FROM t2\n" +
                "ORDER BY id\n" +
                "UNION ALL\n" +
                "(SELECT id\n" +
                "FROM t3)\n" +
                "ORDER BY d ASC", output);
    }

    public void test_union_2() throws Exception {
        String sql = "(select id from t1) union distInct (select id from t2) union  select id from t3";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("(SELECT id\n" +
                "FROM t1)\n" +
                "UNION DISTINCT\n" +
                "(SELECT id\n" +
                "FROM t2)\n" +
                "UNION\n" +
                "SELECT id\n" +
                "FROM t3", output);
    }

    public void test_select_0() throws Exception {
        String sql = "SELect t1.id , t2.* from t1, test.t2 where test.t1.id=1 and t1.id=test.t2.id";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT t1.id, t2.*\n" + //
                            "FROM t1, test.t2\n" + //
                            "WHERE test.t1.id = 1\n" + //
                            "\tAND t1.id = test.t2.id", output);
    }

    public void test_select_1() throws Exception {
        String sql = "select * from  offer  a  straight_join wp_image b use key for join(t1,t2) on a.member_id=b.member_id inner join product_visit c where a.member_id=c.member_id and c.member_id='abc' ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT *" + //
                            "\nFROM offer a" + //
                            "\n\tSTRAIGHT_JOIN wp_image b USE INDEX FOR JOIN (t1, t2) ON a.member_id = b.member_id" + //
                            "\n\tINNER JOIN product_visit c" + //
                            "\nWHERE a.member_id = c.member_id\n" + //
                            "\tAND c.member_id = 'abc'", output);
    }

    public void test_select_2() throws Exception {
        String sql = "SELect all tb1.id,tb2.id from tb1,tb2 where tb1.id2=tb2.id2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT ALL tb1.id, tb2.id\n" + //
                            "FROM tb1, tb2\n" + //
                            "WHERE tb1.id2 = tb2.id2", output);
    }

    public void test_select_3() throws Exception {
        String sql = "SELect distinct high_priority tb1.id,tb2.id from tb1,tb2 where tb1.id2=tb2.id2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT DISTINCT HIGH_PRIORITY tb1.id, tb2.id\nFROM tb1, tb2\nWHERE tb1.id2 = tb2.id2",
                            output);
    }

    public void test_select_4() throws Exception {
        String sql = "SELect distinctrow high_priority sql_small_result tb1.id,tb2.id from tb1,tb2 where tb1.id2=tb2.id2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT DISTINCTROW HIGH_PRIORITY SQL_SMALL_RESULT tb1.id, tb2.id\nFROM tb1, tb2\nWHERE tb1.id2 = tb2.id2",
                            output);
    }

    public void test_select_5() throws Exception {
        String sql = "SELect  sql_cache id1,id2 from tb1,tb2 where tb1.id1=tb2.id1 ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT SQL_CACHE id1, id2\nFROM tb1, tb2\nWHERE tb1.id1 = tb2.id1", output);
    }

    public void test_select_6() throws Exception {
        String sql = "SELect distinct high_priority tb1.id,tb2.id from tb1,tb2 where tb1.id2=tb2.id2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT DISTINCT HIGH_PRIORITY tb1.id, tb2.id\nFROM tb1, tb2\nWHERE tb1.id2 = tb2.id2",
                            output);
    }

    public void test_select_7() throws Exception {
        String sql = "SELect distinctrow high_priority sql_small_result tb1.id,tb2.id "
                     + "from tb1,tb2 where tb1.id2=tb2.id2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT DISTINCTROW HIGH_PRIORITY SQL_SMALL_RESULT tb1.id, tb2.id\nFROM tb1, tb2\nWHERE tb1.id2 = tb2.id2",
                            output);
    }

    public void test_select_8() throws Exception {
        String sql = "SELect  sql_cache id1,id2 from tb1,tb2 where tb1.id1=tb2.id1 ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT SQL_CACHE id1, id2\nFROM tb1, tb2\nWHERE tb1.id1 = tb2.id1", output);
    }

    public void test_select_9() throws Exception {
        String sql = "SELect  sql_cache id1,max(id2) from tb1 group by id1 having id1>10 order by id3 desc";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT SQL_CACHE id1, MAX(id2)\nFROM tb1\nGROUP BY id1\nHAVING id1 > 10\nORDER BY id3 DESC",
                            output);
    }

    public void test_select_10() throws Exception {
        String sql = "SELect  SQL_BUFFER_RESULT tb1.id1,id2 from tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT SQL_BUFFER_RESULT tb1.id1, id2\nFROM tb1", output);
    }

    public void test_select_11() throws Exception {
        String sql = "SELect  SQL_no_cache tb1.id1,id2 from tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT SQL_NO_CACHE tb1.id1, id2\nFROM tb1", output);
    }

    public void test_select_12() throws Exception {
        String sql = "SELect  SQL_CALC_FOUND_ROWS tb1.id1,id2 from tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT SQL_CALC_FOUND_ROWS tb1.id1, id2\nFROM tb1", output);
    }

    public void test_select_13() throws Exception {
        String sql = "SELect 1+1 ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT 1 + 1", output);
    }

    public void test_select_14() throws Exception {
        String sql = "SELect t1.* from tb ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT t1.*\nFROM tb", output);
    }

    public void test_select_15() throws Exception {
        String sql = "SELect distinct high_priority straight_join sql_big_result sql_cache tb1.id,tb2.id "
                     + "from tb1,tb2 where tb1.id2=tb2.id2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT DISTINCT HIGH_PRIORITY STRAIGHT_JOIN SQL_BIG_RESULT"
                            + " SQL_CACHE tb1.id, tb2.id\nFROM tb1, tb2\nWHERE tb1.id2 = tb2.id2", output);
    }

    public void test_select_16() throws Exception {
        String sql = "SELect distinct id1,id2 from tb1,tb2 where tb1.id1=tb2.id2 for update";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT DISTINCT id1, id2\nFROM tb1, tb2\nWHERE tb1.id1 = tb2.id2\nFOR UPDATE", output);
    }

    public void test_select_17() throws Exception {
        String sql = "SELect distinct id1,id2 from tb1,tb2 where tb1.id1=tb2.id2 lock in share mode";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT DISTINCT id1, id2\nFROM tb1, tb2\nWHERE tb1.id1 = tb2.id2\nLOCK IN SHARE MODE",
                            output);
    }

    public void test_select_18() throws Exception {
        String sql = "SELect t1.id , t2.* from t1, test.t2 where test.t1.id='中''‘文' and t1.id=test.t2.id";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT t1.id, t2.*\nFROM t1, test.t2\nWHERE test.t1.id = '中''‘文'\n\tAND t1.id = test.t2.id",
                            output);
    }

    public void test_select_19() throws Exception {
        String sql = "select * from  offer  a  straight_join wp_image b force index for join(t1,t2) on a.member_id=b.member_id inner join product_visit c where a.member_id=c.member_id and c.member_id='abc' ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT *" + //
                            "\nFROM offer a" + //
                            "\n\tSTRAIGHT_JOIN wp_image b FORCE INDEX FOR JOIN (t1, t2) ON a.member_id = b.member_id" + //
                            "\n\tINNER JOIN product_visit c" + //
                            "\nWHERE a.member_id = c.member_id" + //
                            "\n\tAND c.member_id = 'abc'", output);
    }

    public void test_select_20() throws Exception {
        String sql = "select * from  offer  a  straight_join wp_image b ignore index for join(t1,t2) on a.member_id=b.member_id inner join product_visit c where a.member_id=c.member_id and c.member_id='abc' ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT *\n" + //
                            "FROM offer a" + //
                            "\n\tSTRAIGHT_JOIN wp_image b IGNORE INDEX FOR JOIN (t1, t2) ON a.member_id = b.member_id" + //
                            "\n\tINNER JOIN product_visit c" + //
                            "\nWHERE a.member_id = c.member_id" + //
                            "\n\tAND c.member_id = 'abc'", output);
    }
}
