package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest85
        extends TestCase {
    public void test_0() throws Exception {
        String sql =  "select 1 from t_user u\n" +
                "join (\n" +
                "\tWITH RECURSIVE users AS (\n" +
                "\t\tselect id from t_user limit 1\n" +
                "\t) select id from users\n" +
                ") t on u.id = t.id;";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        System.out.println(stmt);

        assertEquals("SELECT 1\n" +
                        "FROM t_user u\n" +
                        "\tJOIN (\n" +
                        "\t\tWITH RECURSIVE users AS (\n" +
                        "\t\t\t\tSELECT id\n" +
                        "\t\t\t\tFROM t_user\n" +
                        "\t\t\t\tLIMIT 1\n" +
                        "\t\t\t)\n" +
                        "\t\tSELECT id\n" +
                        "\t\tFROM users\n" +
                        "\t) t ON u.id = t.id;"
                , stmt.toString());

        assertEquals("select 1\n" +
                "from t_user u\n" +
                "\tjoin (\n" +
                "\t\twith recursive users as (\n" +
                "\t\t\t\tselect id\n" +
                "\t\t\t\tfrom t_user\n" +
                "\t\t\t\tlimit 1\n" +
                "\t\t\t)\n" +
                "\t\tselect id\n" +
                "\t\tfrom users\n" +
                "\t) t on u.id = t.id;", stmt.toLowerCaseString());
    }
}
