package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest75 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select son_id,parent_id from ((select son_id,parent_id from lance_temp limit 10) union all (select son_id,parent_id from lance_temp limit 10)) a;";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        assertEquals("SELECT son_id, parent_id\n" +
                "FROM (\n" +
                "\t(SELECT son_id, parent_id\n" +
                "\tFROM lance_temp\n" +
                "\tLIMIT 10)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT son_id, parent_id\n" +
                "\tFROM lance_temp\n" +
                "\tLIMIT 10)\n" +
                ") a;", stmt.toString());

        assertEquals("select son_id, parent_id\n" +
                "from (\n" +
                "\t(select son_id, parent_id\n" +
                "\tfrom lance_temp\n" +
                "\tlimit 10)\n" +
                "\tunion all\n" +
                "\t(select son_id, parent_id\n" +
                "\tfrom lance_temp\n" +
                "\tlimit 10)\n" +
                ") a;", stmt.toLowerCaseString());
    }
}
