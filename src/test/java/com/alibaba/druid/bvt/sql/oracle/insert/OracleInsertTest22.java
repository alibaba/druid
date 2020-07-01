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
package com.alibaba.druid.bvt.sql.oracle.insert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleInsertTest22 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO MKTG_H_EXEC_RESULT_FACT\n" +
                "(THE_DATE, AREA_ID, SCENE_ID, MKTG_CNT, MKTG_SUC_CNT\n" +
                ", TASK_CNT, TASK_F_CNT, TASK_F_SUC_CNT, CON_CNT, CON_SUC_CNT)\n" +
                "SELECT TRUNC(SYSDATE), T1.AREA_ID\n" +
                ", RTRIM(TO_CHAR(T2.PID))\n" +
                ", SUM(T1.MKTG_CNT), SUM(T1.MKTG_SUC_CNT)\n" +
                ", SUM(T1.TASK_CNT), SUM(T1.TASK_F_CNT)\n" +
                ", SUM(T1.TASK_F_SUC_CNT), SUM(T1.CON_CNT)\n" +
                ", SUM(T1.CON_SUC_CNT)\n" +
                "FROM MKTG_H_EXEC_RESULT_FACT T1, (\n" +
                "SELECT DISTINCT MKTG_PLAN_LVL1_ID AS PID, MKTG_PLAN_LVL4_ID AS SCENE_ID\n" +
                "FROM DMN_MKTG_PLAN_TYPE\n" +
                "UNION ALL\n" +
                "SELECT DISTINCT MKTG_PLAN_LVL2_ID AS PID, MKTG_PLAN_LVL4_ID AS SCENE_ID\n" +
                "FROM DMN_MKTG_PLAN_TYPE_TWO\n" +
                "WHERE MKTG_PLAN_LVL2_ID <> MKTG_PLAN_LVL4_ID\n" +
                "UNION ALL\n" +
                "SELECT DISTINCT MKTG_PLAN_LVL3_ID AS PID, MKTG_PLAN_LVL4_ID AS SCENE_ID\n" +
                "FROM DMN_MKTG_PLAN_TYPE\n" +
                "WHERE MKTG_PLAN_LVL3_ID <> MKTG_PLAN_LVL4_ID\n" +
                ") T2\n" +
                "WHERE T1.THE_DATE = TRUNC(SYSDATE)\n" +
                "AND T1.SCENE_ID = T2.SCENE_ID\n" +
                "GROUP BY T1.AREA_ID, RTRIM(TO_CHAR(T2.PID))";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("INSERT INTO MKTG_H_EXEC_RESULT_FACT\n" +
                        "\t(THE_DATE, AREA_ID, SCENE_ID, MKTG_CNT, MKTG_SUC_CNT\n" +
                        "\t, TASK_CNT, TASK_F_CNT, TASK_F_SUC_CNT, CON_CNT, CON_SUC_CNT)\n" +
                        "SELECT TRUNC(SYSDATE), T1.AREA_ID\n" +
                        "\t, RTRIM(TO_CHAR(T2.PID))\n" +
                        "\t, SUM(T1.MKTG_CNT), SUM(T1.MKTG_SUC_CNT)\n" +
                        "\t, SUM(T1.TASK_CNT), SUM(T1.TASK_F_CNT)\n" +
                        "\t, SUM(T1.TASK_F_SUC_CNT), SUM(T1.CON_CNT)\n" +
                        "\t, SUM(T1.CON_SUC_CNT)\n" +
                        "FROM MKTG_H_EXEC_RESULT_FACT T1, (\n" +
                        "\tSELECT DISTINCT MKTG_PLAN_LVL1_ID AS PID, MKTG_PLAN_LVL4_ID AS SCENE_ID\n" +
                        "\tFROM DMN_MKTG_PLAN_TYPE\n" +
                        "\tUNION ALL\n" +
                        "\tSELECT DISTINCT MKTG_PLAN_LVL2_ID AS PID, MKTG_PLAN_LVL4_ID AS SCENE_ID\n" +
                        "\tFROM DMN_MKTG_PLAN_TYPE_TWO\n" +
                        "\tWHERE MKTG_PLAN_LVL2_ID <> MKTG_PLAN_LVL4_ID\n" +
                        "\tUNION ALL\n" +
                        "\tSELECT DISTINCT MKTG_PLAN_LVL3_ID AS PID, MKTG_PLAN_LVL4_ID AS SCENE_ID\n" +
                        "\tFROM DMN_MKTG_PLAN_TYPE\n" +
                        "\tWHERE MKTG_PLAN_LVL3_ID <> MKTG_PLAN_LVL4_ID\n" +
                        ") T2\n" +
                        "WHERE T1.THE_DATE = TRUNC(SYSDATE)\n" +
                        "\tAND T1.SCENE_ID = T2.SCENE_ID\n" +
                        "GROUP BY T1.AREA_ID, RTRIM(TO_CHAR(T2.PID))",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());

        assertEquals(3, visitor.getTables().size());
        assertEquals(15, visitor.getColumns().size());

        assertTrue(visitor.containsTable("MKTG_H_EXEC_RESULT_FACT"));
        assertTrue(visitor.containsTable("DMN_MKTG_PLAN_TYPE"));
        assertTrue(visitor.containsTable("DMN_MKTG_PLAN_TYPE_TWO"));

         assertTrue(visitor.getColumns().contains(new TableStat.Column("MKTG_H_EXEC_RESULT_FACT", "THE_DATE")));
    }

}
