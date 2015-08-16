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

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;


public class DMLUpdateParserTest extends TestCase {
    public void test_update_0() throws Exception {
        String sql = "upDate LOw_PRIORITY IGNORE test.t1 sEt t1.col1=?, col2=DefaulT";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UPDATE LOW_PRIORITY IGNORE test.t1\nSET t1.col1 = ?, col2 = DEFAULT", output);
    }
    
    public void test_update_1() throws Exception {
        String sql = "upDate  IGNORE (t1) set col2=DefaulT order bY t1.col2 ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UPDATE IGNORE t1\nSET col2 = DEFAULT\nORDER BY t1.col2", output);
    }
    
    public void test_update_2() throws Exception {
        String sql = "upDate   (test.t1) SET col2=DefaulT order bY t1.col2 limit ? offset 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UPDATE test.t1\nSET col2 = DEFAULT\nORDER BY t1.col2\nLIMIT 1, ?", output);
    }
    
    public void test_update_3() throws Exception {
        String sql = "upDate LOW_PRIORITY  t1, test.t2 SET col2=DefaulT , col2='123''4'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UPDATE LOW_PRIORITY t1, test.t2\nSET col2 = DEFAULT, col2 = '123''4'", output);
    }
    
    public void test_update_4() throws Exception {
        String sql = "upDate LOW_PRIORITY  t1, test.t2 SET col2:=DefaulT , col2='123''4' where id='a'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UPDATE LOW_PRIORITY t1, test.t2\nSET col2 = DEFAULT, col2 = '123''4'\nWHERE id = 'a'", output);
    }
}
