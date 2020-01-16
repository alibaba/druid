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
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;


public class MySqlSelectTest_with_20433301 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "WITH total AS(\n" +
                "SELECT cinema_id, count(1) as scheduleCnt, sum(hall_seat_cnt) as hall_seat_cnt, sum(sold_seat_cnt) as sold_seat_cnt, sum(avg_ticket_price*sold_seat_cnt) as box_office\n" +
                "  FROM gw_ads_schedule_rt\n" +
                " WHERE show_date= 20190513\n" +
                "   and stat_month= 201905\n" +
                "   and stat_date= 20190508\n" +
                "   and scd_is_valid= 1\n" +
                "   and scd_status= 4\n" +
                " GROUP BY cinema_id),\n" +
                "         detail AS(\n" +
                "SELECT show_id, cinema_id, count(1) as scheduleCnt, sum(hall_seat_cnt) as hall_seat_cnt, sum(sold_seat_cnt) as sold_seat_cnt, sum(avg_ticket_price*sold_seat_cnt) as box_office\n" +
                "  FROM gw_ads_schedule_rt\n" +
                " WHERE show_date= 20190513\n" +
                "   and stat_month= 201905\n" +
                "   and stat_date= 20190508\n" +
                "   and scd_is_valid= 1\n" +
                "   and scd_status= 4\n" +
                "   AND show_id IN(1229077)\n" +
                " GROUP BY show_id, cinema_id)\n" +
                "SELECT detail.cinema_id,\n" +
                "       detail.show_id,\n" +
                "       detail.scheduleCnt,\n" +
                "       detail.hall_seat_cnt,\n" +
                "       detail.sold_seat_cnt,\n" +
                "       detail.box_office,\n" +
                "       total.scheduleCnt as totalScheduleCnt,\n" +
                "       total.hall_seat_cnt as totalHallSeatCnt,\n" +
                "       total.sold_seat_cnt as totalSoldSeatCnt,\n" +
                "       total.box_office as totalBoxOffice,\n" +
                "       fcst.scd_cnt_fcst as predictScheduleCnt\n" +
                "  FROM detail join total on detail.cinema_id= total.cinema_id left join gw_ads_cinema_schedule_fcst fcst on fcst.cinema_id= detail.cinema_id\n" +
                " where fcst.week_day= 1\n" +
                " order by detail.scheduleCnt desc";


        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, JdbcConstants.MYSQL);

        assertEquals("WITH total AS (\n" +
                "\t\tSELECT cinema_id, count(1) AS scheduleCnt, sum(hall_seat_cnt) AS hall_seat_cnt\n" +
                "\t\t\t, sum(sold_seat_cnt) AS sold_seat_cnt\n" +
                "\t\t\t, sum(avg_ticket_price * sold_seat_cnt) AS box_office\n" +
                "\t\tFROM gw_ads_schedule_rt\n" +
                "\t\tWHERE show_date = 20190513\n" +
                "\t\t\tAND stat_month = 201905\n" +
                "\t\t\tAND stat_date = 20190508\n" +
                "\t\t\tAND scd_is_valid = 1\n" +
                "\t\t\tAND scd_status = 4\n" +
                "\t\tGROUP BY cinema_id\n" +
                "\t), \n" +
                "\tdetail AS (\n" +
                "\t\tSELECT show_id, cinema_id, count(1) AS scheduleCnt\n" +
                "\t\t\t, sum(hall_seat_cnt) AS hall_seat_cnt, sum(sold_seat_cnt) AS sold_seat_cnt\n" +
                "\t\t\t, sum(avg_ticket_price * sold_seat_cnt) AS box_office\n" +
                "\t\tFROM gw_ads_schedule_rt\n" +
                "\t\tWHERE show_date = 20190513\n" +
                "\t\t\tAND stat_month = 201905\n" +
                "\t\t\tAND stat_date = 20190508\n" +
                "\t\t\tAND scd_is_valid = 1\n" +
                "\t\t\tAND scd_status = 4\n" +
                "\t\t\tAND show_id IN (1229077)\n" +
                "\t\tGROUP BY show_id, cinema_id\n" +
                "\t)\n" +
                "SELECT detail.cinema_id, detail.show_id, detail.scheduleCnt, detail.hall_seat_cnt, detail.sold_seat_cnt\n" +
                "\t, detail.box_office, total.scheduleCnt AS totalScheduleCnt, total.hall_seat_cnt AS totalHallSeatCnt, total.sold_seat_cnt AS totalSoldSeatCnt, total.box_office AS totalBoxOffice\n" +
                "\t, fcst.scd_cnt_fcst AS predictScheduleCnt\n" +
                "FROM detail\n" +
                "\tJOIN total ON detail.cinema_id = total.cinema_id\n" +
                "\tLEFT JOIN gw_ads_cinema_schedule_fcst fcst ON fcst.cinema_id = detail.cinema_id\n" +
                "WHERE fcst.week_day = 1\n" +
                "ORDER BY detail.scheduleCnt DESC", stmt.toString());


        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
    }



}