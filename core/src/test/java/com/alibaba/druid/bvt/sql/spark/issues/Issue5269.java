package com.alibaba.druid.bvt.sql.spark.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spark SQL file-format table reference, e.g. FROM parquet.`oss://bucket/path`.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/5269">Issue #5269</a>
 */
public class Issue5269 {
    @Test
    public void test_format_path_table_source() {
        String sql = "select sum(show_cnt) from parquet.`oss://bucket/path` limit 1000";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.spark);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.spark).contains("parquet.`oss://bucket/path`"));
    }
}
