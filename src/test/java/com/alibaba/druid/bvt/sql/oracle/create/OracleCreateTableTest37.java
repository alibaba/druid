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

public class OracleCreateTableTest37 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE hash_products "
        + "    ( product_id          NUMBER(6)   PRIMARY KEY"
        + "    , product_name        VARCHAR2(50) "
        + "    , product_description VARCHAR2(2000) "
        + "    , category_id         NUMBER(2) "
        + "    , weight_class        NUMBER(1) "
        + "    , warranty_period     INTERVAL YEAR TO MONTH "
        + "    , supplier_id         NUMBER(6) "
        + "    , product_status      VARCHAR2(20) "
        + "    , list_price          NUMBER(8,2) "
        + "    , min_price           NUMBER(8,2) "
        + "    , catalog_url         VARCHAR2(50) "
        + "    , CONSTRAINT          product_status_lov_demo "
        + "                          CHECK (product_status in ('orderable' "
        + "                                                  ,'planned' "
        + "                                                  ,'under development' "
        + "                                                  ,'obsolete') "
        + ") ) "
        + " PARTITION BY HASH (product_id) "
        + " PARTITIONS 4 "
        + " STORE IN (tbs_01, tbs_02, tbs_03, tbs_04);";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE hash_products ("
                + "\n\tproduct_id NUMBER(6) PRIMARY KEY,"
                + "\n\tproduct_name VARCHAR2(50),"
                + "\n\tproduct_description VARCHAR2(2000),"
                + "\n\tcategory_id NUMBER(2),"
                + "\n\tweight_class NUMBER(1),"
                + "\n\twarranty_period INTERVAL YEAR TO MONTH,"
                + "\n\tsupplier_id NUMBER(6),"
                + "\n\tproduct_status VARCHAR2(20),"
                + "\n\tlist_price NUMBER(8, 2),"
                + "\n\tmin_price NUMBER(8, 2),"
                + "\n\tcatalog_url VARCHAR2(50),"
                + "\n\tCONSTRAINT product_status_lov_demo CHECK (product_status IN ('orderable', 'planned', 'under development', 'obsolete'))"
                + "\n)"
                + "\nPARTITION BY HASH (product_id) PARTITIONS 4"
                + "\nSTORE IN (tbs_01, tbs_02, tbs_03, tbs_04);",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(11, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("hash_products", "product_id")));
    }
}
