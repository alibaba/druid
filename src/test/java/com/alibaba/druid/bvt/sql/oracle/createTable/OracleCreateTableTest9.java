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
package com.alibaba.druid.bvt.sql.oracle.createTable;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;

public class OracleCreateTableTest9 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "CREATE GLOBAL TEMPORARY TABLE \"ESCROW\".\"RUPD$_HT_TASK_TRADE_HISTOR\" (" + //
                "\"ID\" NUMBER, " + //
                "dmltype$$ varchar2(1), " + //
                "snapid integer, " + //
                "change_vector$$ raw(255)" + //
                ") ON COMMIT PRESERVE ROWS";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ESCROW.RUPD$_HT_TASK_TRADE_HISTOR")));

        Assert.assertEquals(4, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("ESCROW.RUPD$_HT_TASK_TRADE_HISTOR", "ID"));
        Assert.assertTrue(visitor.containsColumn("ESCROW.RUPD$_HT_TASK_TRADE_HISTOR",
                                                                             "dmltype$$"));
        Assert.assertTrue(visitor.containsColumn("ESCROW.RUPD$_HT_TASK_TRADE_HISTOR",
                                                                             "snapid"));
        Assert.assertTrue(visitor.containsColumn("ESCROW.RUPD$_HT_TASK_TRADE_HISTOR",
                                                                             "change_vector$$"));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
