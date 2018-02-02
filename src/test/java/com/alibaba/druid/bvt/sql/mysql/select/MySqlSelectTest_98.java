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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class MySqlSelectTest_98 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO brand_crm_ship.imp_order_lock (tid, pid, __aid, pv) \n" +
                "          SELECT 121, \n" +
                "                 48868196, \n" +
                "                 t2.__aid, \n" +
                "                 t3.pv \n" +
                "          FROM  (SELECT DISTINCT __aid \n" +
                "                 FROM  (SELECT DISTINCT \n" +
                "                brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.__aid AS \n" +
                "                __aid \n" +
                "                FROM   brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump \n" +
                "          WHERE ( \n" +
                "          ( \n" +
                "          brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.label_list_interest IN( '1142' )\n" +
                "          AND brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.user_age \n" +
                "          IN \n" +
                "          ( \n" +
                "          '4', '3', '2', '1' ) \n" +
                "          AND \n" +
                "          brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.label_list_basic IN( '1605', '1603', '1604', '1563' ) )\n" +
                "          AND \n" +
                "          NOT \n" +
                "          ( \n" +
                "          brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.label_list_industry IN( '1140' ) ) )) t1) t2\n" +
                "          JOIN(SELECT __aid, \n" +
                "                      pv \n" +
                "               FROM   brand_crm_ship.palgo_o2o_imp_px_log_sample_adzone_aid_merge \n" +
                "               WHERE  adzone_id = 48868196) t3 \n" +
                "            ON t3.__aid = t2.__aid \n";

        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(3, visitor.getTables().size());
        Assert.assertEquals(12, visitor.getColumns().size());
        Assert.assertEquals(6, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("INSERT INTO brand_crm_ship.imp_order_lock (tid, pid, __aid, pv)\n" +
                            "SELECT 121, 48868196, t2.__aid, t3.pv\n" +
                            "FROM (\n" +
                            "\tSELECT DISTINCT __aid\n" +
                            "\tFROM (\n" +
                            "\t\tSELECT DISTINCT brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.__aid AS __aid\n" +
                            "\t\tFROM brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump\n" +
                            "\t\tWHERE brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.label_list_interest IN ('1142')\n" +
                            "\t\t\tAND brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.user_age IN ('4', '3', '2', '1')\n" +
                            "\t\t\tAND brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.label_list_basic IN ('1605', '1603', '1604', '1563')\n" +
                            "\t\t\tAND NOT (brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.label_list_industry IN ('1140'))\n" +
                            "\t) t1\n" +
                            ") t2\n" +
                            "\tJOIN (\n" +
                            "\t\tSELECT __aid, pv\n" +
                            "\t\tFROM brand_crm_ship.palgo_o2o_imp_px_log_sample_adzone_aid_merge\n" +
                            "\t\tWHERE adzone_id = 48868196\n" +
                            "\t) t3\n" +
                            "\tON t3.__aid = t2.__aid", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("insert into brand_crm_ship.imp_order_lock (tid, pid, __aid, pv)\n" +
                            "select 121, 48868196, t2.__aid, t3.pv\n" +
                            "from (\n" +
                            "\tselect distinct __aid\n" +
                            "\tfrom (\n" +
                            "\t\tselect distinct brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.__aid as __aid\n" +
                            "\t\tfrom brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump\n" +
                            "\t\twhere brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.label_list_interest in ('1142')\n" +
                            "\t\t\tand brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.user_age in ('4', '3', '2', '1')\n" +
                            "\t\t\tand brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.label_list_basic in ('1605', '1603', '1604', '1563')\n" +
                            "\t\t\tand not (brand_crm_ship.ktv_algo_brand_display_ad_label_out_dump.label_list_industry in ('1140'))\n" +
                            "\t) t1\n" +
                            ") t2\n" +
                            "\tjoin (\n" +
                            "\t\tselect __aid, pv\n" +
                            "\t\tfrom brand_crm_ship.palgo_o2o_imp_px_log_sample_adzone_aid_merge\n" +
                            "\t\twhere adzone_id = 48868196\n" +
                            "\t) t3\n" +
                            "\ton t3.__aid = t2.__aid", //
                                output);
        }
    }
    
    
    
}
