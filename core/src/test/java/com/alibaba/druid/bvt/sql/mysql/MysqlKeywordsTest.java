package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MysqlKeywordsTest {
    @Test
    public void test_keywords() {
        DbType dbType = DbType.mysql;
        String sql = "SELECT TIMESTAMPADD(MONTH, 2, sysdate()) FROM dual";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql,
                dbType,
                SQLParserFeature.IgnoreNameQuotes);
        SQLStatement stmt = parser.parseStatement();
        assertEquals(Token.EOF, parser.getLexer().token());
        String result = SQLUtils.toSQLString(stmt, dbType, null, VisitorFeature.OutputNameQuote).trim();
        String expectedSql = "SELECT TIMESTAMPADD(MONTH, 2, sysdate())\n" + "FROM dual";
        assertEquals(expectedSql, result);
    }
}
