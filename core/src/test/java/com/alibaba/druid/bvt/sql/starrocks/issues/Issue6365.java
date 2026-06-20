package com.alibaba.druid.bvt.sql.starrocks.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * StarRocks map/array bracket index access: my_map[key]. The subscript must round-trip with its
 * base expression (and not collapse to ARRAY[...]).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6365">Issue #6365</a>
 */
public class Issue6365 {
    private static String rt(String sql) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.starrocks);
        assertEquals(1, stmtList.size());
        return SQLUtils.toSQLString(stmtList.get(0), DbType.starrocks).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_map_bracket_in_select_and_where() {
        // subscript must be preserved (not collapsed to ARRAY[...]) in both SELECT and WHERE
        String out = rt("SELECT * FROM (SELECT exp_tags[111] FROM tablea WHERE exp_tags[111] = 22222) t");
        assertEquals(2, out.split("exp_tags\\[111\\]", -1).length - 1, "both subscripts kept: " + out);
    }

    @Test
    public void test_map_bracket_simple() {
        assertEquals("SELECT my_map['key'] FROM t", rt("SELECT my_map['key'] FROM t"));
    }

    @Test
    public void test_array_bracket_with_int() {
        assertEquals("SELECT arr[1] FROM t", rt("SELECT arr[1] FROM t"));
    }
}
