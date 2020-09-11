package com.alibaba.druid.mysql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by tianzhen.wtz on 2016/6/7.
 * 类说明：
 */
public class MysqlLimitTest extends TestCase{


    public void testLimit(){
        String sql = "select * from aaa limit 20exx";
        SQLStatementParser statementParser = SQLParserUtils.createSQLStatementParser(sql, "mysql");
        try {
            List<SQLStatement> sqlStatements = statementParser.parseStatementList();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("is not a number!"));
        }
    }

    public void testLimitWithOffset() {
        String sql = "select * from aaa limit ? offset ?";
        SQLStatementParser statementParser = SQLParserUtils.createSQLStatementParser(sql, "mysql");
        SQLStatement sqlStatement = statementParser.parseStatement();
        assertEquals("select * from aaa limit 10 offset 0", SQLUtils.toSQLString(sqlStatement, "mysql", new SQLUtils.FormatOption(false, false)));
    }
}
