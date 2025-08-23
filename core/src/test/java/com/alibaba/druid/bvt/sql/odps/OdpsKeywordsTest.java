package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import org.junit.Test;

import static org.junit.Assert.*;

public class OdpsKeywordsTest {
    @Test
    public void test_keywords() {
        DbType dbType = DbType.odps;
        String sql = "select 1 as function";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql,
                dbType,
                SQLParserFeature.IgnoreNameQuotes);
        SQLStatement stmt = parser.parseStatement();
        assertEquals(Token.EOF, parser.getLexer().token());
        String result = SQLUtils.toSQLString(stmt, dbType, null, VisitorFeature.OutputNameQuote).trim();
        String expectedSql = "SELECT 1 AS `function`";
        assertEquals(expectedSql, result);
    }
}
