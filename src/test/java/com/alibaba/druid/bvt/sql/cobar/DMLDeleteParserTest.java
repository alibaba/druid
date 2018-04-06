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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class DMLDeleteParserTest extends TestCase {

    public void testDelete_0() throws Exception {
        String sql = "deLetE LOW_PRIORITY from id1.id , id using t1 a where col1 =? ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE LOW_PRIORITY FROM id1.id, id\n" +
                "USING t1 a\n" +
                "WHERE col1 = ?", output);
    }

    public void testDelete_1() throws Exception {
        String sql = "deLetE from id1.id  using t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE FROM id1.id" + //
                            "\nUSING t1", output);
    }

    public void testDelete_2() throws Exception {
        String sql = "delete from offer.*,wp_image.* using offer a,wp_image b where a.member_id=b.member_id and a.member_id='abc' ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE FROM offer.*, wp_image.*\n" +
                "USING offer a, wp_image b\n" +
                "WHERE a.member_id = b.member_id\n" +
                "\tAND a.member_id = 'abc'", output);
    }

    public void testDelete_3() throws Exception {
        String sql = "deLetE from id1.id where col1='adf' limit 1,?";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE FROM id1.id\n" + //
                            "WHERE col1 = 'adf'\n" + //
                            "LIMIT 1, ?", output);
    }

    public void testDelete_4() throws Exception {
        String sql = "deLetE from id where col1='adf' ordEr by d liMit ? offset 2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE FROM id\n" + //
                            "WHERE col1 = 'adf'\n" + //
                            "ORDER BY d\n" + //
                            "LIMIT 2, ?", output);
    }

    public void testDelete_5() throws Exception {
        String sql = "deLetE id.* from t1,t2 where col1='adf'            and col2=1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE id.*\n" + //
                            "FROM t1, t2\n" + //
                            "WHERE col1 = 'adf'\n" + //
                            "\tAND col2 = 1", output);
    }

    public void testDelete_6() throws Exception {
        String sql = "deLetE id,id.t from t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE id, id.t\n" + //
                            "FROM t1", output);
    }

    public void testDelete_7() throws Exception {
        String sql = "deLetE from t1 where t1.id1='abc' order by a limit 5";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE FROM t1\n" + //
                            "WHERE t1.id1 = 'abc'\n" + //
                            "ORDER BY a\n" + //
                            "LIMIT 5", output);
    }
    
    public void testDelete_8() throws Exception {
        String sql = "deLetE from t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE FROM t1", output);
    }
    
    public void testDelete_9() throws Exception {
        String sql = "deLetE ignore tb1.*,id1.t from t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE IGNORE tb1.*, id1.t\n" +
        		"FROM t1", output);
    }
    
    public void testDelete_10() throws Exception {
        String sql = "deLetE quick tb1.*,id1.t from t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DELETE QUICK tb1.*, id1.t\n" +
                "FROM t1", output);
    }
}
