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


public class MySqlSelectTest_20190424_01 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/*+ engine= MPP, dump-header= [DUMP DATA]*/\n" +
                "SELECT t3.sim_aid AS __aid\n" +
                "  FROM(\n" +
                "SELECT CASE WHEN t2.__aid IS NOT NULL THEN floor(t1.s * 1.05) ELSE t1.s END AS sum_score, t1.sim_aid AS sim_aid\n" +
                "  FROM(\n" +
                "SELECT sum(new_dmp.alimama_ecpm_algo_xl_online_lookalike_result.score) AS s, new_dmp.alimama_ecpm_algo_xl_online_lookalike_result.sim_aid AS sim_aid\n" +
                "  FROM new_dmp.alimama_ecpm_algo_xl_online_lookalike_result INNER JOIN(\n" +
                "SELECT DISTINCT m_1.__aid AS __aid\n" +
                "  FROM(\n" +
                "SELECT DISTINCT new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_cart.__aid AS __aid\n" +
                "  FROM new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_cart\n" +
                " WHERE new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_cart.freq_15> 0\n" +
                "   AND new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_cart.item_id IN('537565093636', '537523510114', '537603904250', '537524662802', '561704153817') MINUS\n" +
                "SELECT new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_order.__aid AS __aid\n" +
                "  FROM new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_order\n" +
                " WHERE new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_order.item_id IN('537523510114', '537603904250', '537524662802', '561843299403', '561704153817', '537565093636')\n" +
                "   AND new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_order.freq_15>= 1\n" +
                "   AND new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_order.freq_15<= 999999999) AS m_1) t ON t.__aid= new_dmp.alimama_ecpm_algo_xl_online_lookalike_result.__aid\n" +
                " GROUP BY new_dmp.alimama_ecpm_algo_xl_online_lookalike_result.sim_aid) t1 LEFT JOIN(\n" +
                "SELECT __aid\n" +
                "  FROM new_dmp.alimama_ecpm_algo_dmp_ump_lookalike_online_white_feature\n" +
                " WHERE new_dmp.alimama_ecpm_algo_dmp_ump_lookalike_online_white_feature.features IN(1016571, 1014226, 1014223, 1014225)) t2 ON t1.sim_aid= t2.__aid) t3\n" +
                " WHERE t3.sum_score> 1986\n" +
                "    OR(t3.sum_score= 1986\n" +
                "   AND t3.sim_aid % 100< 97)";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("/*+ engine= MPP, dump-header= [DUMP DATA]*/\n" +
                "SELECT t3.sim_aid AS __aid\n" +
                "FROM (\n" +
                "\tSELECT CASE \n" +
                "\t\t\tWHEN t2.__aid IS NOT NULL THEN floor(t1.s * 1.05)\n" +
                "\t\t\tELSE t1.s\n" +
                "\t\tEND AS sum_score, t1.sim_aid AS sim_aid\n" +
                "\tFROM (\n" +
                "\t\tSELECT sum(new_dmp.alimama_ecpm_algo_xl_online_lookalike_result.score) AS s, new_dmp.alimama_ecpm_algo_xl_online_lookalike_result.sim_aid AS sim_aid\n" +
                "\t\tFROM new_dmp.alimama_ecpm_algo_xl_online_lookalike_result\n" +
                "\t\t\tINNER JOIN (\n" +
                "\t\t\t\tSELECT DISTINCT m_1.__aid AS __aid\n" +
                "\t\t\t\tFROM (\n" +
                "\t\t\t\t\tSELECT DISTINCT new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_cart.__aid AS __aid\n" +
                "\t\t\t\t\tFROM new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_cart\n" +
                "\t\t\t\t\tWHERE new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_cart.freq_15 > 0\n" +
                "\t\t\t\t\t\tAND new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_cart.item_id IN ('537565093636', '537523510114', '537603904250', '537524662802', '561704153817')\n" +
                "\t\t\t\t\tMINUS\n" +
                "\t\t\t\t\tSELECT new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_order.__aid AS __aid\n" +
                "\t\t\t\t\tFROM new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_order\n" +
                "\t\t\t\t\tWHERE new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_order.item_id IN (\n" +
                "\t\t\t\t\t\t\t'537523510114', \n" +
                "\t\t\t\t\t\t\t'537603904250', \n" +
                "\t\t\t\t\t\t\t'537524662802', \n" +
                "\t\t\t\t\t\t\t'561843299403', \n" +
                "\t\t\t\t\t\t\t'561704153817', \n" +
                "\t\t\t\t\t\t\t'537565093636'\n" +
                "\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\tAND new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_order.freq_15 >= 1\n" +
                "\t\t\t\t\t\tAND new_dmp.alimama_ecpm_algo_dmp_item_behavior_detail_order.freq_15 <= 999999999\n" +
                "\t\t\t\t) m_1\n" +
                "\t\t\t) t\n" +
                "\t\t\tON t.__aid = new_dmp.alimama_ecpm_algo_xl_online_lookalike_result.__aid\n" +
                "\t\tGROUP BY new_dmp.alimama_ecpm_algo_xl_online_lookalike_result.sim_aid\n" +
                "\t) t1\n" +
                "\t\tLEFT JOIN (\n" +
                "\t\t\tSELECT __aid\n" +
                "\t\t\tFROM new_dmp.alimama_ecpm_algo_dmp_ump_lookalike_online_white_feature\n" +
                "\t\t\tWHERE new_dmp.alimama_ecpm_algo_dmp_ump_lookalike_online_white_feature.features IN (1016571, 1014226, 1014223, 1014225)\n" +
                "\t\t) t2\n" +
                "\t\tON t1.sim_aid = t2.__aid\n" +
                ") t3\n" +
                "WHERE t3.sum_score > 1986\n" +
                "\tOR (t3.sum_score = 1986\n" +
                "\t\tAND t3.sim_aid % 100 < 97)", stmt.toString());
    }



}