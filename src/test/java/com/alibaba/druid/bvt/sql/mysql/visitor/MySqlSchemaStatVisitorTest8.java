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
package com.alibaba.druid.bvt.sql.mysql.visitor;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class MySqlSchemaStatVisitorTest8 extends TestCase {

	public void test_0() throws Exception {
		String sql = "SELECT\n" +
				"(SELECT count() FROM warn_condition_strategy WHERE user_id = 2510701 AND is_delete = 0 AND strategy_state IN\n" +
				"(1, 2, 6)) AS monitorNum,\n" +
				"(SELECT count() FROM warn_condition_trade_history WHERE user_id = 2510701 AND date_sub(curdate(), INTERVAL 7 DAY) <= date(create_date)) AS recentEntrustNum,\n" +
				"(SELECT count() FROM warn_condition_strategy WHERE user_id = 2510701 AND (is_delete = 1 OR strategy_state IN (3, 4, 5))) AS historyNum,\n" +
				"(SELECT count() FROM warn_condition_trade_history WHERE user_id = 2510701 AND status = 1) AS unreadEntrustNum";

//		sql = "select columnName from table1 where id in (select id from table3 where name = ?)";
		MySqlStatementParser parser = new MySqlStatementParser(sql);
		List<SQLStatement> statementList = parser.parseStatementList();
		SQLStatement stmt = statementList.get(0);

		assertEquals(1, statementList.size());

		MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
		stmt.accept(visitor);

		System.out.println(stmt.toString());

//		System.out.println(sql);
		System.out.println("Tables : " + visitor.getTables());
		System.out.println("fields : " + visitor.getColumns());
		System.out.println(visitor.getConditions());

		assertEquals(2, visitor.getTables().size());
		assertEquals(true, visitor.containsTable("warn_condition_strategy"));
		assertEquals(true, visitor.containsTable("warn_condition_trade_history"));

		assertEquals(6, visitor.getColumns().size());
		assertEquals(true, visitor.containsColumn("warn_condition_strategy", "user_id"));
		assertEquals(true, visitor.containsColumn("warn_condition_strategy", "is_delete"));
		assertEquals(true, visitor.containsColumn("warn_condition_strategy", "strategy_state"));
		// assertEquals(true, visitor.getFields().contains(new
		// Column("users", "name")));

	}

}
