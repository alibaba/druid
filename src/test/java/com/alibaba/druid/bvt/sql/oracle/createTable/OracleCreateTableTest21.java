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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateTableTest21 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE divisions " //
                + "    (div_no     NUMBER(2), " //
                + "     div_name   VARCHAR2(14), "//
                + "     location   VARCHAR2(13) ) " //
                + "     STORAGE  ( INITIAL 8M MAXSIZE 1G );";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE divisions (\n" +
                        "\tdiv_no NUMBER(2),\n" +
                        "\tdiv_name VARCHAR2(14),\n" +
                        "\tlocation VARCHAR2(13)\n" +
                        ")\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 8M\n" +
                        "\tMAXSIZE 1G\n" +
                        ");",//
                            SQLUtils.toSQLString(statement, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(3, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("divisions", "div_no")));
    }
}
