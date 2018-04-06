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

public class OracleCreateTableTest7 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "create table \"ALIBABA1949\".\"SYS_JOURNAL_209051\" (" + //
                "C0 VARCHAR2(256),  opcode char(1), partno number,  rid rowid, primary key( C0 , rid )" + //
                ") organization index TABLESPACE \"APPINDX1M\"";

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

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ALIBABA1949.SYS_JOURNAL_209051")));

        Assert.assertEquals(4, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("ALIBABA1949.SYS_JOURNAL_209051", "C0"));
        Assert.assertTrue(visitor.containsColumn("ALIBABA1949.SYS_JOURNAL_209051", "opcode"));
        Assert.assertTrue(visitor.containsColumn("ALIBABA1949.SYS_JOURNAL_209051", "partno"));
        Assert.assertTrue(visitor.containsColumn("ALIBABA1949.SYS_JOURNAL_209051", "rid"));
        // Assert.assertTrue(visitor.containsColumn("pivot_table", "order_mode")));
    }
}
