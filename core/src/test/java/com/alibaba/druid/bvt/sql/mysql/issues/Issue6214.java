package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6214">Issue #6214</a>
 */
public class Issue6214 {
    @Test
    public void test_create_table_comment_and_collate_are_separate() {
        String sql = "CREATE TABLE t (id BIGINT) "
                + "COMMENT = 'table_comment' "
                + "COLLATE = 'utf8mb4_unicode_ci'";

        SQLCreateTableStatement statement = assertInstanceOf(
                SQLCreateTableStatement.class,
                SQLUtils.parseSingleStatement(sql, DbType.mysql)
        );

        SQLCharExpr comment = assertInstanceOf(
                SQLCharExpr.class,
                statement.getComment()
        );
        assertEquals("table_comment", comment.getText());

        SQLCharExpr collate = assertInstanceOf(
                SQLCharExpr.class,
                statement.getOption("COLLATE")
        );
        assertEquals("utf8mb4_unicode_ci", collate.getText());
    }
}
