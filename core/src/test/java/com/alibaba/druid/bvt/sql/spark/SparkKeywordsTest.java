package com.alibaba.druid.bvt.sql.spark;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import junit.framework.TestCase;
import static org.junit.Assert.*;
import org.junit.Test;

public class SparkKeywordsTest extends TestCase {
    public void test_keywords() {
        DbType dbType = DbType.spark;
        String sql = "select 1 as authorization";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql,
                dbType,
                SQLParserFeature.IgnoreNameQuotes);
        SQLStatement stmt = parser.parseStatement();
        assertEquals(Token.EOF, parser.getLexer().token());
        String result = SQLUtils.toSQLString(stmt, dbType, null, VisitorFeature.OutputNameQuote).trim();
        String expectedSql = "SELECT 1 AS `authorization`";
        assertEquals(expectedSql, result);
    }
}
