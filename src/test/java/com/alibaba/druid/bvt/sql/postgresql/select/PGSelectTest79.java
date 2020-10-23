package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest79 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT count(*) FROM phoenix_task_inst where dag_inst_id = 302537384 AND cycle_time >= '2018-08-14 09:00:00'\n" +
                "AND  cycle_time < '2018-08-14 10:00:00'\n" +
                "\n" +
                "-- select  count(*)  from phoenix_task_relation WHERE child_dag_inst_id = 302537384\n" +
                "\n" +
                "-- select dag_inst_id,gmtdate,flow_id,dag_type,root_node_id,app_id,create_time from \"phoenix_dag_inst\" where project_env = 'PROD' AND dag_type = 0 AND root_node_id=1  order by gmtdate DESC limit 10;";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        System.out.println(stmt);

        assertEquals("SELECT count(*)\n" +
                "FROM phoenix_task_inst\n" +
                "WHERE dag_inst_id = 302537384\n" +
                "\tAND cycle_time >= '2018-08-14 09:00:00'\n" +
                "\tAND cycle_time < '2018-08-14 10:00:00' -- select  count(*)  from phoenix_task_relation WHERE child_dag_inst_id = 302537384\n" +
                "-- select dag_inst_id,gmtdate,flow_id,dag_type,root_node_id,app_id,create_time from \"phoenix_dag_inst\" where project_env = 'PROD' AND dag_type = 0 AND root_node_id=1  order by gmtdate DESC limit 10;", stmt.toString());

        assertEquals("select count(*)\n" +
                "from phoenix_task_inst\n" +
                "where dag_inst_id = 302537384\n" +
                "\tand cycle_time >= '2018-08-14 09:00:00'\n" +
                "\tand cycle_time < '2018-08-14 10:00:00' -- select  count(*)  from phoenix_task_relation WHERE child_dag_inst_id = 302537384\n" +
                "-- select dag_inst_id,gmtdate,flow_id,dag_type,root_node_id,app_id,create_time from \"phoenix_dag_inst\" where project_env = 'PROD' AND dag_type = 0 AND root_node_id=1  order by gmtdate DESC limit 10;", stmt.toLowerCaseString());
    }
}
