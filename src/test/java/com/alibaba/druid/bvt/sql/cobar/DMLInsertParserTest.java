/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

public class DMLInsertParserTest extends TestCase {

    public void testInsert_0() throws Exception {
        String sql = "insErt HIGH_PRIORITY intO test.t1 seT t1.id1=?, id2 := '123'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT HIGH_PRIORITY INTO test.t1 (t1.id1, id2)\n" + //
                            "VALUES (?, '123')", output);
    }

    public void testInsert_1() throws Exception {
        String sql = "insErt  IGNORE test.t1 seT t1.id1:=? oN dupLicatE key UPDATE ex.col1=?, col2=12";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT IGNORE INTO test.t1 (t1.id1)\nVALUES (?)" + //
                            "\nON DUPLICATE KEY UPDATE ex.col1 = ?, col2 = 12", output);
    }

    public void testInsert_2() throws Exception {
        String sql = "insErt t1 value (123,?) oN dupLicatE key UPDATE ex.col1=?";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT INTO t1\nVALUES (123, ?)\nON DUPLICATE KEY UPDATE ex.col1 = ?", output);
    }

    public void testInsert_3() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 valueS (12e-2,1,2), (?),(default)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1\nVALUES (0.12, 1, 2)," + //
                            "\n\t(?)," + //
                            "\n\t(DEFAULT)", output);
    }

    public void testInsert_4() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 select id from t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1\nSELECT id\nFROM t1", output);
    }

    public void testInsert_5() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 (select id from t1) oN dupLicatE key UPDATE ex.col1=?, col2=12";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1\nSELECT id\nFROM t1" + //
        		"\nON DUPLICATE KEY UPDATE ex.col1 = ?, col2 = 12",
                            output);
    }

    public void testInsert_6() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 (t1.col1) valueS (123),('12''34')";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1 (t1.col1)\nVALUES (123)," + //
        		"\n\t('12''34')", output);
    }

    public void testInsert_8() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 (col1, t1.col2) select id from t3 oN dupLicatE key UPDATE ex.col1=?";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1 (col1, t1.col2)\nSELECT id\nFROM t3" + //
        		"\nON DUPLICATE KEY UPDATE ex.col1 = ?",
                            output);
    }

    public void testInsert_9() throws Exception {
        String sql = "insErt LOW_PRIORITY IGNORE intO t1 (col1) ( select id from t3) ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY IGNORE INTO t1 (col1)\nSELECT id\nFROM t3", output);
    }
}
