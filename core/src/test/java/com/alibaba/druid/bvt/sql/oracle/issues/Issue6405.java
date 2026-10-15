package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Oracle INTERVAL DAY TO HOUR (and DAY TO MINUTE) must keep their TO unit; previously every
 * TO unit other than SECOND was incorrectly rendered as MONTH.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6405">Issue #6405</a>
 */
public class Issue6405 {
    private static String rt(String fragment) {
        String sql = "INSERT INTO t (c) VALUES (INTERVAL '1 2' " + fragment + ")";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.oracle);
    }

    @Test
    public void test_day_to_hour() {
        assertTrue(rt("DAY(2) TO HOUR").contains("DAY(2) TO HOUR"), "should keep TO HOUR");
    }

    @Test
    public void test_day_to_minute() {
        assertTrue(rt("DAY TO MINUTE").contains("DAY TO MINUTE"), "should keep TO MINUTE");
    }

    @Test
    public void test_day_to_second_unchanged() {
        assertTrue(rt("DAY(3) TO SECOND(2)").contains("DAY(3) TO SECOND(2)"), "should keep TO SECOND");
    }

    @Test
    public void test_year_to_month_unchanged() {
        assertTrue(rt("YEAR TO MONTH").contains("YEAR TO MONTH"), "should keep TO MONTH");
    }

    @Test
    public void test_hour_to_minute() {
        assertTrue(rt("HOUR TO MINUTE").contains("HOUR TO MINUTE"), "should keep TO MINUTE");
    }
}
