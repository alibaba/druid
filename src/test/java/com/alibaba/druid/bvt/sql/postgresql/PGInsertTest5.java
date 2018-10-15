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
package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class PGInsertTest5 extends PGTest {

    public void test_0() throws Exception {
        String sql = "WITH upd AS (" + //
                     "  UPDATE employees SET sales_count = sales_count + 1 WHERE id =" + //
                     "    (SELECT sales_person FROM accounts WHERE name = 'Acme Corporation')" + //
                     "    RETURNING *" + //
                     ")" + //
                     "INSERT INTO employees_log SELECT *, current_timestamp FROM upd;";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees_log")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("accounts")));

        Assert.assertEquals(4, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "sales_count")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("accounts", "sales_person")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("accounts", "name")));
    }

}
