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
package com.alibaba.druid.bvt.sql.mysql.select;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class MySqlSelectTest_crossjoin extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select resource0_.resource_id as resource1_13_, resource0_.asMenu as asMenu13_, resource0_.resource_description as resource3_13_, resource0_.menu_name as menu4_13_, resource0_.resource_name as resource5_13_, resource0_.rg_id as rg7_13_, resource0_.rt_id as rt8_13_, resource0_.resource_serial as resource6_13_ from hnisitc.hnisitc_resource resource0_ cross join hnisitc.hnisitc_resource_type resourcety1_ where resource0_.rt_id=resourcety1_.rt_id and resourcety1_.rt_name='METHOD'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);
        
        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        
        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
        Assert.assertNull(queryBlock.getOrderBy());
        
//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
//        
        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(10, visitor.getColumns().size());
        Assert.assertEquals(3, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());

//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("mytable")));
    }
}
