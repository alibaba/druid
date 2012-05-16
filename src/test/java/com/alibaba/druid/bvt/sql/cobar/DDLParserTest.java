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
public class DDLParserTest extends TestCase {

    public void testTruncate() throws Exception {
        String sql = "Truncate table tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("TRUNCATE TABLE tb1", output);
    }
    
    public void testTruncate_1() throws Exception {
        String sql = "Truncate tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("TRUNCATE TABLE tb1", output);
    }
    
    public void testAlterTable0() throws Exception {
        String sql = "alTer ignore table tb_name";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER IGNORE TABLE tb_name", output);
    }

//    public void testDDLStmt() throws Exception {
//        String sql = "alTer ignore table tb_name";
//        SQLLexer lexer = new SQLLexer(sql);
//        DDLParser parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        DDLStatement dst = parser.ddlStmt();
//
//        sql = "alTeR table tb_name";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//
//        sql = "crEate temporary tabLe if not exists tb_name";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//
//        sql = "crEate tabLe if not exists tb_name";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//
//        sql = "crEate temporary tabLe tb_name";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//
//        sql = "crEate unique index index_name on tb(col(id)) desc";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//
//        sql = "crEate fulltext index index_name on tb(col(id))";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//
//        sql = "crEate spatial index index_name on tb(col(id))";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//
//        sql = "crEate index index_name using hash on tb(col(id))";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//
//        sql = "drop index index_name on tb1";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//        String output = output2MySQL(dst, sql);
//        Assert.assertEquals("DROP INDEX index_name ON tb1", output);
//
//        sql = "drop temporary tabLe if exists tb1,tb2,tb3 restrict";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//        output = output2MySQL(dst, sql);
//        Assert.assertEquals("DROP TEMPORARY TABLE IF EXISTS tb1, tb2, tb3 RESTRICT", output);
//
//        sql = "drop temporary tabLe if exists tb1,tb2,tb3 cascade";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//        output = output2MySQL(dst, sql);
//        Assert.assertEquals("DROP TEMPORARY TABLE IF EXISTS tb1, tb2, tb3 CASCADE", output);
//
//        sql = "drop temporary tabLe if exists tb1 cascade";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//        output = output2MySQL(dst, sql);
//        Assert.assertEquals("DROP TEMPORARY TABLE IF EXISTS tb1 CASCADE", output);
//
//        sql = "drop tabLe if exists tb1 cascade";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//        output = output2MySQL(dst, sql);
//        Assert.assertEquals("DROP TABLE IF EXISTS tb1 CASCADE", output);
//
//        sql = "drop temporary tabLe tb1 cascade";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//        output = output2MySQL(dst, sql);
//        Assert.assertEquals("DROP TEMPORARY TABLE tb1 CASCADE", output);
//
//        sql = "rename table tb1 to ntb1,tb2 to ntb2";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//        output = output2MySQL(dst, sql);
//        Assert.assertEquals("RENAME TABLE tb1 TO ntb1, tb2 TO ntb2", output);
//
//        sql = "rename table tb1 to ntb1";
//        lexer = new SQLLexer(sql);
//        parser = new DDLParser(lexer, new SQLExprParser(lexer));
//        dst = parser.ddlStmt();
//        output = output2MySQL(dst, sql);
//        Assert.assertEquals("RENAME TABLE tb1 TO ntb1", output);
//    }
}
