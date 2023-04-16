package com.alibaba.druid.bvt.sql.postgresql.select;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5225">修复bug，加上FETCH FIRST 1 rows only的支持</a>
 */
public class PGSelectTest88 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT xxx FROM table_test OFFSET 10 ROWS FETCH FIRST 5 rows only;";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);
        System.out.println(stmt.toString());

        assertEquals("SELECT xxx\n"
                + "FROM table_test\n"
                + "OFFSET 10\n"
                + "FETCH FIRST 5 ROWS ONLY;"
                , stmt.toString());

        assertEquals("select xxx\n"
            + "from table_test\n"
            + "offset 10\n"
            + "fetch first 5 rows only;", stmt.toLowerCaseString());
    }
    public void test_1() throws Exception {
        String sql = "SELECT xxx FROM table_test limit 5 OFFSET 10;";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);
        System.out.println(stmt.toString());

        assertEquals("SELECT xxx\n"
                + "FROM table_test\n"
                + "LIMIT 5 OFFSET 10;"
            , stmt.toString());

        assertEquals("select xxx\n"
            + "from table_test\n"
            + "limit 5 offset 10;", stmt.toLowerCaseString());
    }
}
