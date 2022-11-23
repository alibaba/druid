package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest78 extends TestCase {
    public void test_0() throws Exception {
        String sql = "-- select  count(*)  from phoenix_task_relation WHERE child_dag_inst_id = 302537384\nselect 1";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        System.out.println(stmt);

        assertEquals("-- select  count(*)  from phoenix_task_relation WHERE child_dag_inst_id = 302537384\n" +
                "SELECT 1", stmt.toString());

        assertEquals("-- select  count(*)  from phoenix_task_relation WHERE child_dag_inst_id = 302537384\n" +
                "select 1", stmt.toLowerCaseString());
    }
}
