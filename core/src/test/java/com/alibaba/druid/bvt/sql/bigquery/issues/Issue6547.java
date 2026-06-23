package com.alibaba.druid.bvt.sql.bigquery.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * BigQuery UNNEST(...) WITH OFFSET, where the offset alias is optional.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6547">Issue #6547</a>
 */
public class Issue6547 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.bigquery);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.bigquery).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_with_offset_no_alias() {
        assertEquals("SELECT * FROM UNNEST([10, 20, 30]) AS numbers WITH OFFSET",
                rt("SELECT * FROM UNNEST([10,20,30]) as numbers WITH OFFSET"));
    }

    @Test
    public void test_with_offset_alias() {
        assertEquals("SELECT * FROM UNNEST([10, 20, 30]) AS numbers WITH OFFSET AS off",
                rt("SELECT * FROM UNNEST([10,20,30]) as numbers WITH OFFSET AS off"));
    }

    @Test
    public void test_unnest_without_offset_unchanged() {
        assertEquals("SELECT * FROM UNNEST([1, 2]) AS n",
                rt("SELECT * FROM UNNEST([1,2]) as n"));
    }
}
