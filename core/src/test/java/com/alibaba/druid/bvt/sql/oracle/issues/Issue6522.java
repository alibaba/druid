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
 * Oracle table alias must not get an AS keyword (Oracle does not allow AS for table aliases).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6522">Issue #6522</a>
 */
public class Issue6522 {
    @Test
    public void test_table_alias_without_as() {
        String sql = "select * from AB01 a where a.AAB999 = '430521463000'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.oracle);
        assertFalse(out.contains("AB01 AS a"), "Oracle table alias must not use AS:\n" + out);
        assertTrue(out.contains("AB01 a"), "table alias should be preserved without AS:\n" + out);
    }
}
