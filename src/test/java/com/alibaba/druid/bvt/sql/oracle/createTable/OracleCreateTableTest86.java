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

public class OracleCreateTableTest86 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "create table mid_users_restore_nogen_598 as\n" +
                " select/*+ordered user_hash(a b)*/ a.*,\n"
                + " nvl(b.total_amount-b.generated_amount,0) no_genrated_amount\n"
                + " from mid_users_restore_account_598 a,\n" + " subtotal_bill b\n" + " where a.home_city = 598\n"
                + " and b.home_city(+) = 598\n" + " and a.user_id = b.user_id(+)\n"
                + " and b.month(+) = to_char(sysdate,'mm') ;";

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

        Assert.assertEquals(3, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("mid_users_restore_nogen_598")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("mid_users_restore_account_598")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("subtotal_bill")));

        Assert.assertEquals(8, visitor.getColumns().size());

    }
    public void test_1() throws Exception {
        String sql = //
                "create table mid_users_restore_nogen_594 as\n" + " select/*+ordered user_hash(a b)*/ a.*,\n"
                + " nvl(b.total_amount-b.generated_amount,0) no_genrated_amount\n"
                + " from mid_users_restore_account_594 a,\n" + " subtotal_bill b\n" + " where a.home_city = 594\n"
                + " and b.home_city(+) = 594\n" + " and a.user_id = b.user_id(+)\n"
                + " and b.month(+) = to_char(sysdate,'mm') ;";

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

        Assert.assertEquals(3, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("mid_users_restore_nogen_594")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("mid_users_restore_account_594")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("subtotal_bill")));

        Assert.assertEquals(8, visitor.getColumns().size());

    }
}
