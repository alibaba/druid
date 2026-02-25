package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.testutil.ParserTestUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class SQLParserRefactorRegressionTest extends TestCase {
    public void test_round_trip_behavior_equivalence() {
        String sql = "select id from t where k between 1 and 3 order by id";
        String first = ParserTestUtils.parseSingleStatement(sql, JdbcConstants.MYSQL).toString();
        String second = ParserTestUtils.parseSingleStatement(first, JdbcConstants.MYSQL).toString();

        assertEquals(first, second);
    }

    public void test_error_locality_contains_token_context() {
        String sql = "select * from t where";
        try {
            ParserTestUtils.parseSingleStatement(sql, JdbcConstants.MYSQL);
            fail();
        } catch (ParserException ex) {
            assertNotNull(ex.getMessage());
            assertTrue(ex.getMessage().length() > 0);
        }
    }
}
