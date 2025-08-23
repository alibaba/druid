package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class HiveKeywordsTest extends TestCase {

    public void test_keywords() {
        DbType dbType = DbType.hive;
        String sql = "select 1 as TIMESTAMPLOCALTZ";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql,
                dbType,
                SQLParserFeature.IgnoreNameQuotes);
        SQLStatement stmt = parser.parseStatement();
        assertEquals(Token.EOF, parser.getLexer().token());
        String result = SQLUtils.toSQLString(stmt, dbType, null, VisitorFeature.OutputNameQuote).trim();
        String expectedSql = "SELECT 1 AS `TIMESTAMPLOCALTZ`";
        assertEquals(expectedSql, result);
    }

    public void test_keywords2() {
        DbType dbType = DbType.hive;
        String sql = "select date(d) from t";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql,
                dbType,
                SQLParserFeature.IgnoreNameQuotes);
        SQLStatement stmt = parser.parseStatement();
        assertEquals(Token.EOF, parser.getLexer().token());
        String result = SQLUtils.toSQLString(stmt, dbType, null, VisitorFeature.OutputNameQuote).trim();
        String expectedSql = "SELECT date(d)\n" + "FROM t";
        assertEquals(expectedSql, result);
    }
}
