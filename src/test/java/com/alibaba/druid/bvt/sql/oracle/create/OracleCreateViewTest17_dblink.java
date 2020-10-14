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
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateViewTest17_dblink extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE OR REPLACE FORCE VIEW \"OA\".\"HET_MANAGER1_FW\" (\"LX\", \"HS\") AS \n" +
                "  SELECT B.SELECTNAME LX,\n" +
                "       AVG(FUN_CAL_TIME_BETWEEN@LINK_OMSS(TO_CHAR(TO_DATE(A.RECEIVEDATE ||\n" +
                "                                                          A.RECEIVETIME,\n" +
                "                                                          'YYYY-MM-DD HH24:MI:SS'),\n" +
                "                                                  'YYYYMMDDHH24MISS'),\n" +
                "                                          TO_CHAR(TO_DATE(D.OPERATEDATE ||\n" +
                "                                                          D.OPERATETIME,\n" +
                "                                                          'YYYY-MM-DD HH24:MI:SS'),\n" +
                "                                                  'YYYYMMDDHH24MISS'))) HS\n" +
                "  FROM WORKFLOW_CURRENTOPERATOR A,\n" +
                "       WORKFLOW_SELECTITEM      B,\n" +
                "       FORMTABLE_MAIN_93        C,\n" +
                "       WORKFLOW_CURRENTOPERATOR D\n" +
                " WHERE A.WORKFLOWID = 606\n" +
                "   AND D.WORKFLOWID = 606\n" +
                "   AND A.NODEID = 2249\n" +
                "   AND D.NODEID = 2251\n" +
                "   AND B.FIELDID = 10259\n" +
                "   AND A.REQUESTID = C.REQUESTID\n" +
                "   AND D.REQUESTID = C.REQUESTID\n" +
                "   AND C.Hetonglx = B.SELECTVALUE\n" +
                "   AND C.SHENQINGRIQI >= '2017-10-01'\n" +
                "   AND C.SHENQINGRIQI <= '2017-12-31'\n" +
                " GROUP BY B.SELECTNAME"
               ;

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE VIEW \"OA\".\"HET_MANAGER1_FW\" (\n" +
                        "\t\"LX\", \n" +
                        "\t\"HS\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT B.SELECTNAME AS LX\n" +
                        "\t, AVG(FUN_CAL_TIME_BETWEEN@LINK_OMSS(TO_CHAR(TO_DATE(A.RECEIVEDATE || A.RECEIVETIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYYMMDDHH24MISS'), TO_CHAR(TO_DATE(D.OPERATEDATE || D.OPERATETIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYYMMDDHH24MISS'))) AS HS\n" +
                        "FROM WORKFLOW_CURRENTOPERATOR A, WORKFLOW_SELECTITEM B, FORMTABLE_MAIN_93 C, WORKFLOW_CURRENTOPERATOR D\n" +
                        "WHERE A.WORKFLOWID = 606\n" +
                        "\tAND D.WORKFLOWID = 606\n" +
                        "\tAND A.NODEID = 2249\n" +
                        "\tAND D.NODEID = 2251\n" +
                        "\tAND B.FIELDID = 10259\n" +
                        "\tAND A.REQUESTID = C.REQUESTID\n" +
                        "\tAND D.REQUESTID = C.REQUESTID\n" +
                        "\tAND C.Hetonglx = B.SELECTVALUE\n" +
                        "\tAND C.SHENQINGRIQI >= '2017-10-01'\n" +
                        "\tAND C.SHENQINGRIQI <= '2017-12-31'\n" +
                        "GROUP BY B.SELECTNAME",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(3, visitor.getTables().size());

        assertEquals(13, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("WORKFLOW_SELECTITEM", "SELECTNAME")));
    }
}
