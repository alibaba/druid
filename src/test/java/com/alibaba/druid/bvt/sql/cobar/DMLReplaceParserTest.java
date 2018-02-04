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


public class DMLReplaceParserTest extends TestCase {
    public void testReplace_0() throws Exception {
        String sql = "ReplaCe LOW_PRIORITY intO test.t1 seT t1.id1:=?, id2='123'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE LOW_PRIORITY INTO test.t1 (t1.id1, id2)\nVALUES (?, '123')", output);
    }
    
    public void testReplace_1() throws Exception {
        String sql = "ReplaCe   test.t1 seT t1.id1:=? ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE INTO test.t1 (t1.id1)\nVALUES (?)", output);
    }
    
    public void testReplace_2() throws Exception {
        String sql = "ReplaCe t1 value (123,?) ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE INTO t1\nVALUES (123, ?)", output);
    }
    
    public void testReplace_3() throws Exception {
        String sql = "ReplaCe LOW_PRIORITY t1 valueS (12e-2), (?)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE LOW_PRIORITY INTO t1\nVALUES (12e-2), (?)", output);
    }
    
    public void testReplace_4() throws Exception {
        String sql = "ReplaCe LOW_PRIORITY t1 select id from t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE LOW_PRIORITY INTO t1\n\tSELECT id\n\tFROM t1", output);
    }
    
    public void testReplace_5() throws Exception {
        String sql = "ReplaCe delayed t1 select id from t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE DELAYED INTO t1\n\tSELECT id\n\tFROM t1", output);
    }
    
    public void testReplace_6() throws Exception {
        String sql = "ReplaCe LOW_PRIORITY t1 (select id from t1) ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE LOW_PRIORITY INTO t1\n\tSELECT id\n\tFROM t1", output);
    }
    
    public void testReplace_7() throws Exception {
        String sql = "ReplaCe LOW_PRIORITY t1 (t1.col1) valueS (123),('12''34')";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE LOW_PRIORITY INTO t1 (t1.col1)\nVALUES (123), ('12''34')", output);
    }
    
    public void testReplace_8() throws Exception {
        String sql = "ReplaCe LOW_PRIORITY t1 (col1, t1.col2) VALUE (123,'123\\'4') ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE LOW_PRIORITY INTO t1 (col1, t1.col2)\nVALUES (123, '123''4')", output);
    }
    
    public void testReplace_9() throws Exception {
        String sql = "REPLACE LOW_PRIORITY t1 (col1, t1.col2) select id from t3 ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE LOW_PRIORITY INTO t1 (col1, t1.col2)\n\tSELECT id\n\tFROM t3", output);
    }
    
    public void testReplace_10() throws Exception {
        String sql = "replace LOW_PRIORITY  intO t1 (col1) ( select id from t3) ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("REPLACE LOW_PRIORITY INTO t1 (col1)\n" +
                "\t(SELECT id\n" +
                "\tFROM t3)", output);
    }
}
