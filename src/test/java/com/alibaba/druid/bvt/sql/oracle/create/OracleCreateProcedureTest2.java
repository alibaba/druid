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
package com.alibaba.druid.bvt.sql.oracle.create;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleCreateProcedureTest2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE OR REPLACE PROCEDURE transfer (" + //
                     "  from_acct  NUMBER," + //
                     "  to_acct    NUMBER," + //
                     "  amount     NUMBER" + //
                     ") AS " + //
                     "BEGIN" + //
                     "  UPDATE accounts" + //
                     "  SET balance = balance - amount" + //
                     "  WHERE account_id = from_acct;" + //
                     " " + //
                     "  UPDATE accounts" + //
                     "  SET balance = balance + amount" + //
                     "  WHERE account_id = to_acct;" + //
                     " " + //
                     "  COMMIT WRITE IMMEDIATE NOWAIT;" + //
                     "END;"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("accounts")));

        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getRelationships().size());

         Assert.assertTrue(visitor.containsColumn("accounts", "balance"));
         Assert.assertTrue(visitor.containsColumn("accounts", "account_id"));
    }
}
