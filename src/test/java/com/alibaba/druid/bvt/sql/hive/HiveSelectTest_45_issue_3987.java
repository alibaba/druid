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
import junit.framework.TestCase;

import java.util.List;

public class HiveSelectTest_45_issue_3987 extends TestCase {

    public void test_0() throws Exception {
        String sql = "select id,number_id,parent_id,layer_id,alias,name \n" +
                "from (select id,number_id,parent_id,layer_id,alias,name,row_number() over(distribute by number_id sort by create_time desc,id desc) rownum from hdw_ods.ods_my_coredata__dts_device_category where pdate ='') m where m.rownum = 1";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive);
        SQLStatement stmt =  statementList.get(0);

        assertEquals("SELECT id, number_id, parent_id, layer_id, alias\n" +
                "\t, name\n" +
                "FROM (\n" +
                "\tSELECT id, number_id, parent_id, layer_id, alias\n" +
                "\t\t, name, row_number() OVER (DISTRIBUTE BY number_idSORT BY number_id) AS rownum\n" +
                "\tFROM hdw_ods.ods_my_coredata__dts_device_category\n" +
                "\tWHERE pdate = ''\n" +
                ") m\n" +
                "WHERE m.rownum = 1", stmt.toString());

    }
}
