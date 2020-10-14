package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest83
        extends TestCase {
    public void test_0() throws Exception {
        String sql =  "select PERCENTILE_CONT(0.5) WITHIN GROUP ( ORDER BY m asc ) from\n" +
                "(\n" +
                "select distinct extract(month from paidon) as m from core_order where paidon between '2019-1-01' and '2019-5-01' order by m asc\n" +
                ")a";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        System.out.println(stmt);

        assertEquals("SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY m ASC)\n" +
                        "FROM (\n" +
                        "\t(SELECT DISTINCT extract(month FROM paidon) AS m\n" +
                        "\tFROM core_order\n" +
                        "\tWHERE paidon BETWEEN '2019-1-01' AND '2019-5-01'\n" +
                        "\tORDER BY m ASC)\n" +
                        ") a"
                , stmt.toString());

        assertEquals("select percentile_cont(0.5 within group (order by m asc)\n" +
                "from (\n" +
                "\t(select distinct extract(month from paidon) as m\n" +
                "\tfrom core_order\n" +
                "\twhere paidon between '2019-1-01' and '2019-5-01'\n" +
                "\torder by m asc)\n" +
                ") a", stmt.toLowerCaseString());
    }
}
