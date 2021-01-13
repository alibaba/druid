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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import junit.framework.TestCase;

import java.util.List;

public class HiveSelectTest_43_bug extends TestCase {

    public void test_0() throws Exception {
        String sql = "from (select a.ddate\n" +
                "        ,a.game_id\n" +
                "        ,a.plat_id\n" +
                "        ,a.channel_group_id\n" +
                "        ,a.channel_id\n" +
                "        ,a.zone_id\n" +
                "        ,a.player_id\n" +
                "        ,pay_amt_1d\n" +
                "        ,pay_cnt_1d\n" +
                "        ,CASE WHEN actv_day_num_td = 0 THEN 0 \n" +
                "              WHEN actv_day_num_td<=3 THEN 1\n" +
                "              WHEN actv_day_num_td<=6 THEN 2 \n" +
                "              WHEN actv_day_num_td<=13 THEN 3\n" +
                "              WHEN actv_day_num_td<=21 THEN 4\n" +
                "              WHEN actv_day_num_td<=30 THEN 5\n" +
                "              WHEN actv_day_num_td<=60 THEN 6\n" +
                "              WHEN actv_day_num_td<=90 THEN 7\n" +
                "              WHEN actv_day_num_td>90 THEN 99 \n" +
                "              ELSE NULL END AS actv_day_num_td_segment_id     \n" +
                "        ,CASE WHEN reg_day_num_td = 0 THEN 0 \n" +
                "              WHEN reg_day_num_td<=3 THEN 1\n" +
                "              WHEN reg_day_num_td<=6 THEN 2 \n" +
                "              WHEN reg_day_num_td<=13 THEN 3\n" +
                "              WHEN reg_day_num_td<=21 THEN 4\n" +
                "              WHEN reg_day_num_td<=30 THEN 5\n" +
                "              WHEN reg_day_num_td<=60 THEN 6\n" +
                "              WHEN reg_day_num_td<=90 THEN 7\n" +
                "              WHEN reg_day_num_td>90 THEN 99 \n" +
                "              ELSE NULL END AS reg_day_num_td_segment_id \n" +
                "  from   (select ddate\n" +
                "                ,game_id\n" +
                "                ,plat_id\n" +
                "                ,channel_group_id\n" +
                "                ,channel_id\n" +
                "                ,zone_id\n" +
                "                ,player_id\n" +
                "                ,pay_amt_1d\n" +
                "                ,pay_cnt_1d\n" +
                "          from   dws_game_sdk_base.dws_game_sdk_pay_user_base_d a\n" +
                "          where  a.ddate >= '20200512'\n" +
                "          and    a.ddate < '20200513'\n" +
                "          and    a.first_pay_flag = 1) a\n" +
                "  inner  join (select ddate\n" +
                "                    ,game_id\n" +
                "                    ,plat_id\n" +
                "                    ,channel_group_id\n" +
                "                    ,channel_id\n" +
                "                    ,zone_id\n" +
                "                    ,player_id\n" +
                "                    ,actv_day_num_td\n" +
                "                    ,udf.datediff(ddate, reg_date) as reg_day_num_td\n" +
                "              from   dws_game_sdk_base.dws_game_sdk_user_actv_base_d a\n" +
                "              where  a.ddate >= '20200512'\n" +
                "              and    a.ddate < '20200513') b\n" +
                "  on     (a.ddate = b.ddate and a.game_id = b.game_id and\n" +
                "         a.plat_id = b.plat_id and a.channel_group_id = b.channel_group_id and\n" +
                "         a.channel_id = b.channel_id and a.zone_id = b.zone_id and\n" +
                "         a.player_id = b.player_id) ) d \n" +
                "INSERT OVERWRITE TABLE ads_game_sdk_base.ads_rpt_game_sdk_user_segment_d PARTITION(ddate,segment_type,user_type_id)\n" +
                "SELECT  game_id\n" +
                "    ,plat_id\n" +
                "    ,channel_group_id\n" +
                "    ,channel_id\n" +
                "    ,zone_id\n" +
                "    ,actv_day_num_td_segment_id AS segment_id\n" +
                "    ,CASE    WHEN actv_day_num_td_segment_id = 0 THEN '0天'\n" +
                "             WHEN actv_day_num_td_segment_id = 1 THEN '1~3天'\n" +
                "             WHEN actv_day_num_td_segment_id = 2 THEN '4~6天'\n" +
                "             WHEN actv_day_num_td_segment_id = 3 THEN '7~13天'\n" +
                "             WHEN actv_day_num_td_segment_id = 4 THEN '14~21天'\n" +
                "             WHEN actv_day_num_td_segment_id = 5 THEN '21~30天'\n" +
                "             WHEN actv_day_num_td_segment_id = 6 THEN '30~60天'\n" +
                "             WHEN actv_day_num_td_segment_id = 7 THEN '60~90天'\n" +
                "             WHEN actv_day_num_td_segment_id = 99 THEN '>90天' \n" +
                "     END AS segment_name\n" +
                "    ,COUNT(1) AS segment_user_num_1d\n" +
                "    ,CAST(NULL AS BIGINT) AS segment_value_sum_1d\n" +
                "    ,SUM(pay_cnt_1d) AS segment_ext_int_value1_1d\n" +
                "    ,CAST(NULL AS BIGINT) AS segment_ext_int_value2_1d\n" +
                "    ,CAST(NULL AS BIGINT) AS segment_ext_int_value3_1d\n" +
                "    ,CAST(NULL AS BIGINT) AS segment_ext_int_value1_4d\n" +
                "    ,SUM(pay_amt_1d) AS segment_ext_int_value4_1d\n" +
                "    ,CAST(NULL AS DOUBLE) AS segment_ext_double_value2_1d\n" +
                "    ,CAST(NULL AS DOUBLE) AS segment_ext_double_value3_1d\n" +
                "    ,CAST(NULL AS DOUBLE) AS segment_ext_double_value4_1d\n" +
                "    ,ddate\n" +
                "    ,'first_pay_user_actv_day_num' AS segment_type\n" +
                "    ,3 AS user_type_id\n" +
                "WHERE   actv_day_num_td_segment_id IS NOT NULL\n" +
                "GROUP BY ddate\n" +
                "     ,game_id\n" +
                "     ,plat_id\n" +
                "     ,channel_group_id\n" +
                "     ,channel_id\n" +
                "     ,zone_id\n" +
                "     ,actv_day_num_td_segment_id\n" +
                "INSERT OVERWRITE TABLE ads_game_sdk_base.ads_rpt_game_sdk_user_segment_d PARTITION(ddate,segment_type,user_type_id)\n" +
                "SELECT  game_id\n" +
                "    ,plat_id\n" +
                "    ,channel_group_id\n" +
                "    ,channel_id\n" +
                "    ,zone_id\n" +
                "    ,reg_day_num_td_segment_id AS segment_id\n" +
                "    ,CASE    WHEN reg_day_num_td_segment_id = 0 THEN '0天'\n" +
                "             WHEN reg_day_num_td_segment_id = 1 THEN '1~3天'\n" +
                "             WHEN reg_day_num_td_segment_id = 2 THEN '4~6天'\n" +
                "             WHEN reg_day_num_td_segment_id = 3 THEN '7~13天'\n" +
                "             WHEN reg_day_num_td_segment_id = 4 THEN '14~21天'\n" +
                "             WHEN reg_day_num_td_segment_id = 5 THEN '21~30天'\n" +
                "             WHEN reg_day_num_td_segment_id = 6 THEN '30~60天'\n" +
                "             WHEN reg_day_num_td_segment_id = 7 THEN '60~90天'\n" +
                "             WHEN reg_day_num_td_segment_id = 99 THEN '>90天' \n" +
                "     END AS segment_name\n" +
                "    ,COUNT(1) AS segment_user_num_1d\n" +
                "    ,CAST(NULL AS BIGINT) AS segment_value_sum_1d\n" +
                "    ,SUM(pay_cnt_1d) AS segment_ext_int_value1_1d\n" +
                "    ,CAST(NULL AS BIGINT) AS segment_ext_int_value2_1d\n" +
                "    ,CAST(NULL AS BIGINT) AS segment_ext_int_value3_1d\n" +
                "    ,CAST(NULL AS BIGINT) AS segment_ext_int_value1_4d\n" +
                "    ,SUM(pay_amt_1d) AS segment_ext_int_value4_1d\n" +
                "    ,CAST(NULL AS DOUBLE) AS segment_ext_double_value2_1d\n" +
                "    ,CAST(NULL AS DOUBLE) AS segment_ext_double_value3_1d\n" +
                "    ,CAST(NULL AS DOUBLE) AS segment_ext_double_value4_1d\n" +
                "    ,ddate\n" +
                "    ,'first_pay_user_reg_day_num' AS segment_type\n" +
                "    ,3 AS user_type_id\n" +
                "WHERE   reg_day_num_td_segment_id IS NOT NULL\n" +
                "GROUP BY ddate\n" +
                "     ,game_id\n" +
                "     ,plat_id\n" +
                "     ,channel_group_id\n" +
                "     ,channel_id\n" +
                "     ,zone_id\n" +
                "     ,reg_day_num_td_segment_id\n" +
                ";";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive);
        HiveMultiInsertStatement stmt = (HiveMultiInsertStatement) statementList.get(0);

