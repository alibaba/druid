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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlSelectTest_20 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select bsvariety, max(bsh) as bsh, min(bsl) as bsl "
                + " from   exchange_market_info "
                + " where bsdate>date_sub(now(),interval 1 day)"
                + " group by bsvariety desc;";

        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertTrue(visitor.getColumns().contains(new Column("exchange_market_info", "bsvariety")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("exchange_market_info", "bsh")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("exchange_market_info", "bsl")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("exchange_market_info", "bsdate")));
        
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());
        
        String output = SQLUtils.toMySqlString(stmt);
        
        Assert.assertEquals("SELECT bsvariety, max(bsh) AS bsh, min(bsl) AS bsl\n" +
                "FROM exchange_market_info\n" +
                "WHERE bsdate > date_sub(now(), INTERVAL 1 DAY)\n" +
                "GROUP BY bsvariety DESC;", output);
    }
}
