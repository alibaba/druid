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
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class MySqlSelectTest_294_dla extends MysqlTest {


    public void test_1() throws Exception {
        String sql = "/*+engine=spark*/\n" +
                "select  date_p,os_p,nvl(channel,'新增') as channel,\n" +
                "  new_uv,tp_uv,svp_uv\n" +
                "from\n" +
                "(select   date_p,channel,n.os_p as os_p,\n" +
                "  count(distinct n.server_id) as new_uv,\n" +
                "  count(distinct if(tp_guanjian_cnt > 0,n.server_id,null)) as tp_uv,\n" +
                "  count(distinct if(svp_cnt > 0,n.server_id,null)) as svp_uv\n" +
                "from\n" +
                "(select   date_p,os_p,server_id,nvl(regexp_replace(channel,' ',''),'未找到渠道') as channel\n" +
                "from stat_sdk_parquet.sdk_odz_new_device_info\n" +
                "where date_p = '20181201'\n" +
                "  and os_p in ('ios','android')\n" +
                "  and app_key_p in ('F9CC8787275D8691','BDFAFB4ACC7885EE')\n" +
                "group by date_p,os_p,server_id,nvl(regexp_replace(channel,' ',''),'未找到渠道') \n" +
                ")n\n" +
                "left join\n" +
                "(\n" +
                "select  os_p,server_id,\n" +
                "  sum(case when function_type = 'gjmy' then select_pic_cnt when function_type in ('zp','ps','video','fxgj','film') then take_pic_cnt else 0 end) as tp_guanjian_cnt,\n" +
                "  sum(case when function_type != 'qita' then save_cnt else 0 end) as svp_cnt\n" +
                "from stat_meiyan_parquet.meiyan_odz_function_details_test\n" +
                "where date_p = '20181201'\n" +
                "  and os_p in ('ios','android')\n" +
                "group by os_p,server_id\n" +
                ")d on n.os_p = d.os_p and n.server_id = d.server_id\n" +
                "group by date_p,channel,n.os_p\n" +
                "grouping sets((date_p,channel,n.os_p),(date_p,n.os_p))\n" +
                " )a\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("/*+engine=spark*/\n" +
                "SELECT date_p, os_p, nvl(channel, '新增') AS channel\n" +
                "\t, new_uv, tp_uv, svp_uv\n" +
                "FROM (\n" +
                "\tSELECT date_p, channel, n.os_p AS os_p, count(DISTINCT n.server_id) AS new_uv\n" +
                "\t\t, count(DISTINCT if(tp_guanjian_cnt > 0, n.server_id, NULL)) AS tp_uv\n" +
                "\t\t, count(DISTINCT if(svp_cnt > 0, n.server_id, NULL)) AS svp_uv\n" +
                "\tFROM (\n" +
                "\t\tSELECT date_p, os_p, server_id\n" +
                "\t\t\t, nvl(regexp_replace(channel, ' ', ''), '未找到渠道') AS channel\n" +
                "\t\tFROM stat_sdk_parquet.sdk_odz_new_device_info\n" +
                "\t\tWHERE date_p = '20181201'\n" +
                "\t\t\tAND os_p IN ('ios', 'android')\n" +
                "\t\t\tAND app_key_p IN ('F9CC8787275D8691', 'BDFAFB4ACC7885EE')\n" +
                "\t\tGROUP BY date_p, os_p, server_id, nvl(regexp_replace(channel, ' ', ''), '未找到渠道')\n" +
                "\t) n\n" +
                "\t\tLEFT JOIN (\n" +
                "\t\t\tSELECT os_p, server_id\n" +
                "\t\t\t\t, sum(CASE \n" +
                "\t\t\t\t\tWHEN function_type = 'gjmy' THEN select_pic_cnt\n" +
                "\t\t\t\t\tWHEN function_type IN ('zp', 'ps', 'video', 'fxgj', 'film') THEN take_pic_cnt\n" +
                "\t\t\t\t\tELSE 0\n" +
                "\t\t\t\tEND) AS tp_guanjian_cnt\n" +
                "\t\t\t\t, sum(CASE \n" +
                "\t\t\t\t\tWHEN function_type != 'qita' THEN save_cnt\n" +
                "\t\t\t\t\tELSE 0\n" +
                "\t\t\t\tEND) AS svp_cnt\n" +
                "\t\t\tFROM stat_meiyan_parquet.meiyan_odz_function_details_test\n" +
                "\t\t\tWHERE date_p = '20181201'\n" +
                "\t\t\t\tAND os_p IN ('ios', 'android')\n" +
                "\t\t\tGROUP BY os_p, server_id\n" +
                "\t\t) d\n" +
                "\t\tON n.os_p = d.os_p\n" +
                "\t\t\tAND n.server_id = d.server_id\n" +
                "\tGROUP BY date_p, channel, n.os_p\n" +
                "\t\tGROUPING SETS ((date_p, channel, n.os_p), (date_p, n.os_p))\n" +
                ") a", stmt.toString());

        System.out.println(stmt.toString());
    }



    public void testRemoveBackQuoteFromSql() {
        String ret = SQLUtils.parseSingleStatement("select * from `hello`.`world`", DbType.mysql, SQLParserFeature.IgnoreNameQuotes).toString();
        assertEquals(
                "SELECT *\n" +
                        "FROM hello.world",
                ret
        );
    }
}