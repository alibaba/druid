package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Oracle UNPIVOT 的 IN 列表没有显式别名时，不应输出空的 AS ()。
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6515">Issue #6515</a>
 */
public class Issue6515 {
    @Test
    public void test_unpivot_in_items_without_alias() {
        String sql = "select ins_num, indx_val from (select * from pty_corp_fin) "
                + "unpivot (indx_val for indx_no in (tot_ast, tot_liab)) t";

        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());

        String out = SQLUtils.toSQLString(stmts.get(0), DbType.oracle).replaceAll("\\s+", " ").trim();
        assertTrue(out.contains("IN (tot_ast, tot_liab)"), "expected plain UNPIVOT IN list, got:\n" + out);
        assertFalse(out.contains("AS ()"), "UNPIVOT IN item must not render empty alias list:\n" + out);
    }
}
