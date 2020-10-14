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

public class OracleCreateTableTest24 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE locations_demo" //
                + "    ( location_id    NUMBER(4) CONSTRAINT loc_id_pk PRIMARY KEY" //
                + "    , street_address VARCHAR2(40)" //
                + "    , postal_code    VARCHAR2(12)" //
                + "    , city           VARCHAR2(30)" //
                + "    , state_province VARCHAR2(25)" //
                + "    , country_id     CHAR(2)" //
                + "    ) ;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE locations_demo (" //
                            + "\n\tlocation_id NUMBER(4)"//
                            + "\n\t\tCONSTRAINT loc_id_pk PRIMARY KEY," //
                            + "\n\tstreet_address VARCHAR2(40)," //
                            + "\n\tpostal_code VARCHAR2(12)," //
                            + "\n\tcity VARCHAR2(30)," //
                            + "\n\tstate_province VARCHAR2(25)," //
                            + "\n\tcountry_id CHAR(2)" //
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

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("locations_demo", "location_id")));
    }
}
