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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class PGCreateTableTest_4 extends PGTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE products (" + //
                     "    product_no integer," + //
                     "    name text," + //
                     "    price numeric CHECK (price > 0)" + //
                     ");";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals("CREATE TABLE products ("
                + "\n\tproduct_no integer,"
                + "\n\tname text,"
                + "\n\tprice numeric"
                + "\n\t\tCHECK (price > 0)"
                + "\n);", SQLUtils.toPGString(stmt));
        
        Assert.assertEquals("create table products ("
                + "\n\tproduct_no integer,"
                + "\n\tname text,"
                + "\n\tprice numeric"
                + "\n\t\tcheck (price > 0)"
                + "\n);", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("products")));

        Assert.assertTrue(visitor.getTables().get(new TableStat.Name("products")).getCreateCount() == 1);

        Assert.assertTrue(visitor.getColumns().size() == 3);
    }

}
