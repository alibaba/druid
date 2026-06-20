package com.alibaba.druid.bvt.sql.dm.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DM (Dameng) LISTAGG(...) WITHIN GROUP (ORDER BY ...) aggregate parsing.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6648">Issue #6648</a>
 */
public class Issue6648 {
    @Test
    public void test_listagg_within_group() {
        String sql = "SELECT field_1, listagg(field_4, ':') WITHIN GROUP (ORDER BY field_0 desc) result "
                + "FROM t GROUP BY field_1";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.dm).contains("WITHIN GROUP (ORDER BY field_0 DESC)"));
    }
}
