package com.alibaba.druid.bvt.sql.h2;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression for issue #4301: H2 (Oracle-compatibility mode) supports
 * {@code LISTAGG(...) WITHIN GROUP (ORDER BY ...)}, but the H2 parser did not
 * treat LISTAGG as an aggregate, so the WITHIN GROUP clause failed to parse.
 */
public class H2ListaggWithinGroupTest {
    @Test
    public void listaggWithinGroup() {
        String sql = "SELECT listagg(x, ',') WITHIN GROUP (ORDER BY length(fdn) ASC) FROM t";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "h2");
        assertEquals(1, stmts.size());
    }

    @Test
    public void listaggWithinGroupRoundTrips() {
        String sql = "SELECT listagg(x, ',') WITHIN GROUP (ORDER BY fdn) FROM t";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "h2");
        String result = SQLUtils.toSQLString(stmts.get(0), com.alibaba.druid.DbType.h2);
        assertEquals("SELECT listagg(x, ',') WITHIN GROUP (ORDER BY fdn)\nFROM t", result);
    }
}
