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
package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleDeleteTest extends OracleTest {

    public void test_0() throws Exception {
        String sql = "delete from BILLING_LOG_MONITOR log where log.guid in (" + //
                     "'wb_xinmin.zhao_test121','wb_xinmin.zhao_test122'" + //
                     ",'wb_xinmin.zhao_test123','wb_xinmin.zhao_test124'" + ")";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(1, visitor.getColumns().size());

         Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("BILLING_LOG_MONITOR")));
        // Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
        //
         Assert.assertTrue(visitor.containsColumn("BILLING_LOG_MONITOR", "guid"));
        // Assert.assertTrue(visitor.containsColumn("employees", "salary"));
        // Assert.assertTrue(visitor.containsColumn("employees", "commission_pct"));
    }

}
