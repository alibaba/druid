package com.alibaba.druid.bvt.sql.sqlserver.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SQL Server OUTER APPLY / CROSS APPLY.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/5211">Issue #5211</a>
 */
public class Issue5211 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.sqlserver);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.sqlserver).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_outer_apply() {
        assertTrue(rt("select t1.name from tableA as t1 outer apply (select * from tableB t2 where t2.id = 1) where t1.name = ?")
                .contains("OUTER APPLY"));
    }

    @Test
    public void test_cross_apply() {
        assertTrue(rt("select t1.name from tableA t1 cross apply (select * from tableB t2 where t2.id = 1) t")
                .contains("CROSS APPLY"));
    }
}
