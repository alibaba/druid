package com.alibaba.druid.bvt.sql.derby;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression for issue #3612: Derby supports {@code FETCH FIRST n ROWS ONLY} and does NOT
 * support {@code LIMIT}. {@code SQLUtils.toSQLString(..., derby)} previously rendered the
 * fetch clause as {@code LIMIT n}, producing invalid Derby SQL.
 */
public class DerbyFetchFirstTest {
    @Test
    public void fetchFirstRendersAsFetchNotLimit() {
        String sql = "SELECT * FROM mytest FETCH FIRST 200 ROWS ONLY";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "derby");

        String result = SQLUtils.toSQLString(stmts.get(0), com.alibaba.druid.DbType.derby);
        assertEquals("SELECT *\nFROM mytest\nFETCH FIRST 200 ROWS ONLY", result);
    }
}
