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

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTypeTest5 extends OracleTest {

    public void test_types() throws Exception {
        String sql = "CREATE OR REPLACE TYPE T_PMINFO_LOAN_OBJECT                                                                               \n" +
                "is object(\n" +
                " prj_id number(10), --椤圭洰 Id\n" +
                " prj_code varchar2(200), --椤圭洰 缂栧彿\n" +
                " prj_name varchar2(200), --椤圭洰 鍚嶇О\n" +
                " forecast_our_money number, --棰勮\uE178鎴戝徃閲戦\uE582\n" +
                " cust_id number(10), --瀹㈡埛 Id\n" +
                " cust_name varchar2(256), --瀹㈡埛 鍚嶇О\n" +
                " carriert_type varchar2(20), --杩愯惀鍟嗙被鍨�\n" +
                " dept_id number(10) --閮ㄩ棬\n" +
                ") not final instantiable;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE TYPE T_PMINFO_LOAN_OBJECT AS OBJECT (\n" +
                        "\tprj_id number(10), \n" +
                        "\tprj_code varchar2(200), \n" +
                        "\tprj_name varchar2(200), \n" +
                        "\tforecast_our_money number, \n" +
                        "\tcust_id number(10), \n" +
                        "\tcust_name varchar2(256), \n" +
                        "\tcarriert_type varchar2(20), \n" +
                        "\tdept_id number(10)\n" +
                        ") NOT FINAL INSTANTIABLE;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        assertEquals(0, visitor.getColumns().size());

//        assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "order_total")));
    }
}
