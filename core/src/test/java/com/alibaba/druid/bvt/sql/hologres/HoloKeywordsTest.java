package com.alibaba.druid.bvt.sql.hologres;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import junit.framework.TestCase;
import static org.junit.Assert.*;

public class HoloKeywordsTest extends TestCase {
    public void test_keywords() {
        DbType dbType = DbType.hologres;
        String sql = "select 1 as default";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql,
                dbType,
                SQLParserFeature.IgnoreNameQuotes);
        SQLStatement stmt = parser.parseStatement();
        assertEquals(Token.EOF, parser.getLexer().token());
        String result = SQLUtils.toSQLString(stmt, dbType, null, VisitorFeature.OutputNameQuote).trim();
        String expectedSql = "SELECT 1 AS \"default\"";
        assertEquals(expectedSql, result);
    }

    public void test_keywords2() {
        DbType dbType = DbType.hologres;
        String sql = "select a from default.test";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql,
                dbType,
                SQLParserFeature.IgnoreNameQuotes);
        SQLStatement stmt = parser.parseStatement();
        assertEquals(Token.EOF, parser.getLexer().token());
        String result = SQLUtils.toSQLString(stmt, dbType, null, VisitorFeature.OutputNameQuote).trim();
    String expectedSql = "SELECT a\n" + "FROM \"DEFAULT\".test";
        assertEquals(expectedSql, result);
    }
}
