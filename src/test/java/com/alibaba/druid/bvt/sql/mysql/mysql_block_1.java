/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class mysql_block_1 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "BEGIN;\n" +
				"    DELETE t0 FROM ktv_tmp_sqlarea t0 WHERE t0.dbid=?;\n" +
				"    INSERT INTO ktv_tmp_sqlarea(`dbid`,`sql_id`,`parsing_schema_name`,`sql_fulltext`,`cpu_time`,`buffer_gets`,`executions`,`command_name`,`sharable_mem`,`persiste\n" +
				"nt_mem`,`users_opening`,`fetches`,`loads`,`disk_reads`,`direct_writes`,`command_type`,`plan_hash_value`,`action`,`remote`,`is_obsolete`,`physical_read_requests`,`\n" +
				"physical_write_requests`,`elapsed_time`,`user_io_wait_time`,`collection_time`)\n" +
				"    SELECT `dbid`,`sql_id`,`parsing_schema_name`,`sql_fulltext`,sum(`cpu_time`),sum(`buffer_gets`),sum(`executions`),max(`command_name`),sum(`sharable_mem`),sum(`\n" +
				"persistent_mem`),sum(`users_opening`),sum(`fetches`),sum(`loads`),sum(`disk_reads`),sum(`direct_writes`),max(`command_type`),max(`plan_hash_value`),max(`action`),\n" +
				"max(`remote`),max(`is_obsolete`),sum(`physical_read_requests`),sum(`physical_write_requests`),sum(`elapsed_time`),sum(`user_io_wait_time`),max(`collection_time`)\n" +
				"    FROM ktv_sqlarea WHERE dbid=? GROUP BY sql_fulltext;\n" +
				"    DELETE FROM ktv_sqlarea WHERE dbid=?;\n" +
				"    INSERT INTO ktv_sqlarea(`dbid`,`sql_id`,`parsing_schema_name`,`sql_fulltext`,`cpu_time`,`buffer_gets`,`executions`,`command_name`,`sharable_mem`,`persistent_m\n" +
				"em`,`users_opening`,`fetches`,`loads`,`disk_reads`,`direct_writes`,`command_type`,`plan_hash_value`,`action`,`remote`,`is_obsolete`,`physical_read_requests`,`phys\n" +
				"ical_write_requests`,`elapsed_time`,`user_io_wait_time`,`collection_time`)\n" +
				"    SELECT `dbid`,`sql_id`,`parsing_schema_name`,`sql_fulltext`,`cpu_time`,`buffer_gets`,`executions`,`command_name`,`sharable_mem`,`persistent_mem`,`users_openin\n" +
				"g`,`fetches`,`loads`,`disk_reads`,`direct_writes`,`command_type`,`plan_hash_value`,`action`,`remote`,`is_obsolete`,`physical_read_requests`,`physical_write_reques\n" +
				"ts`,`elapsed_time`,`user_io_wait_time`,`collection_time`\n" +
				"    FROM ktv_tmp_sqlarea WHERE dbid=? and sql_fulltext is not null;\n" +
				"    COMMIT;\n" +
				"    DELETE FROM ktv_tmp_sqlarea WHERE dbid=?;"; //

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
		assertEquals(2, statementList.size());
		SQLStatement stmt = statementList.get(0);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
        }

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());

//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp_name")));

