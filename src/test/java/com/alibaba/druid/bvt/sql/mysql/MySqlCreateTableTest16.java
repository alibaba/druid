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

public class MySqlCreateTableTest16 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `xxx` (" + //
                     "                `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID'," + //
                     "                `create_date` datetime DEFAULT NULL," + //
                     "                `update_date` datetime DEFAULT NULL," + //
                     "                `product_id` int(11) NOT NULL COMMENT '产品ID'," + //
                     "                `memeber_id` int(11) NOT NULL COMMENT '用户ID'," + //
                     "                `name` varchar(50) DEFAULT NULL COMMENT '姓名'," + //
                     "                `address` varchar(500) DEFAULT NULL COMMENT '地址'," + //
                     "                `mobile` varchar(50) DEFAULT NULL COMMENT '手机'," + //
                     "                `amount` int(11) DEFAULT NULL COMMENT '兑换数量'," + //
                     "                PRIMARY KEY (`id`)" + //
                     "              ) ENGINE=InnoDB DEFAULT CHARSET=gbk;" + //
                     "";

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
        Assert.assertEquals(9, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("xxx")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("xxx", "id")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("xxx", "amount")));
    }
}
