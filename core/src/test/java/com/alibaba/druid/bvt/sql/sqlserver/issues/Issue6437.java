package com.alibaba.druid.bvt.sql.sqlserver.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SQL Server COLLATE in a join/where predicate, e.g. a.x = b.y COLLATE Chinese_PRC_CI_AI_WS.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6437">Issue #6437</a>
 */
public class Issue6437 {
    @Test
    public void test_collate_in_join_predicate() {
        String sql = "select * from a join b on a.REPORTLINE = b.REPORT_ITEM_ID COLLATE Chinese_PRC_CI_AI_WS "
                + "WHERE a.referencedate = ?";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.sqlserver);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.sqlserver).contains("COLLATE Chinese_PRC_CI_AI_WS"));
    }
}
