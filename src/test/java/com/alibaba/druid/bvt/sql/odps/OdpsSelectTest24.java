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
package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class OdpsSelectTest24 extends TestCase {

    public void test_select() throws Exception {
        // 1095288847322
        String sql = "SELECT ta.member_id AS member_id\n" +
                "\t, ta.city_id\n" +
                "\t, COUNT(IF(ta.add_date = '${date_minus_1}', 1, NULL)) AS order_cnt\n" +
                "\t, COUNT(ta.is_open_order) AS open_order_cnt\n" +
                "\t, COUNT(t1.is_normal_order) AS normal_order_cnt\n" +
                "\t, SUM(t2.is_master_appointed) AS master_appointed_cnt\n" +
                "\t, SUM(t1.is_psaz_order) AS psaz_order_cnt\n" +
                "\t, SUM(t1.is_az_order) AS az_order_cnt\n" +
                "\t, SUM(t1.is_wx_order) AS wx_order_cnt\n" +
                "\t, SUM(t1.is_qx_order) AS qx_order_cnt\n" +
                "\t, SUM(t2.is_trade_complete) AS trade_complete_cnt\n" +
                "\t, SUM(IF(t2.is_trade_complete = 1, t2.order_price, 0)) AS order_income\n" +
                "\t, SUM(t2.is_trade_close) AS trade_close_cnt\n" +
                "FROM (\n" +
                "\tSELECT order_id\n" +
                "\t\t, member_id\n" +
                "\t\t, order_price\n" +
                "\t\t, region_id\n" +
                "\t\t, appoint_method\n" +
                "\t\t, category\n" +
                "\t\t, serve_types\n" +
                "\t\t, to_char(from_unixtime(add_time), 'yyyy-mm-dd') AS add_date\n" +
                "\t\t, IF(t1.appoint_method = 'open', 1, NULL) AS is_open_order\n" +
                "\t\t, IF(t1.appoint_method = 'normal', 1, NULL) AS is_normal_order\n" +
                "\t\t, IF((t1.category = 1\n" +
                "\t\tAND t1.serve_types = 3)\n" +
                "\t\tOR (t1.category > 1\n" +
                "\t\tAND t1.serve_types = 2), 1, NULL) AS is_psaz_order\n" +
                "\t\t, IF((t1.category = 1\n" +
                "\t\tAND t1.serve_types = 4)\n" +
                "\t\tOR (t1.category > 1\n" +
                "\t\tAND t1.serve_types = 3), 1, NULL) AS is_az_order\n" +
                "\t\t, IF((t1.category = 1\n" +
                "\t\tAND t1.serve_types = 5)\n" +
                "\t\tOR (t1.category > 1\n" +
                "\t\tAND t1.serve_types = 4), 1, NULL) AS is_wx_order\n" +
                "\t\t, IF(t1.category > 1\n" +
                "\t\tAND t1.serve_types = 5, 1, NULL) AS is_qx_order\n" +
                "\tFROM bds_order_base_d\n" +
                "\tWHERE dt = '${date_minus_1}'\n" +
                ") t1\n" +
                "LEFT OUTER JOIN (\n" +
                "\tSELECT order_id\n" +
                "\t\t, MAX(IF(status = 'master_appointed', 1, 0)) AS is_master_appointed\n" +
                "\t\t, MAX(IF(status = 'trade_complete', 1, 0)) AS is_trade_complete\n" +
                "\t\t, MAX(IF(status = 'trade_close', 1, 0)) AS is_trade_close\n" +
                "\tFROM ods_order_tracking_time_d\n" +
                "\tWHERE dt = '${date_minus_1}'\n" +
                "\t\tAND status IN ('master_appointed', 'trade_complete', 'trade_close')\n" +
                "\tGROUP BY order_id\n" +
                ") t2\n" +
                "ON t1.order_id = t2.order_id\n" +
                "LEFT OUTER JOIN (\n" +
                "\tSELECT region_id\n" +
                "\t\t, lvl2region_id AS city_id\n" +
                "\tFROM d_prov_city_district\n" +
                ") t3\n" +
                "ON t1.region_id = t3.region_id\n" +
                "WHERE t1.add_date = '${date_minus_1}'\n" +
                "\tOR t2.order_id IS NOT NULL\n" +
                "GROUP BY t1.member_id, \n" +
                "\tt3.city_id";//
        assertEquals("SELECT ta.member_id AS member_id, ta.city_id\n" +
                "\t, COUNT(IF(ta.add_date = '${date_minus_1}', 1, NULL)) AS order_cnt\n" +
                "\t, COUNT(ta.is_open_order) AS open_order_cnt, COUNT(t1.is_normal_order) AS normal_order_cnt\n" +
                "\t, SUM(t2.is_master_appointed) AS master_appointed_cnt, SUM(t1.is_psaz_order) AS psaz_order_cnt\n" +
                "\t, SUM(t1.is_az_order) AS az_order_cnt, SUM(t1.is_wx_order) AS wx_order_cnt\n" +
                "\t, SUM(t1.is_qx_order) AS qx_order_cnt, SUM(t2.is_trade_complete) AS trade_complete_cnt\n" +
                "\t, SUM(IF(t2.is_trade_complete = 1, t2.order_price, 0)) AS order_income\n" +
                "\t, SUM(t2.is_trade_close) AS trade_close_cnt\n" +
                "FROM (\n" +
                "\tSELECT order_id, member_id, order_price, region_id, appoint_method\n" +
                "\t\t, category, serve_types\n" +
                "\t\t, to_char(from_unixtime(add_time), 'yyyy-mm-dd') AS add_date\n" +
                "\t\t, IF(t1.appoint_method = 'open', 1, NULL) AS is_open_order\n" +
                "\t\t, IF(t1.appoint_method = 'normal', 1, NULL) AS is_normal_order\n" +
                "\t\t, IF((t1.category = 1\n" +
                "\t\t\t\tAND t1.serve_types = 3)\n" +
                "\t\t\tOR (t1.category > 1\n" +
                "\t\t\t\tAND t1.serve_types = 2), 1, NULL) AS is_psaz_order\n" +
                "\t\t, IF((t1.category = 1\n" +
                "\t\t\t\tAND t1.serve_types = 4)\n" +
                "\t\t\tOR (t1.category > 1\n" +
                "\t\t\t\tAND t1.serve_types = 3), 1, NULL) AS is_az_order\n" +
                "\t\t, IF((t1.category = 1\n" +
                "\t\t\t\tAND t1.serve_types = 5)\n" +
                "\t\t\tOR (t1.category > 1\n" +
                "\t\t\t\tAND t1.serve_types = 4), 1, NULL) AS is_wx_order\n" +
                "\t\t, IF(t1.category > 1\n" +
                "\t\t\tAND t1.serve_types = 5, 1, NULL) AS is_qx_order\n" +
                "\tFROM bds_order_base_d\n" +
                "\tWHERE dt = '${date_minus_1}'\n" +
                ") t1\n" +
                "LEFT OUTER JOIN (\n" +
                "\tSELECT order_id\n" +
                "\t\t, MAX(IF(status = 'master_appointed', 1, 0)) AS is_master_appointed\n" +
                "\t\t, MAX(IF(status = 'trade_complete', 1, 0)) AS is_trade_complete\n" +
                "\t\t, MAX(IF(status = 'trade_close', 1, 0)) AS is_trade_close\n" +
                "\tFROM ods_order_tracking_time_d\n" +
                "\tWHERE dt = '${date_minus_1}'\n" +
                "\t\tAND status IN ('master_appointed', 'trade_complete', 'trade_close')\n" +
                "\tGROUP BY order_id\n" +
                ") t2\n" +
                "ON t1.order_id = t2.order_id\n" +
                "LEFT OUTER JOIN (\n" +
                "\tSELECT region_id, lvl2region_id AS city_id\n" +
                "\tFROM d_prov_city_district\n" +
                ") t3\n" +
                "ON t1.region_id = t3.region_id\n" +
                "WHERE t1.add_date = '${date_minus_1}'\n" +
                "\tOR t2.order_id IS NOT NULL\n" +
                "GROUP BY t1.member_id, \n" +
                "\tt3.city_id", SQLUtils.formatOdps(sql));

        assertEquals("select ta.member_id as member_id, ta.city_id\n" +
                "\t, count(IF(ta.add_date = '${date_minus_1}', 1, null)) as order_cnt\n" +
                "\t, count(ta.is_open_order) as open_order_cnt, count(t1.is_normal_order) as normal_order_cnt\n" +
                "\t, sum(t2.is_master_appointed) as master_appointed_cnt, sum(t1.is_psaz_order) as psaz_order_cnt\n" +
                "\t, sum(t1.is_az_order) as az_order_cnt, sum(t1.is_wx_order) as wx_order_cnt\n" +
                "\t, sum(t1.is_qx_order) as qx_order_cnt, sum(t2.is_trade_complete) as trade_complete_cnt\n" +
                "\t, sum(IF(t2.is_trade_complete = 1, t2.order_price, 0)) as order_income\n" +
                "\t, sum(t2.is_trade_close) as trade_close_cnt\n" +
                "from (\n" +
                "\tselect order_id, member_id, order_price, region_id, appoint_method\n" +
                "\t\t, category, serve_types\n" +
                "\t\t, to_char(from_unixtime(add_time), 'yyyy-mm-dd') as add_date\n" +
                "\t\t, IF(t1.appoint_method = 'open', 1, null) as is_open_order\n" +
                "\t\t, IF(t1.appoint_method = 'normal', 1, null) as is_normal_order\n" +
                "\t\t, IF((t1.category = 1\n" +
                "\t\t\t\tand t1.serve_types = 3)\n" +
                "\t\t\tor (t1.category > 1\n" +
                "\t\t\t\tand t1.serve_types = 2), 1, null) as is_psaz_order\n" +
                "\t\t, IF((t1.category = 1\n" +
                "\t\t\t\tand t1.serve_types = 4)\n" +
                "\t\t\tor (t1.category > 1\n" +
                "\t\t\t\tand t1.serve_types = 3), 1, null) as is_az_order\n" +
                "\t\t, IF((t1.category = 1\n" +
                "\t\t\t\tand t1.serve_types = 5)\n" +
                "\t\t\tor (t1.category > 1\n" +
                "\t\t\t\tand t1.serve_types = 4), 1, null) as is_wx_order\n" +
                "\t\t, IF(t1.category > 1\n" +
                "\t\t\tand t1.serve_types = 5, 1, null) as is_qx_order\n" +
                "\tfrom bds_order_base_d\n" +
                "\twhere dt = '${date_minus_1}'\n" +
                ") t1\n" +
                "left outer join (\n" +
                "\tselect order_id\n" +
                "\t\t, max(IF(status = 'master_appointed', 1, 0)) as is_master_appointed\n" +
                "\t\t, max(IF(status = 'trade_complete', 1, 0)) as is_trade_complete\n" +
                "\t\t, max(IF(status = 'trade_close', 1, 0)) as is_trade_close\n" +
                "\tfrom ods_order_tracking_time_d\n" +
                "\twhere dt = '${date_minus_1}'\n" +
                "\t\tand status in ('master_appointed', 'trade_complete', 'trade_close')\n" +
                "\tgroup by order_id\n" +
                ") t2\n" +
                "on t1.order_id = t2.order_id\n" +
                "left outer join (\n" +
                "\tselect region_id, lvl2region_id as city_id\n" +
                "\tfrom d_prov_city_district\n" +
                ") t3\n" +
                "on t1.region_id = t3.region_id\n" +
                "where t1.add_date = '${date_minus_1}'\n" +
                "\tor t2.order_id is not null\n" +
                "group by t1.member_id, \n" +
                "\tt3.city_id", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);

        System.out.println(stmt);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(3, visitor.getTables().size());
        assertEquals(19, visitor.getColumns().size());
        assertEquals(13, visitor.getConditions().size());

//        System.out.println(SQLUtils.formatOdps(sql));

//        assertTrue(visitor.getColumns().contains(new Column("abc", "name")));
    }


}
