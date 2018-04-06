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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateTableTest23 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE promotions_var1" //
                + "    ( promo_id         NUMBER(6)" //
                + "                       CONSTRAINT promo_id_u  UNIQUE" //
                + "    , promo_name       VARCHAR2(20)" //
                + "    , promo_category   VARCHAR2(15)" //
                + "    , promo_cost       NUMBER(10,2)" //
                + "    , promo_begin_date DATE" //
                + "    , promo_end_date   DATE" //
                + "    ) ;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE promotions_var1 (" //
                            + "\n\tpromo_id NUMBER(6)" //
                            + "\n\t\tCONSTRAINT promo_id_u UNIQUE," //
                            + "\n\tpromo_name VARCHAR2(20)," //
                            + "\n\tpromo_category VARCHAR2(15)," //
                            + "\n\tpromo_cost NUMBER(10, 2)," //
                            + "\n\tpromo_begin_date DATE," //
                            + "\n\tpromo_end_date DATE" //
                            + "\n);",//
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

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("promotions_var1", "promo_id")));
    }
}
