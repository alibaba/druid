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


public class MySqlSelectTest_288 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO hive.oa1878750739446285_stat_meiyan_parquet.meiyan_odz_daily_user\n" +
                "(SELECT 'android', server_id, imei, app_version\n" +
                "\t, lower(sdk_channel) AS channel, city_id, province_id\n" +
                "\t, country_id, '' AS device_brand, device_model, resolution\n" +
                "\t, CASE\n" +
                "\t\tWHEN is_app_new = 2 THEN 'new'\n" +
                "\t\tELSE 'active'\n" +
                "\tEND AS user_type\n" +
                "FROM (\n" +
                "\tSELECT t.server_id\n" +
                "\t\t, if(current_imei IS NULL\n" +
                "\t\t\tAND length(trim(imei)) > 10, imei, current_imei) AS imei\n" +
                "\t\t, max(app_version) OVER (PARTITION BY t.server_id ) AS app_version, sdk_channel\n" +
                "\t\t, row_number() OVER (PARTITION BY t.server_id ORDER BY time DESC) AS row_num, city_id, province_id\n" +
                "\t\t, country_id, device_model, resolution\n" +
                "\t\t, CASE\n" +
                "\t\t\tWHEN t1.server_id IS NOT NULL THEN 2\n" +
                "\t\t\tELSE 1\n" +
                "\t\tEND AS is_app_new, 0 AS is_back, 0 AS is_first_launch\n" +
                "\tFROM (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM hive.oa1878750739446285_bigdataop_parquet.stat_sdk_android\n" +
                "\t\tWHERE date_p = '20181201'\n" +
                "\t\t\tAND app_key_p = 'F9CC8787275D8691'\n" +
                "\t\t\tAND app_version IS NOT NULL\n" +
                "\t\t\tAND server_id IS NOT NULL\n" +
                "\t\t\tAND event_id = 'app_start'\n" +
                "\t\t\tAND app_version NOT REGEXP '[a-zA-Z]'\n" +
                "\t) t\n" +
                "\t\tLEFT JOIN (\n" +
                "\t\t\tSELECT server_id, date_p\n" +
                "\t\t\tFROM hive.oa1878750739446285_stat_sdk_parquet.sdk_odz_new_device_info\n" +
                "\t\t\tWHERE date_p = 20181201\n" +
                "\t\t\t\tAND os_p = 'android'\n" +
                "\t\t\t\tAND app_key_p = 'F9CC8787275D8691'\n" +
                "\t\t) t1\n" +
                "\t\tON t.server_id = t1.server_id\n" +
                "\tWHERE row_num = 1\n" +
                ") t)\n" +
                "UNION ALL\n" +
                "(SELECT 'ios', d.server_id, idfa, app_version\n" +
                "\t, COALESCE(last_channel, channel) AS channel, city_id\n" +
                "\t, province_id, country_id, '' AS device_brand, device_model, resolution\n" +
                "\t, CASE\n" +
                "\t\tWHEN is_app_new = 2 THEN 'new'\n" +
                "\t\tELSE 'active'\n" +
                "\tEND AS user_type\n" +
                "FROM (\n" +
                "\tSELECT server_id, idfa, app_version, city_id, province_id\n" +
                "\t\t, country_id, device_model, resolution, is_app_new\n" +
                "\tFROM (\n" +
                "\t\tSELECT t.server_id, idfa, max(app_version) OVER (PARTITION BY t.server_id ) AS app_version\n" +
                "\t\t\t, row_number() OVER (PARTITION BY t.server_id ORDER BY time DESC) AS row_num, city_id, province_id\n" +
                "\t\t\t, country_id, device_model, resolution\n" +
                "\t\t\t, CASE\n" +
                "\t\t\t\tWHEN t1.server_id IS NOT NULL THEN 2\n" +
                "\t\t\t\tELSE 1\n" +
                "\t\t\tEND AS is_app_new\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT *\n" +
                "\t\t\tFROM hive.oa1878750739446285_bigdataop_parquet.stat_sdk_ios\n" +
                "\t\t\tWHERE date_p = '20181201'\n" +
                "\t\t\t\tAND app_key_p = 'BDFAFB4ACC7885EE'\n" +
                "\t\t\t\tAND app_version IS NOT NULL\n" +
                "\t\t\t\tAND event_id = 'app_start'\n" +
                "\t\t\t\tAND app_version NOT REGEXP '[a-zA-Z]'\n" +
                "\t\t) t\n" +
                "\t\t\tLEFT JOIN (\n" +
                "\t\t\t\tSELECT server_id, date_p\n" +
                "\t\t\t\tFROM hive.oa1878750739446285_stat_sdk_parquet.sdk_odz_new_device_info\n" +
                "\t\t\t\tWHERE date_p = 20181201\n" +
                "\t\t\t\t\tAND os_p = 'ios'\n" +
                "\t\t\t\t\tAND app_key_p = 'BDFAFB4ACC7885EE'\n" +
                "\t\t\t) t1\n" +
                "\t\t\tON t.server_id = t1.server_id\n" +
                "\t) t\n" +
                "\tWHERE row_num = 1\n" +
                ") d\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT server_id\n" +
                "\t\t\t, if(channel IS NULL\n" +
                "\t\t\t\tOR regexp_replace(channel, ' ', '') = '', 'unknown', lower(regexp_replace(channel, ' ', ''))) AS channel\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT server_id, channel, row_number() OVER (PARTITION BY server_id ORDER BY time DESC) AS row_num\n" +
                "\t\t\tFROM hive.oa1878750739446285_bigdataop_parquet.sdk_channel_data\n" +
                "\t\t\tWHERE type_p = 'ios'\n" +
                "\t\t\t\tAND app_key = 'BDFAFB4ACC7885EE'\n" +
                "\t\t\t\tAND channel IS NOT NULL\n" +
                "\t\t) t\n" +
                "\t\tWHERE row_num = 1\n" +
                "\t) tt\n" +
                "\tON d.server_id = tt.server_id\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT server_id, last_channel\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT server_id\n" +
                "\t\t\t\t, if(channel IS NULL\n" +
                "\t\t\t\t\tOR regexp_replace(channel, ' ', '') = '', 'unknown', lower(regexp_replace(channel, ' ', ''))) AS last_channel\n" +
                "\t\t\t\t, row_number() OVER (PARTITION BY server_id ORDER BY receive_time DESC) AS row_num\n" +
                "\t\t\tFROM hive.oa1878750739446285_stat_sdk_parquet.sdk_odz_back_visit_data\n" +
                "\t\t\tWHERE type_p = 'ios'\n" +
                "\t\t\t\tAND app_key = 'BDFAFB4ACC7885EE'\n" +
                "\t\t\t\tAND channel IS NOT NULL\n" +
                "\t\t) r\n" +
                "\t\tWHERE row_num = 1\n" +
                "\t) back\n" +
                "\tON d.server_id = back.server_id)";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("INSERT INTO hive.oa1878750739446285_stat_meiyan_parquet.meiyan_odz_daily_user\n" +
                "(SELECT 'android', server_id, imei, app_version\n" +
                "\t, lower(sdk_channel) AS channel, city_id, province_id\n" +
                "\t, country_id, '' AS device_brand, device_model, resolution\n" +
                "\t, CASE \n" +
                "\t\tWHEN is_app_new = 2 THEN 'new'\n" +
                "\t\tELSE 'active'\n" +
                "\tEND AS user_type\n" +
                "FROM (\n" +
                "\tSELECT t.server_id\n" +
                "\t\t, if(current_imei IS NULL\n" +
                "\t\t\tAND length(trim(imei)) > 10, imei, current_imei) AS imei\n" +
                "\t\t, max(app_version) OVER (PARTITION BY t.server_id ) AS app_version, sdk_channel\n" +
                "\t\t, row_number() OVER (PARTITION BY t.server_id ORDER BY time DESC) AS row_num, city_id, province_id\n" +
                "\t\t, country_id, device_model, resolution\n" +
                "\t\t, CASE \n" +
                "\t\t\tWHEN t1.server_id IS NOT NULL THEN 2\n" +
                "\t\t\tELSE 1\n" +
                "\t\tEND AS is_app_new, 0 AS is_back, 0 AS is_first_launch\n" +
                "\tFROM (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM hive.oa1878750739446285_bigdataop_parquet.stat_sdk_android\n" +
                "\t\tWHERE date_p = '20181201'\n" +
                "\t\t\tAND app_key_p = 'F9CC8787275D8691'\n" +
                "\t\t\tAND app_version IS NOT NULL\n" +
                "\t\t\tAND server_id IS NOT NULL\n" +
                "\t\t\tAND event_id = 'app_start'\n" +
                "\t\t\tAND app_version NOT REGEXP '[a-zA-Z]'\n" +
                "\t) t\n" +
                "\t\tLEFT JOIN (\n" +
                "\t\t\tSELECT server_id, date_p\n" +
                "\t\t\tFROM hive.oa1878750739446285_stat_sdk_parquet.sdk_odz_new_device_info\n" +
                "\t\t\tWHERE date_p = 20181201\n" +
                "\t\t\t\tAND os_p = 'android'\n" +
                "\t\t\t\tAND app_key_p = 'F9CC8787275D8691'\n" +
                "\t\t) t1\n" +
                "\t\tON t.server_id = t1.server_id\n" +
                "\tWHERE row_num = 1\n" +
                ") t)\n" +
                "UNION ALL\n" +
                "(SELECT 'ios', d.server_id, idfa, app_version\n" +
                "\t, COALESCE(last_channel, channel) AS channel, city_id\n" +
                "\t, province_id, country_id, '' AS device_brand, device_model, resolution\n" +
                "\t, CASE \n" +
                "\t\tWHEN is_app_new = 2 THEN 'new'\n" +
                "\t\tELSE 'active'\n" +
                "\tEND AS user_type\n" +
                "FROM (\n" +
                "\tSELECT server_id, idfa, app_version, city_id, province_id\n" +
                "\t\t, country_id, device_model, resolution, is_app_new\n" +
                "\tFROM (\n" +
                "\t\tSELECT t.server_id, idfa, max(app_version) OVER (PARTITION BY t.server_id ) AS app_version\n" +
                "\t\t\t, row_number() OVER (PARTITION BY t.server_id ORDER BY time DESC) AS row_num, city_id, province_id\n" +
                "\t\t\t, country_id, device_model, resolution\n" +
                "\t\t\t, CASE \n" +
                "\t\t\t\tWHEN t1.server_id IS NOT NULL THEN 2\n" +
                "\t\t\t\tELSE 1\n" +
                "\t\t\tEND AS is_app_new\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT *\n" +
                "\t\t\tFROM hive.oa1878750739446285_bigdataop_parquet.stat_sdk_ios\n" +
                "\t\t\tWHERE date_p = '20181201'\n" +
                "\t\t\t\tAND app_key_p = 'BDFAFB4ACC7885EE'\n" +
                "\t\t\t\tAND app_version IS NOT NULL\n" +
                "\t\t\t\tAND event_id = 'app_start'\n" +
                "\t\t\t\tAND app_version NOT REGEXP '[a-zA-Z]'\n" +
                "\t\t) t\n" +
                "\t\t\tLEFT JOIN (\n" +
                "\t\t\t\tSELECT server_id, date_p\n" +
                "\t\t\t\tFROM hive.oa1878750739446285_stat_sdk_parquet.sdk_odz_new_device_info\n" +
                "\t\t\t\tWHERE date_p = 20181201\n" +
                "\t\t\t\t\tAND os_p = 'ios'\n" +
                "\t\t\t\t\tAND app_key_p = 'BDFAFB4ACC7885EE'\n" +
                "\t\t\t) t1\n" +
                "\t\t\tON t.server_id = t1.server_id\n" +
                "\t) t\n" +
                "\tWHERE row_num = 1\n" +
                ") d\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT server_id\n" +
                "\t\t\t, if(channel IS NULL\n" +
                "\t\t\t\tOR regexp_replace(channel, ' ', '') = '', 'unknown', lower(regexp_replace(channel, ' ', ''))) AS channel\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT server_id, channel, row_number() OVER (PARTITION BY server_id ORDER BY time DESC) AS row_num\n" +
                "\t\t\tFROM hive.oa1878750739446285_bigdataop_parquet.sdk_channel_data\n" +
                "\t\t\tWHERE type_p = 'ios'\n" +
                "\t\t\t\tAND app_key = 'BDFAFB4ACC7885EE'\n" +
                "\t\t\t\tAND channel IS NOT NULL\n" +
                "\t\t) t\n" +
                "\t\tWHERE row_num = 1\n" +
                "\t) tt\n" +
                "\tON d.server_id = tt.server_id\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT server_id, last_channel\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT server_id\n" +
                "\t\t\t\t, if(channel IS NULL\n" +
                "\t\t\t\t\tOR regexp_replace(channel, ' ', '') = '', 'unknown', lower(regexp_replace(channel, ' ', ''))) AS last_channel\n" +
                "\t\t\t\t, row_number() OVER (PARTITION BY server_id ORDER BY receive_time DESC) AS row_num\n" +
                "\t\t\tFROM hive.oa1878750739446285_stat_sdk_parquet.sdk_odz_back_visit_data\n" +
                "\t\t\tWHERE type_p = 'ios'\n" +
                "\t\t\t\tAND app_key = 'BDFAFB4ACC7885EE'\n" +
                "\t\t\t\tAND channel IS NOT NULL\n" +
                "\t\t) r\n" +
                "\t\tWHERE row_num = 1\n" +
                "\t) back\n" +
                "\tON d.server_id = back.server_id)", stmt.toString());
    }



}