        assertEquals("FROM (\n" +
                "\tSELECT a.ddate, a.game_id, a.plat_id, a.channel_group_id, a.channel_id\n" +
                "\t\t, a.zone_id, a.player_id, pay_amt_1d, pay_cnt_1d\n" +
                "\t\t, CASE \n" +
                "\t\t\tWHEN actv_day_num_td = 0 THEN 0\n" +
                "\t\t\tWHEN actv_day_num_td <= 3 THEN 1\n" +
                "\t\t\tWHEN actv_day_num_td <= 6 THEN 2\n" +
                "\t\t\tWHEN actv_day_num_td <= 13 THEN 3\n" +
                "\t\t\tWHEN actv_day_num_td <= 21 THEN 4\n" +
                "\t\t\tWHEN actv_day_num_td <= 30 THEN 5\n" +
                "\t\t\tWHEN actv_day_num_td <= 60 THEN 6\n" +
                "\t\t\tWHEN actv_day_num_td <= 90 THEN 7\n" +
                "\t\t\tWHEN actv_day_num_td > 90 THEN 99\n" +
                "\t\t\tELSE NULL\n" +
                "\t\tEND AS actv_day_num_td_segment_id\n" +
                "\t\t, CASE \n" +
                "\t\t\tWHEN reg_day_num_td = 0 THEN 0\n" +
                "\t\t\tWHEN reg_day_num_td <= 3 THEN 1\n" +
                "\t\t\tWHEN reg_day_num_td <= 6 THEN 2\n" +
                "\t\t\tWHEN reg_day_num_td <= 13 THEN 3\n" +
                "\t\t\tWHEN reg_day_num_td <= 21 THEN 4\n" +
                "\t\t\tWHEN reg_day_num_td <= 30 THEN 5\n" +
                "\t\t\tWHEN reg_day_num_td <= 60 THEN 6\n" +
                "\t\t\tWHEN reg_day_num_td <= 90 THEN 7\n" +
                "\t\t\tWHEN reg_day_num_td > 90 THEN 99\n" +
                "\t\t\tELSE NULL\n" +
                "\t\tEND AS reg_day_num_td_segment_id\n" +
                "\tFROM (\n" +
                "\t\tSELECT ddate, game_id, plat_id, channel_group_id, channel_id\n" +
                "\t\t\t, zone_id, player_id, pay_amt_1d, pay_cnt_1d\n" +
                "\t\tFROM dws_game_sdk_base.dws_game_sdk_pay_user_base_d a\n" +
                "\t\tWHERE a.ddate >= '20200512'\n" +
                "\t\t\tAND a.ddate < '20200513'\n" +
                "\t\t\tAND a.first_pay_flag = 1\n" +
                "\t) a\n" +
                "\t\tINNER JOIN (\n" +
                "\t\t\tSELECT ddate, game_id, plat_id, channel_group_id, channel_id\n" +
                "\t\t\t\t, zone_id, player_id, actv_day_num_td\n" +
                "\t\t\t\t, udf.datediff(ddate, reg_date) AS reg_day_num_td\n" +
                "\t\t\tFROM dws_game_sdk_base.dws_game_sdk_user_actv_base_d a\n" +
                "\t\t\tWHERE a.ddate >= '20200512'\n" +
                "\t\t\t\tAND a.ddate < '20200513'\n" +
                "\t\t) b\n" +
                "\t\tON a.ddate = b.ddate\n" +
                "\t\t\tAND a.game_id = b.game_id\n" +
                "\t\t\tAND a.plat_id = b.plat_id\n" +
                "\t\t\tAND a.channel_group_id = b.channel_group_id\n" +
                "\t\t\tAND a.channel_id = b.channel_id\n" +
                "\t\t\tAND a.zone_id = b.zone_id\n" +
                "\t\t\tAND a.player_id = b.player_id\n" +
                ") d\n" +
                "INSERT OVERWRITE TABLE ads_game_sdk_base.ads_rpt_game_sdk_user_segment_d PARTITION (ddate, segment_type, user_type_id)\n" +
                "SELECT game_id, plat_id, channel_group_id, channel_id, zone_id\n" +
                "\t, actv_day_num_td_segment_id AS segment_id\n" +
                "\t, CASE \n" +
                "\t\tWHEN actv_day_num_td_segment_id = 0 THEN '0天'\n" +
                "\t\tWHEN actv_day_num_td_segment_id = 1 THEN '1~3天'\n" +
                "\t\tWHEN actv_day_num_td_segment_id = 2 THEN '4~6天'\n" +
                "\t\tWHEN actv_day_num_td_segment_id = 3 THEN '7~13天'\n" +
                "\t\tWHEN actv_day_num_td_segment_id = 4 THEN '14~21天'\n" +
                "\t\tWHEN actv_day_num_td_segment_id = 5 THEN '21~30天'\n" +
                "\t\tWHEN actv_day_num_td_segment_id = 6 THEN '30~60天'\n" +
                "\t\tWHEN actv_day_num_td_segment_id = 7 THEN '60~90天'\n" +
                "\t\tWHEN actv_day_num_td_segment_id = 99 THEN '>90天'\n" +
                "\tEND AS segment_name, COUNT(1) AS segment_user_num_1d, CAST(NULL AS BIGINT) AS segment_value_sum_1d\n" +
                "\t, SUM(pay_cnt_1d) AS segment_ext_int_value1_1d, CAST(NULL AS BIGINT) AS segment_ext_int_value2_1d, CAST(NULL AS BIGINT) AS segment_ext_int_value3_1d\n" +
                "\t, CAST(NULL AS BIGINT) AS segment_ext_int_value1_4d, SUM(pay_amt_1d) AS segment_ext_int_value4_1d, CAST(NULL AS DOUBLE) AS segment_ext_double_value2_1d, CAST(NULL AS DOUBLE) AS segment_ext_double_value3_1d\n" +
                "\t, CAST(NULL AS DOUBLE) AS segment_ext_double_value4_1d, ddate\n" +
                "\t, 'first_pay_user_actv_day_num' AS segment_type, 3 AS user_type_id\n" +
                "WHERE actv_day_num_td_segment_id IS NOT NULL\n" +
                "GROUP BY ddate, game_id, plat_id, channel_group_id, channel_id, zone_id, actv_day_num_td_segment_id\n" +
                "INSERT OVERWRITE TABLE ads_game_sdk_base.ads_rpt_game_sdk_user_segment_d PARTITION (ddate, segment_type, user_type_id)\n" +
                "SELECT game_id, plat_id, channel_group_id, channel_id, zone_id\n" +
                "\t, reg_day_num_td_segment_id AS segment_id\n" +
                "\t, CASE \n" +
                "\t\tWHEN reg_day_num_td_segment_id = 0 THEN '0天'\n" +
                "\t\tWHEN reg_day_num_td_segment_id = 1 THEN '1~3天'\n" +
                "\t\tWHEN reg_day_num_td_segment_id = 2 THEN '4~6天'\n" +
                "\t\tWHEN reg_day_num_td_segment_id = 3 THEN '7~13天'\n" +
                "\t\tWHEN reg_day_num_td_segment_id = 4 THEN '14~21天'\n" +
                "\t\tWHEN reg_day_num_td_segment_id = 5 THEN '21~30天'\n" +
                "\t\tWHEN reg_day_num_td_segment_id = 6 THEN '30~60天'\n" +
                "\t\tWHEN reg_day_num_td_segment_id = 7 THEN '60~90天'\n" +
                "\t\tWHEN reg_day_num_td_segment_id = 99 THEN '>90天'\n" +
                "\tEND AS segment_name, COUNT(1) AS segment_user_num_1d, CAST(NULL AS BIGINT) AS segment_value_sum_1d\n" +
                "\t, SUM(pay_cnt_1d) AS segment_ext_int_value1_1d, CAST(NULL AS BIGINT) AS segment_ext_int_value2_1d, CAST(NULL AS BIGINT) AS segment_ext_int_value3_1d\n" +
                "\t, CAST(NULL AS BIGINT) AS segment_ext_int_value1_4d, SUM(pay_amt_1d) AS segment_ext_int_value4_1d, CAST(NULL AS DOUBLE) AS segment_ext_double_value2_1d, CAST(NULL AS DOUBLE) AS segment_ext_double_value3_1d\n" +
                "\t, CAST(NULL AS DOUBLE) AS segment_ext_double_value4_1d, ddate\n" +
                "\t, 'first_pay_user_reg_day_num' AS segment_type, 3 AS user_type_id\n" +
                "WHERE reg_day_num_td_segment_id IS NOT NULL\n" +
                "GROUP BY ddate, game_id, plat_id, channel_group_id, channel_id, zone_id, reg_day_num_td_segment_id;", stmt.toString());

//        SQLUtils.toSQLString(stmt, DbType.hive)

    }
}
