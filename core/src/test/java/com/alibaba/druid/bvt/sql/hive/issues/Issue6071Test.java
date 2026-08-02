package com.alibaba.druid.bvt.sql.hive.issues;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Hive CREATE TABLE 在 PARTITIONED BY 与 LOCATION 之间出现 COMMENT 时解析失败的问题。
 *
 * @author fudianchn
 * @see <a href="https://github.com/alibaba/druid/issues/6071">Issue #6071</a>
 */
public class Issue6071Test {
    @Test
    public void commentBetweenPartitionedByAndLocation() {
        String sql = "CREATE TABLE IF NOT EXISTS test.t (\n"
            + "  bat_no string COMMENT '批号'\n"
            + ")\n"
            + "PARTITIONED BY (acct_prd string comment '会计期')\n"
            + "COMMENT '明细表'\n"
            + "LOCATION 'obs://bucket/path'\n"
            + "TBLPROPERTIES ('type'='mor')";

        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "hive");
        assertEquals(1, stmts.size());

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) stmts.get(0);
        assertNotNull(stmt.getComment(), "table COMMENT must be captured");
        assertNotNull(stmt.getLocation(), "LOCATION must be captured");
    }

    @Test
    public void commentAfterLocationStillWorks() {
        // regression guard: COMMENT after LOCATION (the previously supported order) still parses
        String sql = "CREATE TABLE t (id int) LOCATION 'loc' COMMENT 'c'";
        SQLCreateTableStatement stmt = (SQLCreateTableStatement)
                SQLUtils.parseStatements(sql, "hive").get(0);
        assertNotNull(stmt.getComment());
        assertNotNull(stmt.getLocation());
    }
}
