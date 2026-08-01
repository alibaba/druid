package com.alibaba.druid.bvt.sql.dm.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression for issue #6220: DM (Dameng) is Oracle-compatible and supports the
 * {@code DATE 'literal'} syntax, but its lexer did not enable the SQLDateExpr feature,
 * so {@code DATE '2022-02-02'} raised {@code ParserException: not supported ... token LITERAL_CHARS}.
 */
public class Issue6220 {
    private final DbType dbType = DbType.dm;

    @Test
    public void updateWithDateLiteral() {
        String sql = "UPDATE TEST_TABLE SET SQLDATE = DATE'2022-02-02' WHERE TEST_ID = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void dateLiteralRoundTrips() {
        String sql = "SELECT * FROM t WHERE d = DATE'2022-02-02'";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        String result = SQLUtils.toSQLString(stmtList.get(0), dbType);
        assertTrue(result.contains("DATE '2022-02-02'"), "DATE literal must round-trip, got: " + result);
    }
}
