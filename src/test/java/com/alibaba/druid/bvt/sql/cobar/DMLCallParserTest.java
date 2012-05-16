/**
 * (created at 2011-7-18)
 */
package com.alibaba.druid.bvt.sql.cobar;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

import junit.framework.TestCase;

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
