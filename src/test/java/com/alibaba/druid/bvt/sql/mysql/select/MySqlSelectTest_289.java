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


public class MySqlSelectTest_289 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT supplier AS 'supplier', offline_num AS 'offlineCount', online_num AS 'onlineCount', current_all_num AS 'currentAllCount'\n" +
                "\t, (\n" +
                "\t\tSELECT COUNT(1) AS all_num\n" +
                "\t\tFROM cnt_iot_device\n" +
                "\t\tWHERE status IN (1, 0)\n" +
                "\t\t\tAND tenant = 'cainiao'\n" +
                "\t\t\tAND supplier != 'TS'\n" +
                "\t\t\tAND supplier IN ('DC', 'HS', 'ZL')\n" +
                "\t) AS 'allDeviceCount'\n" +
                "FROM (\n" +
                "\tSELECT t_offline.supplier AS 'supplier', t_offline.offline_num AS 'offline_num', t_online.online_num AS 'online_num', t_offline.offline_num + t_online.online_num AS 'current_all_num'\n" +
                "\tFROM (\n" +
                "\t\tSELECT supplier, COUNT(1) AS offline_num\n" +
                "\t\tFROM cnt_iot_device\n" +
                "\t\tWHERE status IN (1)\n" +
                "\t\t\tAND tenant = 'cainiao'\n" +
                "\t\t\tAND supplier != 'TS'\n" +
                "\t\t\tAND supplier IN ('DC', 'HS', 'ZL')\n" +
                "\t\tGROUP BY supplier\n" +
                "\t\tORDER BY 1, 2\n" +
                "\t) t_offline\n" +
                "\t\tLEFT JOIN (\n" +
                "\t\t\tSELECT supplier, online_num\n" +
                "\t\t\tFROM (\n" +
                "\t\t\t\tSELECT supplier, COUNT(1) AS online_num\n" +
                "\t\t\t\tFROM cnt_iot_device\n" +
                "\t\t\t\tWHERE status IN (0)\n" +
                "\t\t\t\t\tAND tenant = 'cainiao'\n" +
                "\t\t\t\t\tAND supplier != 'TS'\n" +
                "\t\t\t\t\tAND supplier IN ('DC', 'HS', 'ZL')\n" +
                "\t\t\t\tGROUP BY supplier\n" +
                "\t\t\t\tORDER BY 1, 2\n" +
                "\t\t\t)\n" +
                "\t\t\tORDER BY 1, 2\n" +
                "\t\t) t_online\n" +
                "\t\tON t_offline.supplier = t_online.supplier\n" +
                "\tORDER BY 1, 2, 3, 4\n" +
                ") t1\n" +
                "ORDER BY 1, 2, 3, 4, 5;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT supplier AS \"supplier\", offline_num AS \"offlineCount\", online_num AS \"onlineCount\", current_all_num AS \"currentAllCount\"\n" +
                "\t, (\n" +
                "\t\tSELECT COUNT(1) AS all_num\n" +
                "\t\tFROM cnt_iot_device\n" +
                "\t\tWHERE status IN (1, 0)\n" +
                "\t\t\tAND tenant = 'cainiao'\n" +
                "\t\t\tAND supplier != 'TS'\n" +
                "\t\t\tAND supplier IN ('DC', 'HS', 'ZL')\n" +
                "\t) AS \"allDeviceCount\"\n" +
                "FROM (\n" +
                "\tSELECT t_offline.supplier AS \"supplier\", t_offline.offline_num AS \"offline_num\", t_online.online_num AS \"online_num\", t_offline.offline_num + t_online.online_num AS \"current_all_num\"\n" +
                "\tFROM (\n" +
                "\t\tSELECT supplier, COUNT(1) AS offline_num\n" +
                "\t\tFROM cnt_iot_device\n" +
                "\t\tWHERE status IN (1)\n" +
                "\t\t\tAND tenant = 'cainiao'\n" +
                "\t\t\tAND supplier != 'TS'\n" +
                "\t\t\tAND supplier IN ('DC', 'HS', 'ZL')\n" +
                "\t\tGROUP BY supplier\n" +
                "\t\tORDER BY 1, 2\n" +
                "\t) t_offline\n" +
                "\t\tLEFT JOIN (\n" +
                "\t\t\tSELECT supplier, online_num\n" +
                "\t\t\tFROM (\n" +
                "\t\t\t\tSELECT supplier, COUNT(1) AS online_num\n" +
                "\t\t\t\tFROM cnt_iot_device\n" +
                "\t\t\t\tWHERE status IN (0)\n" +
                "\t\t\t\t\tAND tenant = 'cainiao'\n" +
                "\t\t\t\t\tAND supplier != 'TS'\n" +
                "\t\t\t\t\tAND supplier IN ('DC', 'HS', 'ZL')\n" +
                "\t\t\t\tGROUP BY supplier\n" +
                "\t\t\t\tORDER BY 1, 2\n" +
                "\t\t\t)\n" +
                "\t\t\tORDER BY 1, 2\n" +
                "\t\t) t_online\n" +
                "\t\tON t_offline.supplier = t_online.supplier\n" +
                "\tORDER BY 1, 2, 3, 4\n" +
                ") t1\n" +
                "ORDER BY 1, 2, 3, 4, 5;", stmt.toString());
    }



}