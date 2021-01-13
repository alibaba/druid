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

public class MySqlSelectTest_301_dla
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "WITH `contextual_log` AS (\n" +
                "        SELECT\n" +
                "            *,\n" +
                "            count_if(`event` = 26001) OVER(\n" +
                "                PARTITION BY `uuid`\n" +
                "                ORDER BY\n" +
                "                    `seq` ASC\n" +
                "            ) AS `op_group`,\n" +
                "            lag(`event`) OVER(\n" +
                "                PARTITION BY `uuid`\n" +
                "                ORDER BY\n" +
                "                    `seq` ASC\n" +
                "            ) AS `prev_event`\n" +
                "        FROM\n" +
                "            `attendance_log`\n" +
                "        WHERE\n" +
                "            `createdAt` >= unix_timestamp('20190828') * 1000\n" +
                "            AND `createdAt` < unix_timestamp(date_add('20190828', 1)) * 1000\n" +
                "    ),\n" +
                "    `consecutive_counted_log` AS (\n" +
                "        SELECT\n" +
                "            *,\n" +
                "            count_if(`event` = `prev_event`) OVER(\n" +
                "                PARTITION BY `uuid`,\n" +
                "                `op_group`,\n" +
                "                `event`\n" +
                "                ORDER BY\n" +
                "                    `seq` ASC\n" +
                "            ) AS `consecutive_count`\n" +
                "        FROM\n" +
                "            `contextual_log`\n" +
                "    ),\n" +
                "    `log_metrics` AS (\n" +
                "        SELECT\n" +
                "            `uuid`,\n" +
                "            `key`,\n" +
                "            regexp_substr(\n" +
                "                `key`,\n" +
                "                '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$',\n" +
                "                1\n" +
                "            ) AS `course_id`,\n" +
                "            regexp_substr(\n" +
                "                `key`,\n" +
                "                '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$',\n" +
                "                2\n" +
                "            ) AS `lesson_type`,\n" +
                "            regexp_substr(\n" +
                "                `key`,\n" +
                "                '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$',\n" +
                "                3\n" +
                "            ) AS `lesson_id`,\n" +
                "            regexp_substr(\n" +
                "                `key`,\n" +
                "                '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$',\n" +
                "                4\n" +
                "            ) AS `mode`,\n" +
                "            regexp_substr(\n" +
                "                `key`,\n" +
                "                '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$',\n" +
                "                5\n" +
                "            ) AS `section`,\n" +
                "            regexp_substr(\n" +
                "                `key`,\n" +
                "                '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$',\n" +
                "                6\n" +
                "            ) AS `level`,\n" +
                "            regexp_substr(\n" +
                "                `key`,\n" +
                "                '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$',\n" +
                "                7\n" +
                "            ) AS `question_type`,\n" +
                "            regexp_substr(\n" +
                "                `key`,\n" +
                "                '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$',\n" +
                "                8\n" +
                "            ) AS `question_id`,\n" +
                "            min(happenedAt) AS `start_at`,\n" +
                "            max(happenedAt) AS `end_at`,\n" +
                "            count_if(`event` = 11001) AS `app_pause`,\n" +
                "            count_if(`event` = 11002) AS `app_resume`,\n" +
                "            count_if(`event` = 21001) AS `lesson_start`,\n" +
                "            count_if(`event` = 21002) AS `lesson_end`,\n" +
                "            count_if(`event` = 21003) AS `lesson_quit`,\n" +
                "            count_if(`event` = 21004) AS `lesson_evaluate`,\n" +
                "            count_if(`event` = 21101) AS `part_start`,\n" +
                "            count_if(`event` = 21102) AS `part_end`,\n" +
                "            count_if(`event` = 22001) AS `section_start`,\n" +
                "            count_if(`event` = 22002) AS `section_end`,\n" +
                "            count_if(`event` = 23001) AS `level_start`,\n" +
                "            count_if(`event` = 23002) AS `level_pass`,\n" +
                "            count_if(`event` = 23003) AS `level_fail`,\n" +
                "            count_if(`event` = 24001) AS `cutscene_start`,\n" +
                "            count_if(`event` = 24002) AS `cutscene_end`,\n" +
                "            count_if(`event` = 25001) AS `question_start`,\n" +
                "            count_if(`event` = 25002) AS `question_pass`,\n" +
                "            count_if(`event` = 25003) AS `question_fail`,\n" +
                "            count_if(`event` = 25004) AS `question_pause`,\n" +
                "            count_if(`event` = 25005) AS `question_resume`,\n" +
                "            count_if(`event` = 26001) AS `operation_start`,\n" +
                "            count_if(`event` = 26002) AS `operation_end`,\n" +
                "            count_if(`event` = 26003) AS `operation_correct`,\n" +
                "            count_if(`event` = 26004) AS `operation_wrong`,\n" +
                "            max(\n" +
                "                CASE `event` WHEN 26004 THEN `consecutive_count` END\n" +
                "            ) AS `operation_wrong_longest`,\n" +
                "            count_if(`event` = 26005) AS `operation_neutral`,\n" +
                "            count_if(`event` = 26006) AS `operation_timeout`,\n" +
                "            count_if(`event` = 31001) AS `photobooth_start`,\n" +
                "            count_if(`event` = 31002) AS `photobooth_end`,\n" +
                "            count_if(`event` = 31003) AS `photobooth_shot`,\n" +
                "            count_if(`event` = 31004) AS `photobooth_skip`,\n" +
                "            count_if(`event` = 31005) AS `photobooth_save`,\n" +
                "            count_if(`event` = 31006) AS `photobooth_discard`,\n" +
                "            count_if(`event` = 41001) AS `ui_next_appear`,\n" +
                "            count_if(`event` = 41002) AS `ui_next_click`\n" +
                "        FROM\n" +
                "            `consecutive_counted_log`\n" +
                "        GROUP BY\n" +
                "            `uuid`,\n" +
                "            `key`\n" +
                "    )\n" +
                "SELECT\n" +
                "    *\n" +
                "FROM\n" +
                "    `log_metrics`\n" +
                "WHERE \n" +
                "`operation_wrong_longest` IS NOT NULL AND `operation_wrong_longest` > 0";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("WITH `contextual_log` AS (\n" +
                "\t\tSELECT *, count_if(`event` = 26001) OVER (PARTITION BY `uuid` ORDER BY `seq` ASC) AS `op_group`\n" +
                "\t\t\t, lag(`event`) OVER (PARTITION BY `uuid` ORDER BY `seq` ASC) AS `prev_event`\n" +
                "\t\tFROM `attendance_log`\n" +
                "\t\tWHERE `createdAt` >= unix_timestamp('20190828') * 1000\n" +
                "\t\t\tAND `createdAt` < unix_timestamp(date_add('20190828', 1)) * 1000\n" +
                "\t), \n" +
                "\t`consecutive_counted_log` AS (\n" +
                "\t\tSELECT *, count_if(`event` = `prev_event`) OVER (PARTITION BY `uuid`, `op_group`, `event` ORDER BY `seq` ASC) AS `consecutive_count`\n" +
                "\t\tFROM `contextual_log`\n" +
                "\t), \n" +
                "\t`log_metrics` AS (\n" +
                "\t\tSELECT `uuid`, `key`\n" +
                "\t\t\t, regexp_substr(`key`, '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$', 1) AS `course_id`\n" +
                "\t\t\t, regexp_substr(`key`, '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$', 2) AS `lesson_type`\n" +
                "\t\t\t, regexp_substr(`key`, '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$', 3) AS `lesson_id`\n" +
                "\t\t\t, regexp_substr(`key`, '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$', 4) AS `mode`\n" +
                "\t\t\t, regexp_substr(`key`, '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$', 5) AS `section`\n" +
                "\t\t\t, regexp_substr(`key`, '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$', 6) AS `level`\n" +
                "\t\t\t, regexp_substr(`key`, '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$', 7) AS `question_type`\n" +
                "\t\t\t, regexp_substr(`key`, '^C(\\\\d+)\\\\.([LER])(\\\\d+)(?:\\\\.([SD]))?(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?(?:\\\\.([QC])(\\\\d+))?$', 8) AS `question_id`\n" +
                "\t\t\t, min(happenedAt) AS `start_at`, max(happenedAt) AS `end_at`\n" +
                "\t\t\t, count_if(`event` = 11001) AS `app_pause`\n" +
                "\t\t\t, count_if(`event` = 11002) AS `app_resume`\n" +
                "\t\t\t, count_if(`event` = 21001) AS `lesson_start`\n" +
                "\t\t\t, count_if(`event` = 21002) AS `lesson_end`\n" +
                "\t\t\t, count_if(`event` = 21003) AS `lesson_quit`\n" +
                "\t\t\t, count_if(`event` = 21004) AS `lesson_evaluate`\n" +
                "\t\t\t, count_if(`event` = 21101) AS `part_start`\n" +
                "\t\t\t, count_if(`event` = 21102) AS `part_end`\n" +
                "\t\t\t, count_if(`event` = 22001) AS `section_start`\n" +
                "\t\t\t, count_if(`event` = 22002) AS `section_end`\n" +
                "\t\t\t, count_if(`event` = 23001) AS `level_start`\n" +
                "\t\t\t, count_if(`event` = 23002) AS `level_pass`\n" +
                "\t\t\t, count_if(`event` = 23003) AS `level_fail`\n" +
                "\t\t\t, count_if(`event` = 24001) AS `cutscene_start`\n" +
                "\t\t\t, count_if(`event` = 24002) AS `cutscene_end`\n" +
                "\t\t\t, count_if(`event` = 25001) AS `question_start`\n" +
                "\t\t\t, count_if(`event` = 25002) AS `question_pass`\n" +
                "\t\t\t, count_if(`event` = 25003) AS `question_fail`\n" +
                "\t\t\t, count_if(`event` = 25004) AS `question_pause`\n" +
                "\t\t\t, count_if(`event` = 25005) AS `question_resume`\n" +
                "\t\t\t, count_if(`event` = 26001) AS `operation_start`\n" +
                "\t\t\t, count_if(`event` = 26002) AS `operation_end`\n" +
                "\t\t\t, count_if(`event` = 26003) AS `operation_correct`\n" +
                "\t\t\t, count_if(`event` = 26004) AS `operation_wrong`\n" +
                "\t\t\t, max(CASE `event`\n" +
                "\t\t\t\tWHEN 26004 THEN `consecutive_count`\n" +
                "\t\t\tEND) AS `operation_wrong_longest`\n" +
                "\t\t\t, count_if(`event` = 26005) AS `operation_neutral`\n" +
                "\t\t\t, count_if(`event` = 26006) AS `operation_timeout`\n" +
                "\t\t\t, count_if(`event` = 31001) AS `photobooth_start`\n" +
                "\t\t\t, count_if(`event` = 31002) AS `photobooth_end`\n" +
                "\t\t\t, count_if(`event` = 31003) AS `photobooth_shot`\n" +
                "\t\t\t, count_if(`event` = 31004) AS `photobooth_skip`\n" +
                "\t\t\t, count_if(`event` = 31005) AS `photobooth_save`\n" +
                "\t\t\t, count_if(`event` = 31006) AS `photobooth_discard`\n" +
                "\t\t\t, count_if(`event` = 41001) AS `ui_next_appear`\n" +
                "\t\t\t, count_if(`event` = 41002) AS `ui_next_click`\n" +
                "\t\tFROM `consecutive_counted_log`\n" +
                "\t\tGROUP BY `uuid`, `key`\n" +
                "\t)\n" +
                "SELECT *\n" +
                "FROM `log_metrics`\n" +
                "WHERE `operation_wrong_longest` IS NOT NULL\n" +
                "\tAND `operation_wrong_longest` > 0", stmt.toString());
    }



}