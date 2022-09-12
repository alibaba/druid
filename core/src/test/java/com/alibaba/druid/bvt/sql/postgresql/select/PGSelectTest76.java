package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest76 extends TestCase {
    public void test_0() throws Exception {
        String sql = "WITH new_values (host, gmt_last_report) AS (   VALUES('127.0.0.1', now())  ),   upsert AS (   UPDATE dataphin.od_host m   SET gmt_last_report = nv.gmt_last_report   FROM new_values nv   WHERE m.host = nv.host   RETURNING m.*  ) INSERT INTO dataphin.od_host (host, gmt_last_report) SELECT host, gmt_last_report FROM new_values WHERE NOT EXISTS (  SELECT 1  FROM upsert up  WHERE up.host = new_values.host )";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        System.out.println(stmt);

        assertEquals("WITH new_values (host, gmt_last_report) AS (\n" +
                "\t\tVALUES ('127.0.0.1', now())\n" +
                "\t), \n" +
                "\tupsert AS (\n" +
                "\t\tUPDATE dataphin.od_host m\n" +
                "\t\tSET gmt_last_report = nv.gmt_last_report\n" +
                "\t\tFROM new_values nv\n" +
                "\t\tWHERE m.host = nv.host\n" +
                "\t\tRETURNING m.*\n" +
                "\t)\n" +
                "INSERT INTO dataphin.od_host (host, gmt_last_report)\n" +
                "SELECT host, gmt_last_report\n" +
                "FROM new_values\n" +
                "WHERE NOT EXISTS (\n" +
                "\tSELECT 1\n" +
                "\tFROM upsert up\n" +
                "\tWHERE up.host = new_values.host\n" +
                ")", stmt.toString());

        assertEquals("with new_values (host, gmt_last_report) as (\n" +
                "\t\tvalues ('127.0.0.1', now())\n" +
                "\t), \n" +
                "\tupsert as (\n" +
                "\t\tupdate dataphin.od_host m\n" +
                "\t\tset gmt_last_report = nv.gmt_last_report\n" +
                "\t\tfrom new_values nv\n" +
                "\t\twhere m.host = nv.host\n" +
                "\t\treturning m.*\n" +
                "\t)\n" +
                "insert into dataphin.od_host (host, gmt_last_report)\n" +
                "select host, gmt_last_report\n" +
                "from new_values\n" +
                "where not exists (\n" +
                "\tselect 1\n" +
                "\tfrom upsert up\n" +
                "\twhere up.host = new_values.host\n" +
                ")", stmt.toLowerCaseString());
    }
}
