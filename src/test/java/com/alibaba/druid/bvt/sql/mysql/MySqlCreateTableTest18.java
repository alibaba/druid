/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlCreateTableTest18 extends MysqlTest {

	public void test_0() throws Exception {
		String sql = "CREATE TABLE IF NOT EXISTS `t_awards` (" + //
				"  `id` int(11) NOT NULL AUTO_INCREMENT," + //
				"  `seller_id` int(11) NOT NULL," + //
				"  `shop_id` int(11) DEFAULT NULL," + //
				"  `mode` int(4) NOT NULL," + //
				"  `draw_rate_1` int(11) NOT NULL," + //
				"  `draw_rate_2` int(11) NOT NULL," + //
				"  `amount` int(11) NOT NULL," + //
				"  `position_code` int(11) NOT NULL," + //
				"  `f_denomination` int(11) DEFAULT NULL," + //
				"  `f_description` text," + //
				"  `f_url` text," + //
				"  `f_type` int(4) DEFAULT NULL," + //
				"  PRIMARY KEY (`id`)," + //
				"  UNIQUE KEY `id` (`id`)" + //
				") ENGINE=InnoDB  DEFAULT CHARSET=gbk" + //
				";";

		MySqlStatementParser parser = new MySqlStatementParser(sql);
		List<SQLStatement> statementList = parser.parseStatementList();
		SQLStatement statemen = statementList.get(0);
		print(statementList);

		Assert.assertEquals(1, statementList.size());

		MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
		statemen.accept(visitor);

		System.out.println("Tables : " + visitor.getTables());
		System.out.println("fields : " + visitor.getColumns());
		System.out.println("coditions : " + visitor.getConditions());
		System.out.println("orderBy : " + visitor.getOrderByColumns());

		Assert.assertEquals(1, visitor.getTables().size());
		Assert.assertEquals(12, visitor.getColumns().size());
		Assert.assertEquals(0, visitor.getConditions().size());

		Assert.assertTrue(visitor.getTables().containsKey(
				new TableStat.Name("t_awards")));

		Assert.assertTrue(visitor.getColumns().contains(new Column("t_awards", "f_type")));
	}
}
