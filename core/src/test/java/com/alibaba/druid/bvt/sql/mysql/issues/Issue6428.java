package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A back-quoted column name that collides with the CLUSTERED keyword must be treated as an
 * ordinary column, not a CLUSTERED KEY/INDEX clause.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6428">Issue #6428</a>
 */
public class Issue6428 {
    @Test
    public void test_backquoted_clustered_column() {
        String sql = "CREATE TABLE t1 (`clustered` tinyint(1) DEFAULT NULL COMMENT 'abc')";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.mysql).contains("`clustered` tinyint(1)"));
    }

    @Test
    public void test_clustered_key_still_parses() {
        // unquoted CLUSTERED KEY must still be recognized
        String sql = "CREATE TABLE t1 (id int, CLUSTERED KEY idx (id))";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.mysql).toUpperCase().contains("CLUSTERED"));
    }
}
