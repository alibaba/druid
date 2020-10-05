package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest81
        extends TestCase {
    public void test_0() throws Exception {
        String sql =  "select field from table where field like 'aa''a';";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        System.out.println(stmt);

        assertEquals("SELECT field\n" +
                "FROM table\n" +
                "WHERE field LIKE 'aa''a';"
                , stmt.toString());

        assertEquals("select field\n" +
                "from table\n" +
                "where field like 'aa''a';", stmt.toLowerCaseString());
    }
}
