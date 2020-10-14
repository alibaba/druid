/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;


public class MySqlSelectTest_271 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT `EXTRACT` AS id, `OVER` AS order_id, `DECIMAL` AS user_id, `INDEX` AS car_id, `ADD` AS plate_number\n" +
                "\t, `CURSOR` AS from_op_point_id, `INSERT` AS to_op_point_id, `INNER` AS amount, `REFERENCES` AS status, `TIMESTAMP` AS distance\n" +
                "\t, `KILL` AS start_energy, `FOREIGN` AS finish_energy, `ROW` AS duration, `JOIN` AS from_op_point_gh, `DESCRIBE` AS to_op_point_gh\n" +
                "\t, `SEPARATOR` AS create_yyyymmddhh, `DISTINCT` AS reserve_yyyymmddhh, `OUTER` AS cancel_yyyymmddhh, `SESSION` AS start_yyyymmddhh, `CONSTRAINT` AS finish_yyyymmddhh\n" +
                "\t, `PROPERTIES` AS create_date, `ORDER` AS reserve_date, `AND` AS cancel_date, `XOR` AS start_date, `LIKE` AS finish_date\n" +
                "\t, `UNBOUNDED` AS create_month, `STORED` AS reserve_month, `CASE` AS cancel_month, `LANGUAGE` AS start_month, `VALUES` AS finish_month\n" +
                "\t, `AGAINST` AS create_hour, `TOP` AS reserve_hour, `REPEAT` AS cancel_hour, `LIMIT` AS start_hour, `EXISTS` AS finish_hour\n" +
                "\t, `DESC` AS create_weekday, `LOOP` AS reserve_weekday, `ASC` AS cancel_weekday, `IF` AS start_weekday, `NOT` AS finish_weekday\n" +
                "\t, `TRUNCATE` AS create_cycletype, `SET` AS reserve_cycletype, `THEN` AS cancel_cycletype, `TABLE` AS start_cycletype, `MERGE` AS finish_cycletype\n" +
                "\t, `END` AS amount_cat, `ADDONINDEX` AS distance_cat, `SELECT` AS duration_cat, `FULL` AS car_model, `FOLLOWING` AS group_id\n" +
                "FROM `STATUS`\n" +
                "WHERE `FOLLOWING` = 'B0C2QBCOP9'\n" +
                "ORDER BY 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50\n" +
                "LIMIT 100";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT `EXTRACT` AS id, `OVER` AS order_id, `DECIMAL` AS user_id, `INDEX` AS car_id, `ADD` AS plate_number\n" +
                "\t, `CURSOR` AS from_op_point_id, `INSERT` AS to_op_point_id, `INNER` AS amount, `REFERENCES` AS status, `TIMESTAMP` AS distance\n" +
                "\t, `KILL` AS start_energy, `FOREIGN` AS finish_energy, `ROW` AS duration, `JOIN` AS from_op_point_gh, `DESCRIBE` AS to_op_point_gh\n" +
                "\t, `SEPARATOR` AS create_yyyymmddhh, `DISTINCT` AS reserve_yyyymmddhh, `OUTER` AS cancel_yyyymmddhh, `SESSION` AS start_yyyymmddhh, `CONSTRAINT` AS finish_yyyymmddhh\n" +
                "\t, `PROPERTIES` AS create_date, `ORDER` AS reserve_date, `AND` AS cancel_date, `XOR` AS start_date, `LIKE` AS finish_date\n" +
                "\t, `UNBOUNDED` AS create_month, `STORED` AS reserve_month, `CASE` AS cancel_month, `LANGUAGE` AS start_month, `VALUES` AS finish_month\n" +
                "\t, `AGAINST` AS create_hour, `TOP` AS reserve_hour, `REPEAT` AS cancel_hour, `LIMIT` AS start_hour, `EXISTS` AS finish_hour\n" +
                "\t, `DESC` AS create_weekday, `LOOP` AS reserve_weekday, `ASC` AS cancel_weekday, `IF` AS start_weekday, `NOT` AS finish_weekday\n" +
                "\t, `TRUNCATE` AS create_cycletype, `SET` AS reserve_cycletype, `THEN` AS cancel_cycletype, `TABLE` AS start_cycletype, `MERGE` AS finish_cycletype\n" +
                "\t, `END` AS amount_cat, `ADDONINDEX` AS distance_cat, `SELECT` AS duration_cat, `FULL` AS car_model, `FOLLOWING` AS group_id\n" +
                "FROM `STATUS`\n" +
                "WHERE `FOLLOWING` = 'B0C2QBCOP9'\n" +
                "ORDER BY 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50\n" +
                "LIMIT 100", stmt.toString());
    }



}