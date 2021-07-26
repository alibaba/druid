package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest87
        extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from table1 limit 10 offset 100";
        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);
        assertEquals("select *\n" +
                "from table1\n" +
                "limit 10 offset 100", stmt.toLowerCaseString());
    }

    public void test_1() throws Exception {
        String sql = "select * from table1 offset 100 limit 10";
        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);
        assertEquals("select *\n" +
                "from table1\n" +
                "offset 100 limit 10", stmt.toLowerCaseString());
    }

    public void test_2() throws Exception {
        String sql = "select * from table1 offset 100";
        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);
        assertNotNull(stmt);
        assertEquals("select *\n" +
                "from table1\n" +
                "offset 100", stmt.toLowerCaseString());
    }

}
