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

public class OracleCreateTableTest22 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE promotions_var2" //
                + "    ( promo_id         NUMBER(6)"//
                + "    , promo_name       VARCHAR2(20)"//
                + "    , promo_category   VARCHAR2(15)"//
                + "    , promo_cost       NUMBER(10,2)"//
                + "    , promo_begin_date DATE"//
                + "    , promo_end_date   DATE"//
                + "    , CONSTRAINT promo_id_u UNIQUE (promo_id)"//
                + "   USING INDEX PCTFREE 20"//
                + "      TABLESPACE stocks"//
                + "      STORAGE (INITIAL 8M) );";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE promotions_var2 (\n" +
                        "\tpromo_id NUMBER(6),\n" +
                        "\tpromo_name VARCHAR2(20),\n" +
                        "\tpromo_category VARCHAR2(15),\n" +
                        "\tpromo_cost NUMBER(10, 2),\n" +
                        "\tpromo_begin_date DATE,\n" +
                        "\tpromo_end_date DATE,\n" +
                        "\tCONSTRAINT promo_id_u UNIQUE (promo_id)\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 20\n" +
                        "\t\tTABLESPACE stocks\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 8M\n" +
                        "\t\t)\n" +
                        ");",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(6, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("promotions_var2", "promo_id")));
    }
}
