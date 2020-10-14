package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest82
        extends TestCase {
    public void test_0() throws Exception {
        String sql =  "select count(distinct h.user_id) \n" +
                " FROM dblink (\n" +
                "'dbname=dz_statistics host=127.0.0.1 user=xxx  password= xxx ',\n" +
                "'select user_id from user_product_view_history where create_time > ''2019-10-04 00:00:00''')\n" +
                "AS h (user_id VARCHAR(32)) \n" +
                "\n" +
                "left join\n" +
                "core_order o\n" +
                "on h.user_id=o.buyer_id";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        System.out.println(stmt);

        assertEquals("SELECT count(DISTINCT h.user_id)\n" +
                        "FROM dblink('dbname=dz_statistics host=127.0.0.1 user=xxx  password= xxx ', 'select user_id from user_product_view_history where create_time > ''2019-10-04 00:00:00''') AS h(user_id VARCHAR(32))\n" +
                        "\tLEFT JOIN core_order o ON h.user_id = o.buyer_id"
                , stmt.toString());

        assertEquals("select count(DISTINCT h.user_id)\n" +
                "from dblink('dbname=dz_statistics host=127.0.0.1 user=xxx  password= xxx ', 'select user_id from user_product_view_history where create_time > ''2019-10-04 00:00:00''') as h(user_id VARCHAR(32))\n" +
                "\tleft join core_order o on h.user_id = o.buyer_id", stmt.toLowerCaseString());
    }
}
