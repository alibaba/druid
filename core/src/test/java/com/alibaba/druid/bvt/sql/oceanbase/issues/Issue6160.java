package com.alibaba.druid.bvt.sql.oceanbase.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Fix OceanBase/Oracle SUBPARTITION TEMPLATE parsing in CREATE TABLE.
 * <p>
 * The MySQL parser (used by OceanBase) only recognized SUBPARTITION OPTIONS
 * after the subpartition-by clause, but not SUBPARTITION TEMPLATE which is
 * used to define template subpartitions for composite partitioning.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6160">Issue #6160</a>
 */
public class Issue6160 {
    @Test
    public void test_subpartition_template_list_range() {
        // Exact SQL from the issue
        String sql = "CREATE TABLE t2_m_lr(col1 INT, col2 INT)\n"
                + "PARTITION BY LIST (col1)\n"
                + "SUBPARTITION BY RANGE(col2)\n"
                + "SUBPARTITION TEMPLATE\n"
                + " (SUBPARTITION mp0 VALUES LESS THAN(100),\n"
                + "  SUBPARTITION mp1 VALUES LESS THAN(200),\n"
                + "  SUBPARTITION mp2 VALUES LESS THAN(300))\n"
                + " (PARTITION p0 VALUES IN(1,3),\n"
                + "  PARTITION p1 VALUES IN(4,6),\n"
                + "  PARTITION p2 VALUES IN(7,9))";

        for (DbType dbType : new DbType[]{DbType.oceanbase, DbType.mysql}) {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
            List<SQLStatement> stmtList = parser.parseStatementList();
            assertEquals(1, stmtList.size());
        }
    }

    @Test
    public void test_subpartition_template_range_hash() {
        String sql = "CREATE TABLE t_range_hash(col1 INT, col2 INT)\n"
                + "PARTITION BY RANGE(col1)\n"
                + "SUBPARTITION BY HASH(col2)\n"
                + "SUBPARTITION TEMPLATE\n"
                + " (SUBPARTITION sp0,\n"
                + "  SUBPARTITION sp1,\n"
                + "  SUBPARTITION sp2)\n"
                + " (PARTITION p0 VALUES LESS THAN(100),\n"
                + "  PARTITION p1 VALUES LESS THAN(200))";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oceanbase);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_subpartition_template_with_values_in() {
        String sql = "CREATE TABLE t_range_list(col1 INT, col2 INT)\n"
                + "PARTITION BY RANGE(col1)\n"
                + "SUBPARTITION BY LIST(col2)\n"
                + "SUBPARTITION TEMPLATE\n"
                + " (SUBPARTITION sp0 VALUES IN(1,2,3),\n"
                + "  SUBPARTITION sp1 VALUES IN(4,5,6))\n"
                + " (PARTITION p0 VALUES LESS THAN(100),\n"
                + "  PARTITION p1 VALUES LESS THAN(200))";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oceanbase);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_subpartition_options_still_works() {
        // Ensure existing SUBPARTITION OPTIONS parsing is not broken
        String sql = "CREATE TABLE t_opt(id INT)\n"
                + "PARTITION BY HASH(id)\n"
                + "SUBPARTITION BY KEY(id)\n"
                + "SUBPARTITIONS 4";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }
}
