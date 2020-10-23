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


public class MySqlSelectTest_279 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select l.*\n" +
                "from (select\n" +
                "        h.name COLLATE utf8_unicode_ci            '供应商名称',\n" +
                "        contact_name COLLATE utf8_unicode_ci      '联系人',\n" +
                "        contact_phone COLLATE utf8_unicode_ci     '电话',\n" +
                "        address COLLATE utf8_unicode_ci           '地址',\n" +
                "        protocol_district COLLATE utf8_unicode_ci '区划'\n" +
                "      from hotel_base_protocol h\n" +
                "      where h.is_in_time = 1 and h.status = 6 and h.enabled = 1 and h.is_internal_dept = 0\n" +
                "      UNION\n" +
                "      select\n" +
                "        supplier_name COLLATE utf8_unicode_ci   '供应商名称',\n" +
                "        i.contact_name COLLATE utf8_unicode_ci  '联系人',\n" +
                "        i.contact_phone COLLATE utf8_unicode_ci '电话',\n" +
                "        i.contact_addr COLLATE utf8_unicode_ci  '地址',\n" +
                "        district_code COLLATE utf8_unicode_ci   '区划'\n" +
                "      from insurance_protocol_t i\n" +
                "      where i.status = 6 and i.in_service = 1 and i.in_time = 1\n" +
                "      UNION\n" +
                "      select\n" +
                "        supplier_name '供应商名称',\n" +
                "        coordinator   '联系人',\n" +
                "        cellphone     '电话',\n" +
                "        address       '地址',\n" +
                "        region        '区划'\n" +
                "      from vehicle_repairment_protocol) l\n" +
                "order by '区划'";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT l.*\n" +
                "FROM (\n" +
                "\tSELECT h.name COLLATE utf8_unicode_ci AS '供应商名称', contact_name COLLATE utf8_unicode_ci AS '联系人'\n" +
                "\t\t, contact_phone COLLATE utf8_unicode_ci AS '电话', address COLLATE utf8_unicode_ci AS '地址'\n" +
                "\t\t, protocol_district COLLATE utf8_unicode_ci AS '区划'\n" +
                "\tFROM hotel_base_protocol h\n" +
                "\tWHERE h.is_in_time = 1\n" +
                "\t\tAND h.status = 6\n" +
                "\t\tAND h.enabled = 1\n" +
                "\t\tAND h.is_internal_dept = 0\n" +
                "\tUNION\n" +
                "\tSELECT supplier_name COLLATE utf8_unicode_ci AS '供应商名称', i.contact_name COLLATE utf8_unicode_ci AS '联系人'\n" +
                "\t\t, i.contact_phone COLLATE utf8_unicode_ci AS '电话', i.contact_addr COLLATE utf8_unicode_ci AS '地址'\n" +
                "\t\t, district_code COLLATE utf8_unicode_ci AS '区划'\n" +
                "\tFROM insurance_protocol_t i\n" +
                "\tWHERE i.status = 6\n" +
                "\t\tAND i.in_service = 1\n" +
                "\t\tAND i.in_time = 1\n" +
                "\tUNION\n" +
                "\tSELECT supplier_name AS '供应商名称', coordinator AS '联系人', cellphone AS '电话', address AS '地址', region AS '区划'\n" +
                "\tFROM vehicle_repairment_protocol\n" +
                ") l\n" +
                "ORDER BY '区划'", stmt.toString());
    }



}