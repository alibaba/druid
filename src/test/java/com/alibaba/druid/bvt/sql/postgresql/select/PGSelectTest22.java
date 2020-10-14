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
package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class PGSelectTest22 extends PGTest {

    public void test_0() throws Exception {
        String sql = "SELECT i.relname, d.indisunique, a.attname"
                + "          FROM pg_class t, pg_class i, pg_index d, pg_attribute "
                + "         WHERE i.relkind = 'i'"
                + "           AND d.indexrelid = i.oid"
                + "           AND d.indisprimary = 'f'"
                + "           AND t.oid = d.indrelid"
                + "           AND t.relname = 'schema_migrations'"
                + "           AND a.attrelid = t.oid"
                + "           AND ( d.indkey[0]=a.attnum OR d.indkey[1]=a.attnum"
                + "              OR d.indkey[2]=a.attnum OR d.indkey[3]=a.attnum"
                + "              OR d.indkey[4]=a.attnum OR d.indkey[5]=a.attnum"
                + "              OR d.indkey[6]=a.attnum OR d.indkey[7]=a.attnum"
                + "              OR d.indkey[8]=a.attnum OR d.indkey[9]=a.attnum )"
                + "        ORDER BY i.relname"
                + "";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
//        print(statementList);

        Assert.assertEquals("SELECT i.relname, d.indisunique, a.attname"
                + "\nFROM pg_class t, pg_class i, pg_index d, pg_attribute"
                + "\nWHERE i.relkind = 'i'"
                + "\n\tAND d.indexrelid = i.oid"
                + "\n\tAND d.indisprimary = 'f'"
                + "\n\tAND t.oid = d.indrelid"
                + "\n\tAND t.relname = 'schema_migrations'"
                + "\n\tAND a.attrelid = t.oid"
                + "\n\tAND (d.indkey[0] = a.attnum"
                + "\n\t\tOR d.indkey[1] = a.attnum"
                + "\n\t\tOR d.indkey[2] = a.attnum"
                + "\n\t\tOR d.indkey[3] = a.attnum"
                + "\n\t\tOR d.indkey[4] = a.attnum"
                + "\n\t\tOR d.indkey[5] = a.attnum"
                + "\n\t\tOR d.indkey[6] = a.attnum"
                + "\n\t\tOR d.indkey[7] = a.attnum"
                + "\n\t\tOR d.indkey[8] = a.attnum"
                + "\n\t\tOR d.indkey[9] = a.attnum)"
                + "\nORDER BY i.relname", output(statementList));

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertEquals(13, visitor.getColumns().size());
        Assert.assertEquals(3, visitor.getTables().size());
    }
}
