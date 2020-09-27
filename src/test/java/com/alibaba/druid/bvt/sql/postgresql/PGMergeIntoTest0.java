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
package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

public class PGMergeIntoTest0 extends PGTest {

    public void test_0() throws Exception {
        String sql = "MERGE INTO CustomerAccount CA  \n" +
                "USING (Select CustomerId, TransactionValue From RecentTransactions) AS T  \n" +
                "ON CA.CustomerId = T.CustomerId  \n" +
                "WHEN NOT MATCHED THEN  \n" +
                "  INSERT (CustomerId, Balance)  \n" +
                "  VALUES (T.CustomerId, T.TransactionValue)  \n" +
                "WHEN MATCHED THEN  \n" +
                "  UPDATE SET Balance = Balance + TransactionValue;  ";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals("MERGE INTO CustomerAccount CA\n" +
                "USING (\n" +
                "\t(SELECT CustomerId, TransactionValue\n" +
                "\tFROM RecentTransactions)\n" +
                ") T ON (CA.CustomerId = T.CustomerId) \n" +
                "WHEN MATCHED THEN UPDATE SET Balance = Balance + TransactionValue\n" +
                "WHEN NOT MATCHED THEN INSERT (CustomerId, Balance) VALUES (T.CustomerId, T.TransactionValue);", stmt.toString());

        assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

//        assertTrue(visitor.getTables().containsKey(new TableStat.Name("student")));
//
//        assertEquals(2, visitor.getColumns().size() );
//
//        assertTrue(visitor.containsColumn("student", "id"));
//        assertTrue(visitor.containsColumn("student", "grade"));
    }

    
}
