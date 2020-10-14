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

import com.alibaba.druid.sql.MysqlTest;

public class MySqlSelectTest_311_issue
        extends MysqlTest {

    public void test_join() throws Exception {
        String sql = "create view 054839e135a0be669145f2b90c4e8fcf5af46b as select 1";

//        SQLStatement statement = SQLUtils.parseSingleMysqlStatement(sql);
//        assertEquals("SELECT *\n" +
//                "FROM abif.mob_rpt_retail_touch_d a\n" +
//                "\tJOIN (\n" +
//                "\t\tSELECT DISTINCT split_value, split_index\n" +
//                "\t\tFROM abif.ysf_mercury_split_index_mapping\n" +
//                "\t\tWHERE split_key = 'retailid'\n" +
//                "\t\t\tAND rule_id = '1'\n" +
//                "\t) b\n" +
//                "\tON a.retail_id = b.split_value\n" +
//                "\t\tAND a.ds = 20200201\n" +
//                "\t\tAND a.aid IS NOT NULL", statement.toString());
    }
}