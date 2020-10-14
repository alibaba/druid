package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest84
        extends TestCase {
    public void test_0() throws Exception {
        String sql =  "select u.id, (\n" +
                "\tWITH RECURSIVE users AS (\n" +
                "\t\tselect id from t_user limit 1\n" +
                "\t) select id from users\n" +
                ") from t_user u \n" +
                "limit 1;";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        System.out.println(stmt);

        assertEquals("SELECT u.id\n" +
                        "\t, (\n" +
                        "\t\tWITH RECURSIVE users AS (\n" +
                        "\t\t\t\tSELECT id\n" +
                        "\t\t\t\tFROM t_user\n" +
                        "\t\t\t\tLIMIT 1\n" +
                        "\t\t\t)\n" +
                        "\t\tSELECT id\n" +
                        "\t\tFROM users\n" +
                        "\t)\n" +
                        "FROM t_user u\n" +
                        "LIMIT 1;"
                , stmt.toString());

        assertEquals("select u.id\n" +
                "\t, (\n" +
                "\t\twith recursive users as (\n" +
                "\t\t\t\tselect id\n" +
                "\t\t\t\tfrom t_user\n" +
                "\t\t\t\tlimit 1\n" +
                "\t\t\t)\n" +
                "\t\tselect id\n" +
                "\t\tfrom users\n" +
                "\t)\n" +
                "from t_user u\n" +
                "limit 1;", stmt.toLowerCaseString());
    }
}