//        Assert.assertEquals(7, visitor.getColumns().size());
//        Assert.assertEquals(3, visitor.getConditions().size());
//        Assert.assertEquals(1, visitor.getRelationships().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));

		{
			String output = SQLUtils.toMySqlString(stmt);
			assertEquals("BEGIN;\n" +
							"DELETE t0\n" +
							"FROM ktv_tmp_sqlarea t0\n" +
							"WHERE t0.dbid = ?;\n" +
							"INSERT INTO ktv_tmp_sqlarea (`dbid`, `sql_id`, `parsing_schema_name`, `sql_fulltext`, `cpu_time`\n" +
							"\t, `buffer_gets`, `executions`, `command_name`, `sharable_mem`, `persiste\n" +
							"nt_mem`\n" +
							"\t, `users_opening`, `fetches`, `loads`, `disk_reads`, `direct_writes`\n" +
							"\t, `command_type`, `plan_hash_value`, `action`, `remote`, `is_obsolete`\n" +
							"\t, `physical_read_requests`, `\n" +
							"physical_write_requests`, `elapsed_time`, `user_io_wait_time`, `collection_time`)\n" +
							"SELECT `dbid`, `sql_id`, `parsing_schema_name`, `sql_fulltext`\n" +
							"\t, SUM(`cpu_time`), SUM(`buffer_gets`)\n" +
							"\t, SUM(`executions`), MAX(`command_name`)\n" +
							"\t, SUM(`sharable_mem`), SUM(`\n" +
							"persistent_mem`)\n" +
							"\t, SUM(`users_opening`), SUM(`fetches`)\n" +
							"\t, SUM(`loads`), SUM(`disk_reads`)\n" +
							"\t, SUM(`direct_writes`), MAX(`command_type`)\n" +
							"\t, MAX(`plan_hash_value`), MAX(`action`)\n" +
							"\t, MAX(`remote`), MAX(`is_obsolete`)\n" +
							"\t, SUM(`physical_read_requests`), SUM(`physical_write_requests`)\n" +
							"\t, SUM(`elapsed_time`), SUM(`user_io_wait_time`)\n" +
							"\t, MAX(`collection_time`)\n" +
							"FROM ktv_sqlarea\n" +
							"WHERE dbid = ?\n" +
							"GROUP BY sql_fulltext;\n" +
							"DELETE FROM ktv_sqlarea\n" +
							"WHERE dbid = ?;\n" +
							"INSERT INTO ktv_sqlarea (`dbid`, `sql_id`, `parsing_schema_name`, `sql_fulltext`, `cpu_time`\n" +
							"\t, `buffer_gets`, `executions`, `command_name`, `sharable_mem`, `persistent_m\n" +
							"em`\n" +
							"\t, `users_opening`, `fetches`, `loads`, `disk_reads`, `direct_writes`\n" +
							"\t, `command_type`, `plan_hash_value`, `action`, `remote`, `is_obsolete`\n" +
							"\t, `physical_read_requests`, `phys\n" +
							"ical_write_requests`, `elapsed_time`, `user_io_wait_time`, `collection_time`)\n" +
							"SELECT `dbid`, `sql_id`, `parsing_schema_name`, `sql_fulltext`, `cpu_time`\n" +
							"\t, `buffer_gets`, `executions`, `command_name`, `sharable_mem`, `persistent_mem`\n" +
							"\t, `users_openin\n" +
							"g`, `fetches`, `loads`, `disk_reads`, `direct_writes`\n" +
							"\t, `command_type`, `plan_hash_value`, `action`, `remote`, `is_obsolete`\n" +
							"\t, `physical_read_requests`, `physical_write_reques\n" +
							"ts`, `elapsed_time`, `user_io_wait_time`, `collection_time`\n" +
							"FROM ktv_tmp_sqlarea\n" +
							"WHERE dbid = ?\n" +
							"\tAND sql_fulltext IS NOT NULL;\n" +
							"COMMIT;", //
					output);
		}
		{
			String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("begin;\n" +
							"delete t0\n" +
							"from ktv_tmp_sqlarea t0\n" +
							"where t0.dbid = ?;\n" +
							"insert into ktv_tmp_sqlarea (`dbid`, `sql_id`, `parsing_schema_name`, `sql_fulltext`, `cpu_time`\n" +
							"\t, `buffer_gets`, `executions`, `command_name`, `sharable_mem`, `persiste\n" +
							"nt_mem`\n" +
							"\t, `users_opening`, `fetches`, `loads`, `disk_reads`, `direct_writes`\n" +
							"\t, `command_type`, `plan_hash_value`, `action`, `remote`, `is_obsolete`\n" +
							"\t, `physical_read_requests`, `\n" +
							"physical_write_requests`, `elapsed_time`, `user_io_wait_time`, `collection_time`)\n" +
							"select `dbid`, `sql_id`, `parsing_schema_name`, `sql_fulltext`\n" +
							"\t, sum(`cpu_time`), sum(`buffer_gets`)\n" +
							"\t, sum(`executions`), max(`command_name`)\n" +
							"\t, sum(`sharable_mem`), sum(`\n" +
							"persistent_mem`)\n" +
							"\t, sum(`users_opening`), sum(`fetches`)\n" +
							"\t, sum(`loads`), sum(`disk_reads`)\n" +
							"\t, sum(`direct_writes`), max(`command_type`)\n" +
							"\t, max(`plan_hash_value`), max(`action`)\n" +
							"\t, max(`remote`), max(`is_obsolete`)\n" +
							"\t, sum(`physical_read_requests`), sum(`physical_write_requests`)\n" +
							"\t, sum(`elapsed_time`), sum(`user_io_wait_time`)\n" +
							"\t, max(`collection_time`)\n" +
							"from ktv_sqlarea\n" +
							"where dbid = ?\n" +
							"group by sql_fulltext;\n" +
							"delete from ktv_sqlarea\n" +
							"where dbid = ?;\n" +
							"insert into ktv_sqlarea (`dbid`, `sql_id`, `parsing_schema_name`, `sql_fulltext`, `cpu_time`\n" +
							"\t, `buffer_gets`, `executions`, `command_name`, `sharable_mem`, `persistent_m\n" +
							"em`\n" +
							"\t, `users_opening`, `fetches`, `loads`, `disk_reads`, `direct_writes`\n" +
							"\t, `command_type`, `plan_hash_value`, `action`, `remote`, `is_obsolete`\n" +
							"\t, `physical_read_requests`, `phys\n" +
							"ical_write_requests`, `elapsed_time`, `user_io_wait_time`, `collection_time`)\n" +
							"select `dbid`, `sql_id`, `parsing_schema_name`, `sql_fulltext`, `cpu_time`\n" +
							"\t, `buffer_gets`, `executions`, `command_name`, `sharable_mem`, `persistent_mem`\n" +
							"\t, `users_openin\n" +
							"g`, `fetches`, `loads`, `disk_reads`, `direct_writes`\n" +
							"\t, `command_type`, `plan_hash_value`, `action`, `remote`, `is_obsolete`\n" +
							"\t, `physical_read_requests`, `physical_write_reques\n" +
							"ts`, `elapsed_time`, `user_io_wait_time`, `collection_time`\n" +
							"from ktv_tmp_sqlarea\n" +
							"where dbid = ?\n" +
							"\tand sql_fulltext is not null;\n" +
							"commit;", //
					output);
		}
	}
}
