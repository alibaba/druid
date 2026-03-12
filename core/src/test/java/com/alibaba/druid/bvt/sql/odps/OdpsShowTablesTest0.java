package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsShowTablesTest0 {
    @Test
    public void test_0() throws Exception {
        String sql = "show tables";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        assertEquals("SHOW TABLES", output);
    }

    @Test
    public void test_from() throws Exception {
        String sql = "show tables from xx";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        assertEquals("SHOW TABLES FROM xx", SQLUtils.toOdpsString(stmt));
        assertEquals("show tables from xx", SQLUtils.toOdpsString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

    @Test
    public void test_from_like() throws Exception {
        String sql = "show tables from xx like '*'";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        assertEquals("SHOW TABLES FROM xx LIKE '*'", SQLUtils.toOdpsString(stmt));
        assertEquals("show tables from xx like '*'", SQLUtils.toOdpsString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
