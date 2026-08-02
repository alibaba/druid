package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression for issue #3480: PostgreSQL's trailing array column type
 * ({@code <type> ARRAY} / {@code <type> ARRAY[n]}) failed to parse.
 */
public class PostgresTrailingArrayTypeTest {
    @Test
    public void columnWithBareTrailingArray() {
        String sql = "CREATE TABLE tb_menu (id bigserial NOT NULL, reference_ids integer ARRAY, PRIMARY KEY (id))";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "postgresql");
        assertEquals(1, stmts.size());
    }

    @Test
    public void columnWithSizedTrailingArray() {
        String sql = "CREATE TABLE t (id int, scores integer ARRAY[4])";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "postgresql");
        assertEquals(1, stmts.size());
    }

    @Test
    public void plainColumnArraySyntaxStillWorks() {
        // Regression guard: the int[] form must keep working.
        String sql = "CREATE TABLE t (id int, refs integer[])";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "postgresql");
        assertEquals(1, stmts.size());
    }
}
