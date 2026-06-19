package com.alibaba.druid.bvt.sql.starrocks.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * StarRocks map/array bracket index access: my_map[key].
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6365">Issue #6365</a>
 */
public class Issue6365 {
    @Test
    public void test_map_bracket_in_select_and_where() {
        String sql = "SELECT * FROM (SELECT exp_tags[111] FROM tablea WHERE exp_tags[111] = 22222) t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.starrocks);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_map_bracket_simple() {
        String sql = "SELECT my_map['key'] FROM t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.starrocks);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_array_bracket_with_int() {
        String sql = "SELECT arr[1] FROM t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.starrocks);
        assertEquals(1, stmtList.size());
    }
}
