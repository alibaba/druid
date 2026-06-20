package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PostgreSQL INSERT ... WITH cte ... SELECT must parse as a single statement, not be split in two.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6498">Issue #6498</a>
 */
public class Issue6498 {
    @Test
    public void test_insert_with_cte_select_is_one_statement() {
        String sql = "INSERT INTO category (\"name\") WITH t1 AS (SELECT 'aaaa' c) SELECT c FROM t1 WHERE 1 = 0";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
    }
}
