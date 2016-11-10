/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateTableTest15 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "create table T (" + //
                "F1 TIMESTAMP," + //
                "F2 TIMESTAMP(9), " + //
                "F3 TIMESTAMP WITH TIME ZONE," + //
                "F4 TIMESTAMP(9) WITH TIME ZONE, " + //
                "F5 TIMESTAMP WITH TIME ZONE," + //
                "F6 TIMESTAMP(9) WITH TIME ZONE" + //
                ") ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE T (" + //
                            "\n\tF1 TIMESTAMP," + //
                            "\n\tF2 TIMESTAMP(9)," + //
                            "\n\tF3 TIMESTAMP WITH TIME ZONE," + //
                            "\n\tF4 TIMESTAMP(9) WITH TIME ZONE," + //
                            "\n\tF5 TIMESTAMP WITH TIME ZONE," + //
                            "\n\tF6 TIMESTAMP(9) WITH TIME ZONE" + //
                            "\n)", SQLUtils.toSQLString(statement, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(6, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F1")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F2")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F3")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F4")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F5")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F6")));
    }
}
