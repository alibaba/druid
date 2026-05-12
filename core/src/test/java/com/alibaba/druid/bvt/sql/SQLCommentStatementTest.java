package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.test.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLCommentStatementTest {
    @Test
    public void test_0() throws Exception {
        String sql = "COMMENT on table t1 IS 'xxx'";

        SQLStatementParser parser = new SQLStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        assertEquals("COMMENT ON TABLE t1 IS 'xxx'", text);
    }
}
