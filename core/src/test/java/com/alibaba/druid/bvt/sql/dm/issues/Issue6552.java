package com.alibaba.druid.bvt.sql.dm.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DM (Dameng) LISTAGG over a derived table with DISTINCT.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6552">Issue #6552</a>
 */
public class Issue6552 {
    @Test
    public void test_listagg_over_derived_table() {
        String sql = "SELECT project_id, LISTAGG(seq, ',') as ids FROM ("
                + "SELECT DISTINCT t1.project_id, t2.seq FROM a t1 LEFT JOIN b t2 ON t1.id = t2.id) "
                + "GROUP BY project_id";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmts.size());
    }
}
