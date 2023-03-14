package com.alibaba.druid.bvt.sql.presto;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrestoAlterFunction_0 {
    @Test
    public void test_alter_function_called() {
        String sql = "ALTER FUNCTION prod.default.tan(double)\n" +
                "CALLED ON NULL INPUT";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        SQLStatement stmt = parser.parseStatement();

        assertEquals("ALTER FUNCTION prod.default.tan(double) CALLED ON NULL INPUT", stmt.toString());
    }

    @Test
    public void test_alter_function_returns() {
        String sql = "ALTER FUNCTION prod.default.tan\n" +
                "CALLED ON NULL INPUT";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        SQLStatement stmt = parser.parseStatement();

        assertEquals("ALTER FUNCTION prod.default.tan CALLED ON NULL INPUT", stmt.toString());
    }
}
