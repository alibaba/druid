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

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;


public class OracleSelectTest121 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "SELECT DATE_TYPE , NVL ( TIME_0 , ? ) TIME_0 , NVL ( TIME_1 , ? ) TIME_1 , NVL ( TIME_2 , ? ) TIME_2 , NVL ( TIME_3 , ? ) TIME_3 , NVL ( TIME_4 , ? ) TIME_4 , NVL ( TIME_5 , ? ) TIME_5 , NVL ( TIME_6 , ? ) TIME_6 , NVL ( TIME_7 , ? ) TIME_7 , NVL ( TIME_8 , ? ) TIME_8 , NVL ( TIME_9 , ? ) TIME_9 , NVL ( TIME_10 , ? ) TIME_10 , NVL ( TIME_11 , ? ) TIME_11 , NVL ( TIME_12 , ? ) TIME_12 , NVL ( TIME_13 , ? ) TIME_13 , NVL ( TIME_14 , ? ) TIME_14 , NVL ( TIME_15 , ? ) TIME_15 , NVL ( TIME_16 , ? ) TIME_16 , NVL ( TIME_17 , ? ) TIME_17 , NVL ( TIME_18 , ? ) TIME_18 , NVL ( TIME_19 , ? ) TIME_19 , NVL ( TIME_20 , ? ) TIME_20 , NVL ( TIME_21 , ? ) TIME_21 , NVL ( TIME_22 , ? ) TIME_22 , NVL ( TIME_23 , ? ) TIME_23 , NVL ( TIME_ALL , ? ) TIME_ALL \n" +
                "FROM ( \n" +
                "    SELECT DATE_TYPE , NVL ( HOUR , ? ) HOUR , SUM ( CNT ) CNT \n" +
                "    FROM ( \n" +
                "        SELECT ? DATE_TYPE , TO_NUMBER ( TO_CHAR ( ACCIDENT_TIME , ? ) ) HOUR , COUNT ( * ) CNT \n" +
                "        FROM PROD_AVAMS.ACCIDENT_INFO \n" +
                "        WHERE ACCIDENT_TIME BETWEEN TO_DATE ( :1 , ? ) \n" +
                "            AND TO_DATE ( :2 , ? ) \n" +
                "        GROUP BY TO_NUMBER ( TO_CHAR ( ACCIDENT_TIME , ? ) ) \n" +
                "        UNION ALL \n" +
                "        SELECT ? DATE_TYPE , TO_NUMBER ( TO_CHAR ( ACCIDENT_TIME , ? ) ) HOUR , COUNT ( * ) CNT \n" +
                "        FROM ACCIDENT_INFO \n" +
                "        WHERE ( ACCIDENT_TIME BETWEEN TO_DATE ( :3 , ? ) \n" +
                "            AND TO_DATE ( :4 , ? ) ) \n" +
                "        GROUP BY TO_NUMBER ( TO_CHAR ( ACCIDENT_TIME , ? ) ) \n" +
                "    ) T1 \n" +
                "    GROUP BY GROUPING SETS ( ( DATE_TYPE , HOUR ) , DATE_TYPE ) \n" +
                ") T2 \n" +
                "PIVOT ( MAX ( CNT ) FOR HOUR IN ( ? \"TIME_0\" , ? \"TIME_1\" , ? \"TIME_2\" , ? \"TIME_3\" , ? \"TIME_4\" , ? \"TIME_5\" , ? \"TIME_6\" , ? \"TIME_7\" , ? \"TIME_8\" , ? \"TIME_9\" , ? \"TIME_10\" , ? \"TIME_11\" , ? \"TIME_12\" , ? \"TIME_13\" , ? \"TIME_14\" , ? \"TIME_15\" , ? \"TIME_16\" , ? \"TIME_17\" , ? \"TIME_18\" , ? \"TIME_19\" , ? \"TIME_20\" , ? \"TIME_21\" , ? \"TIME_22\" , ? \"TIME_23\" , ? \"TIME_ALL\" ) \n" +
                ") \n" +
                "ORDER BY DATE_TYPE";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT DATE_TYPE, NVL(TIME_0, ?) AS TIME_0\n" +
                "\t, NVL(TIME_1, ?) AS TIME_1\n" +
                "\t, NVL(TIME_2, ?) AS TIME_2\n" +
                "\t, NVL(TIME_3, ?) AS TIME_3\n" +
                "\t, NVL(TIME_4, ?) AS TIME_4\n" +
                "\t, NVL(TIME_5, ?) AS TIME_5\n" +
                "\t, NVL(TIME_6, ?) AS TIME_6\n" +
                "\t, NVL(TIME_7, ?) AS TIME_7\n" +
                "\t, NVL(TIME_8, ?) AS TIME_8\n" +
                "\t, NVL(TIME_9, ?) AS TIME_9\n" +
                "\t, NVL(TIME_10, ?) AS TIME_10\n" +
                "\t, NVL(TIME_11, ?) AS TIME_11\n" +
                "\t, NVL(TIME_12, ?) AS TIME_12\n" +
                "\t, NVL(TIME_13, ?) AS TIME_13\n" +
                "\t, NVL(TIME_14, ?) AS TIME_14\n" +
                "\t, NVL(TIME_15, ?) AS TIME_15\n" +
                "\t, NVL(TIME_16, ?) AS TIME_16\n" +
                "\t, NVL(TIME_17, ?) AS TIME_17\n" +
                "\t, NVL(TIME_18, ?) AS TIME_18\n" +
                "\t, NVL(TIME_19, ?) AS TIME_19\n" +
                "\t, NVL(TIME_20, ?) AS TIME_20\n" +
                "\t, NVL(TIME_21, ?) AS TIME_21\n" +
                "\t, NVL(TIME_22, ?) AS TIME_22\n" +
                "\t, NVL(TIME_23, ?) AS TIME_23\n" +
                "\t, NVL(TIME_ALL, ?) AS TIME_ALL\n" +
                "FROM (\n" +
                "\tSELECT DATE_TYPE, NVL(HOUR, ?) AS HOUR\n" +
                "\t\t, SUM(CNT) AS CNT\n" +
                "\tFROM (\n" +
                "\t\tSELECT ? AS DATE_TYPE, TO_NUMBER(TO_CHAR(ACCIDENT_TIME, ?)) AS HOUR\n" +
                "\t\t\t, COUNT(*) AS CNT\n" +
                "\t\tFROM PROD_AVAMS.ACCIDENT_INFO\n" +
                "\t\tWHERE ACCIDENT_TIME BETWEEN TO_DATE(:1, ?) AND TO_DATE(:2, ?)\n" +
                "\t\tGROUP BY TO_NUMBER(TO_CHAR(ACCIDENT_TIME, ?))\n" +
                "\t\tUNION ALL\n" +
                "\t\tSELECT ? AS DATE_TYPE, TO_NUMBER(TO_CHAR(ACCIDENT_TIME, ?)) AS HOUR\n" +
                "\t\t\t, COUNT(*) AS CNT\n" +
                "\t\tFROM ACCIDENT_INFO\n" +
                "\t\tWHERE ACCIDENT_TIME BETWEEN TO_DATE(:3, ?) AND TO_DATE(:4, ?)\n" +
                "\t\tGROUP BY TO_NUMBER(TO_CHAR(ACCIDENT_TIME, ?))\n" +
                "\t) T1\n" +
                "\tGROUP BY GROUPING SETS ((DATE_TYPE, HOUR), DATE_TYPE)\n" +
                ")\n" +
                "PIVOT (MAX(CNT) FOR HOUR IN (? AS \"TIME_0\", ? AS \"TIME_1\", ? AS \"TIME_2\", ? AS \"TIME_3\", ? AS \"TIME_4\", ? AS \"TIME_5\", ? AS \"TIME_6\", ? AS \"TIME_7\", ? AS \"TIME_8\", ? AS \"TIME_9\", ? AS \"TIME_10\", ? AS \"TIME_11\", ? AS \"TIME_12\", ? AS \"TIME_13\", ? AS \"TIME_14\", ? AS \"TIME_15\", ? AS \"TIME_16\", ? AS \"TIME_17\", ? AS \"TIME_18\", ? AS \"TIME_19\", ? AS \"TIME_20\", ? AS \"TIME_21\", ? AS \"TIME_22\", ? AS \"TIME_23\", ? AS \"TIME_ALL\")) T2\n" +
                "ORDER BY DATE_TYPE", stmt.toString());
    }

}