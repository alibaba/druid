package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SQLASTOutputVisitorSplitRefactorTest {
    @Test
    public void test_roundTripStable_forBinaryGroupAndBetween_mysql() {
        String sql = "select * from t where (a between 1 and 2) and (b = 3 or b = 4)";
        assertRoundTripStable(sql, DbType.mysql);
    }

    @Test
    public void test_roundTripStable_forBinaryGroupAndBetween_oracle() {
        String sql = "select * from dual where (a = 1 or b = 2) and c between 3 and 4";
        assertRoundTripStable(sql, DbType.oracle);
    }

    @Test
    public void test_roundTripStable_forComplexDmlBoundary_mysql() {
        String sql = "update t set c = c + 1 where (a = 1 or a = 2 or a = 3) and d between 10 and 20";
        assertRoundTripStable(sql, DbType.mysql);
    }

    private void assertRoundTripStable(String sql, DbType dbType) {
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, dbType);
        String first = SQLUtils.toSQLString(stmt, dbType);

        SQLStatement reparsed = SQLUtils.parseSingleStatement(first, dbType);
        String second = SQLUtils.toSQLString(reparsed, dbType);

        assertEquals(first, second);
    }
}
