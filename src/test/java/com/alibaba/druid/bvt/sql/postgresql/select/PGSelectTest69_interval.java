/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest69_interval extends TestCase {
    private final DbType dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "SELECT\n" +
                "\n" +
                "        job_id,\n" +
                "        task_id,\n" +
                "        process_id,\n" +
                "        job_type,\n" +
                "        job_status,\n" +
                "        execute_server,\n" +
                "        execute_result,\n" +
                "        execute_times,\n" +
                "        execute_begin,\n" +
                "        execute_end,\n" +
                "        timeout_advice,\n" +
                "        create_time,\n" +
                "        update_time,\n" +
                "        opr_user,\n" +
                "        opr_time\n" +
                "\n" +
                "        FROM robot_job j\n" +
                "\n" +
                "        WHERE j.job_status = 1 AND j.timeout_advice = 0\n" +
                "        AND ( j.execute_begin <= NOW()-cast( ? as interval)*60)";
        System.out.println(sql);

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT job_id, task_id, process_id, job_type, job_status\n" +
                "\t, execute_server, execute_result, execute_times, execute_begin, execute_end\n" +
                "\t, timeout_advice, create_time, update_time, opr_user, opr_time\n" +
                "FROM robot_job j\n" +
                "WHERE j.job_status = 1\n" +
                "\tAND j.timeout_advice = 0\n" +
                "\tAND j.execute_begin <= NOW() - CAST(? AS interval) * 60", SQLUtils.toPGString(stmt));
        
        assertEquals("select job_id, task_id, process_id, job_type, job_status\n" +
                "\t, execute_server, execute_result, execute_times, execute_begin, execute_end\n" +
                "\t, timeout_advice, create_time, update_time, opr_user, opr_time\n" +
                "from robot_job j\n" +
                "where j.job_status = 1\n" +
                "\tand j.timeout_advice = 0\n" +
                "\tand j.execute_begin <= NOW() - cast(? as interval) * 60", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(15, visitor.getColumns().size());
        assertEquals(1, visitor.getTables().size());
    }
}
