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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class MySqlSelectTest_group_concat extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT GROUP_CONCAT(ext_customer_id Separator '.') " //
                     + "from ad_texts " //
                     + "where customer_id=13001 " //
                     + "and description1 like '%爱丽%' " //
                     + "order by id asc;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
        Assert.assertNotNull(queryBlock.getOrderBy());

        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());
        Assert.assertEquals(1, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ad_texts")));

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT GROUP_CONCAT(ext_customer_id SEPARATOR '.')" //
                            + "\nFROM ad_texts" //
                            + "\nWHERE customer_id = 13001" //
                            + "\n\tAND description1 LIKE '%爱丽%'" //
                            + "\nORDER BY id ASC", //
                            output);
    }
}
