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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

import java.util.List;

public class OracleSelectTest111 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "select EFFECTIVE_START_DATE, EFFECTIVE_END_DATE, ASSIGNMENT_NUMBER, CONTRACT, substr(EMPLOYMENT_CATEGORY, 1, 200) EMPLOYMENT_CATEGORY, substr(GRADE, 1, 200) GRADE, INTERNAL_ADDRESS, substr(JOB, 1, 200) JOB, LAST_UPDATED_BY, LAST_UPDATE_DATE, LOCATION, MANAGER, NORMAL_END_TIME, NORMAL_HOURS, NORMAL_START_TIME, substr(ORGANIZATION, 1, 200) ORGANIZATION, PAYROLL, substr(PEOPLE_GROUP, 1, 200) PEOPLE_GROUP, substr(POSITION, 1, 200) POSITION, PRIMARY, PROBATION_END_DATE, PROBATION_PERIOD, substr(PROBATION_UNITS, 1, 200) PROBATION_UNITS, substr(REASON, 1, 200) REASON, substr(RECRUITER, 1, 200) RECRUITER, RECRUITMENT_ACTIVITY, SALARY_BASIS, SPECIAL_CEILING_POINT, substr(STATUS, 1, 200) STATUS, substr(STATUTORY_INFORMATION, 1, 200) STATUTORY_INFORMATION, substr(SUPERVISOR, 1, 200) SUPERVISOR, TITLE, VACANCY, substr(WORKING_HOURS_FREQUENCY, 1, 200) WORKING_HOURS_FREQUENCY from PER_ALL_ASSIGNMENTS_D where ASSIGNMENT_ID = 26587 order by effective_start_date desc";

        OracleStatementParser parser = new OracleStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> statementList = parser.parseStatementList();
        System.out.println(statementList.toString());

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
        SQLStatement stmt = statementList.get(0);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT EFFECTIVE_START_DATE, EFFECTIVE_END_DATE, ASSIGNMENT_NUMBER, CONTRACT\n"
                         + "\t, substr(EMPLOYMENT_CATEGORY, 1, 200) AS EMPLOYMENT_CATEGORY\n"
                         + "\t, substr(GRADE, 1, 200) AS GRADE\n" + "\t, INTERNAL_ADDRESS, substr(JOB, 1, 200) AS JOB\n"
                         + "\t, LAST_UPDATED_BY, LAST_UPDATE_DATE, LOCATION, MANAGER, NORMAL_END_TIME\n"
                         + "\t, NORMAL_HOURS, NORMAL_START_TIME\n"
                         + "\t, substr(ORGANIZATION, 1, 200) AS ORGANIZATION\n"
                         + "\t, PAYROLL, substr(PEOPLE_GROUP, 1, 200) AS PEOPLE_GROUP\n"
                         + "\t, substr(POSITION, 1, 200) AS POSITION\n"
                         + "\t, PRIMARY, PROBATION_END_DATE, PROBATION_PERIOD\n"
                         + "\t, substr(PROBATION_UNITS, 1, 200) AS PROBATION_UNITS\n"
                         + "\t, substr(REASON, 1, 200) AS REASON\n" + "\t, substr(RECRUITER, 1, 200) AS RECRUITER\n"
                         + "\t, RECRUITMENT_ACTIVITY, SALARY_BASIS, SPECIAL_CEILING_POINT\n"
                         + "\t, substr(STATUS, 1, 200) AS STATUS\n"
                         + "\t, substr(STATUTORY_INFORMATION, 1, 200) AS STATUTORY_INFORMATION\n"
                         + "\t, substr(SUPERVISOR, 1, 200) AS SUPERVISOR\n" + "\t, TITLE, VACANCY\n"
                         + "\t, substr(WORKING_HOURS_FREQUENCY, 1, 200) AS WORKING_HOURS_FREQUENCY\n"
                         + "FROM PER_ALL_ASSIGNMENTS_D\n" + "WHERE ASSIGNMENT_ID = 26587\n"
                         + "ORDER BY effective_start_date DESC", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(35, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(1, visitor.getOrderByColumns().size());

    }
    public void test_1() throws Exception {
        String sql = "SELECT instance_id\n" + "FROM JTF_FM_SERVICE_MONITOR\n" + "WHERE server_id = 5000\n"
                     + "      AND health = 'A'\n" + "      AND primary = 'Y'\n"
                     + "      AND last_update_date >= (sysdate - (1 / 288))";

        OracleStatementParser parser = new OracleStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> statementList = parser.parseStatementList();
        System.out.println(statementList.toString());

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
        SQLStatement stmt = statementList.get(0);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT instance_id\n"
                         + "FROM JTF_FM_SERVICE_MONITOR\n"
                         + "WHERE server_id = 5000\n"
                         + "\tAND health = 'A'\n"
                         + "\tAND primary = 'Y'\n"
                         + "\tAND last_update_date >= SYSDATE - 1 / 288", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(4, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

    }
//
//    public void test_2() throws Exception {
//        String sql = "SELECT\n" + "  K.FPZLMC AS FPMC,\n" + "  T.MYZGGPSL,\n" + "  T.MCZGGPSL,\n" + "  T.CPZGSL,\n"
//                     + "  NVL(D.JCFS, 0),\n" + "  0,\n"
//                     + "  CASE WHEN LEAST(T.MCZGGPSL * K.MBFS, T.CPZGSL * K.MBFS - NVL(D.JCFS, 0)/*+nvl(b.yjfs,0)*/,\n"
//                     + "                  T.MYZGGPSL * K.MBFS + NVL(N.BYTPFS, 0) * K.MBFS - NVL(M.BYYMFS, 0) * K.MBFS) < 0\n"
//                     + "    THEN 0\n"
//                     + "  ELSE FLOOR((LEAST(T.MCZGGPSL * K.MBFS, T.CPZGSL * K.MBFS - NVL(D.JCFS, 0)/*+nvl(b.yjfs,0)*/,\n"
//                     + "                    T.MYZGGPSL * K.MBFS + NVL(N.BYTPFS, 0) * K.MBFS - NVL(M.BYYMFS, 0) * K.MBFS)) / K.MBFS) END,\n"
//                     + "  T.FPZL_DM,\n" + "  K.JLDWMC\n"
//                     + "FROM HX_DJ.DJ_NSRXX A INNER JOIN HX_FP.FP_PZHDXX T ON T.YXBZ = 'Y' AND A.DJXH = T.DJXH\n"
//                     + "  INNER JOIN (SELECT\n" + "                B.FPZL_DM,\n" + "                B.FPZLMC,\n"
//                     + "                C.JLDW_DM,\n" + "                K.JLDWMC,\n"
//                     + "                MIN(C.MBFS) MBFS\n"
//                     + "              FROM HX_DM_QG.DM_FP_FPZL B, HX_DM_ZDY.DM_FP_FP C, HX_DM_QG.DM_GY_JLDW K\n"
//                     + "              WHERE B.JLDW_DM = C.JLDW_DM AND B.FPZL_DM = C.FPZL_DM AND C.JLDW_DM = K.JLDW_DM AND C.YXBZ = 'Y' AND\n"
//                     + "                    B.YXBZ = 'Y' AND C.XYBZ = 'Y' AND B.XYBZ = 'Y'\n"
//                     + "              GROUP BY B.FPZL_DM, B.FPZLMC, C.JLDW_DM, K.JLDWMC) K ON T.FPZL_DM = K.FPZL_DM\n"
//                     + "  LEFT JOIN (SELECT\n" + "               D.DJXH,\n" + "               D.FPZL_DM,\n"
//                     + "               SUM(D.FS) AS JCFS\n" + "             FROM HX_FP.FP_NSRFPJC D\n"
//                     + "             WHERE D.DJXH = :B1\n"
//                     + "             GROUP BY D.DJXH, D.FPZL_DM) D ON T.DJXH = D.DJXH AND T.FPZL_DM = D.FPZL_DM\n"
//                     + "  LEFT JOIN (SELECT\n" + "               A.DJXH,\n" + "               B.FPZL_DM,\n"
//                     + "               NVL(SUM(B.FPSL), 0) AS BYYMFS\n"
//                     + "             FROM HX_FP.FP_LY A, HX_FP.FP_LY_MX B\n"
//                     + "             WHERE A.FPLYUUID = B.FPLYUUID AND A.LRRQ >= (TRUNC(ADD_MONTHS(LAST_DAY(SYSDATE), -1) + 1)) AND\n"
//                     + "                   A.LRRQ <= (TRUNC(LAST_DAY(SYSDATE)) + 1 - 1 / 86400) AND A.DJXH = :B1\n"
//                     + "             GROUP BY DJXH, FPZL_DM) M ON T.DJXH = M.DJXH AND T.FPZL_DM = M.FPZL_DM\n"
//                     + "  LEFT JOIN (SELECT\n" + "               A.DJXH,\n" + "               B.FPZL_DM,\n"
//                     + "               NVL(SUM(B.FPSL), 0) AS BYTPFS\n"
//                     + "             FROM HX_FP.FP_TP A, HX_FP.FP_TP_MX B\n"
//                     + "             WHERE A.FPTPUUID = B.FPTPUUID AND A.LRRQ >= (TRUNC(ADD_MONTHS(LAST_DAY(SYSDATE), -1) + 1)) AND\n"
//                     + "                   A.LRRQ <= (TRUNC(LAST_DAY(SYSDATE)) + 1 - 1 / 86400) AND A.DJXH = :B1\n"
//                     + "             GROUP BY DJXH, FPZL_DM) N ON T.DJXH = N.DJXH AND T.FPZL_DM = N.FPZL_DM\n"
//                     + "WHERE\n"
//                     + "  A.NSRZT_DM <= '03' AND KZZTDJLX_DM IN ('1110', '1100', '1120', '1600', '1300', '1131', '1134') AND KQCCSZTDJBZ = 'N'\n"
//                     + "  AND (A.NSRSBH = :B2 OR A.SHXYDM = :B2) AND A.YXBZ = 'Y'";
//
//        OracleStatementParser parser = new OracleStatementParser(sql, SQLParserFeature.KeepComments);
//        List<SQLStatement> statementList = parser.parseStatementList();
//        System.out.println(statementList.toString());
//
//        assertEquals(1, statementList.size());
//
//        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
//        SQLStatement stmt = statementList.get(0);
//        stmt.accept(visitor);
//
//        {
//            String text = SQLUtils.toOracleString(stmt);
//
//            assertEquals("SELECT instance_id\n"
//                         + "FROM JTF_FM_SERVICE_MONITOR\n"
//                         + "WHERE server_id = 5000\n"
//                         + "\tAND health = 'A'\n"
//                         + "\tAND primary = 'Y'\n"
//                         + "\tAND last_update_date >= SYSDATE - 1 / 288", text);
//        }
//
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
//
//        assertEquals(1, visitor.getTables().size());
//        assertEquals(35, visitor.getColumns().size());
//        assertEquals(1, visitor.getConditions().size());
//        assertEquals(0, visitor.getRelationships().size());
//        assertEquals(1, visitor.getOrderByColumns().size());
//
//    }
}
