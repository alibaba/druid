package com.alibaba.druid.bvt.sql.oceanbase.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UPDATE ... LIMIT must parse (OceanBase / MySQL-compatible).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6546">Issue #6546</a>
 */
public class Issue6546 {
    @Test
    public void test_update_with_limit() {
        String sql = "update t set id = last_insert_id(id + 1) limit 1";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oceanbase);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.oceanbase).contains("LIMIT 1"));
    }
}
