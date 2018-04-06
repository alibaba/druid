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
package com.alibaba.druid.bvt.sql.mysql.createTable;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlCreateTableTest11 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `tmall_campaign` (" + //
                     "`id` int(11) NOT NULL AUTO_INCREMENT," + //
                     "`campaign_name` varchar(200) NOT NULL COMMENT '活动名称'," + //
                     "`create_date` datetime DEFAULT NULL COMMENT '活动创建时间'," + //
                     "`delete_flag` int(11) DEFAULT '0' COMMENT '活动删除标识'," + //
                     "PRIMARY KEY (`id`)" + //
                     ") ENGINE=InnoDB DEFAULT CHARSET=gbk;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);
//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("tmall_campaign")));

        Assert.assertTrue(visitor.containsColumn("tmall_campaign", "id"));
        Assert.assertTrue(visitor.containsColumn("tmall_campaign", "campaign_name"));
        Assert.assertTrue(visitor.containsColumn("tmall_campaign", "create_date"));
        Assert.assertTrue(visitor.containsColumn("tmall_campaign", "delete_flag"));
    }
}
