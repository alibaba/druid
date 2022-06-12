package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest87 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from mc_job a inner join (mc_role b inner join mc_source c on b.id = c.id) as tem2 on tem2.user_id = a.id;";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        assertEquals("SELECT *\n" +
                        "FROM mc_job a\n" +
                        "\tINNER JOIN (mc_role b\n" +
                        "\t\tINNER JOIN mc_source c ON b.id = c.id) AS tem2 ON tem2.user_id = a.id;"
                , stmt.toString());

        assertEquals("select *\n" +
                "from mc_job a\n" +
                "\tinner join (mc_role b\n" +
                "\t\tinner join mc_source c on b.id = c.id) as tem2 on tem2.user_id = a.id;", stmt.toLowerCaseString());
    }
}
