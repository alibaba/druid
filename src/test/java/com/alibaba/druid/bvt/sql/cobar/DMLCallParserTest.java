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
/**
 * (created at 2011-7-18)
 */
package com.alibaba.druid.bvt.sql.cobar;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

/**
 * @author <a href="mailto:danping.yudp@alibaba-inc.com">YU Danping</a>
 */

public class DMLCallParserTest extends TestCase {
    public void testCall_0() throws Exception {
        String sql = "call p(?,?)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CALL p(?, ?)", output);
    }
    
    public void testCall_1() throws Exception {
        String sql = "call p(@var1,'@var2',var3)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CALL p(@var1, '@var2', var3)", output);
    }
    
    public void testCall_2() throws Exception {
        String sql = "call p()";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CALL p()", output);
    }
    
//    public void testCall() throws SQLSyntaxErrorException {
//        String sql = "call p(?,?) ";
//        SQLLexer lexer = new SQLLexer(sql);
//        DMLCallParser parser = new DMLCallParser(lexer, new SQLExprParser(lexer));
//        DMLCallStatement calls = parser.call();
//        parser.match(Token.EOF);
//        String output = output2MySQL(calls, sql);
//        Assert.assertEquals("CALL p(?, ?)", output);
//
//        sql = "call p(@var1,'@var2',var3)";
//        lexer = new SQLLexer(sql);
//        parser = new DMLCallParser(lexer, new SQLExprParser(lexer));
//        calls = parser.call();
//        parser.match(Token.EOF);
//        output = output2MySQL(calls, sql);
//        Assert.assertEquals("CALL p(@var1, '@var2', var3)", output);
//
//        sql = "call p()";
//        lexer = new SQLLexer(sql);
//        parser = new DMLCallParser(lexer, new SQLExprParser(lexer));
//        calls = parser.call();
//        parser.match(Token.EOF);
//        output = output2MySQL(calls, sql);
//        Assert.assertEquals("CALL p()", output);
//
//        sql = "call p(?)";
//        lexer = new SQLLexer(sql);
//        parser = new DMLCallParser(lexer, new SQLExprParser(lexer));
//        calls = parser.call();
//        parser.match(Token.EOF);
//        output = output2MySQL(calls, sql);
//        Assert.assertEquals("CALL p(?)", output);
//    }
}
