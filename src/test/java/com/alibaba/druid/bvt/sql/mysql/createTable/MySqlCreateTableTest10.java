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

public class MySqlCreateTableTest10 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table TACCOUNT (" + //
                     "        ID varchar(36) not null," + //
                     "        ACCOUNT varchar(100) not null," + //
                     "        account_money double precision," + //
                     "        NAME varchar(100) not null," + //
                     "        TYPE integer," + //
                     "        primary key (ID)" + //
                     "    )";

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
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("TACCOUNT")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("TACCOUNT", "ID")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("TACCOUNT", "ACCOUNT")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("TACCOUNT", "account_money")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("TACCOUNT", "NAME")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("TACCOUNT", "TYPE")));
    }
}
