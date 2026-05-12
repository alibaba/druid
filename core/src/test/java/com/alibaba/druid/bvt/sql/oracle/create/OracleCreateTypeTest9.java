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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OracleCreateTypeTest9 extends OracleTest {
    public void test_types() throws Exception {
        String sql = "CREATE OR REPLACE TYPE \"T_EVA_PM_INFO_OBJECT\"                    \n" +
                "under ecc_pm.t_pminfo_loan_object(\n" +
                " marster_dept_id number,\n" +
                " marster_dept_name varchar2(500), --鎵�灞炲姙浜嬪\uE629\n" +
                " area_id number,\n" +
                " area_name varchar2(500),  --鎵�灞炲浗瀹舵垨鍦板尯\n" +
                " cust2_id2 number(10), --鏈�缁堝\uE179鎴� Id\n" +
                " cust2_name2 varchar2(256), --鏈�缁堝\uE179鎴� 鍚嶇О\n" +
                " finalUsage2 varchar2(256),   --鏈�缁堢敤閫�\n" +
                " sanctionedParty12 varchar2(256), --椤圭洰瀹㈡埛鍙楀埗瑁佷富浣�\n" +
                " sanctionedParty22  varchar2(256)  --鏈�缁堝\uE179鎴峰彈鍒惰\uE5C6涓讳綋\n" +
                " );";

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE TYPE \"T_EVA_PM_INFO_OBJECT\" UNDER ecc_pm.t_pminfo_loan_object (\n" +
                        "\tmarster_dept_id number, \n" +
                        "\tmarster_dept_name varchar2(500), \n" +
                        "\tarea_id number, \n" +
                        "\tarea_name varchar2(500), \n" +
                        "\tcust2_id2 number(10), \n" +
                        "\tcust2_name2 varchar2(256), \n" +
                        "\tfinalUsage2 varchar2(256), \n" +
                        "\tsanctionedParty12 varchar2(256), \n" +
                        "\tsanctionedParty22 varchar2(256)\n" +
                        ");",
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
