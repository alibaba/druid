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


public class MySqlSelectTest_272 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select * from (select product from mysql_olap_t1 \n" +
                "where exists (\n" +
                "   select product, country_id , sum(profit) \n" +
                "   from mysql_olap_t1 as mysql_olap_t2 \n" +
                "   where mysql_olap_t1.product=mysql_olap_t2.product \n" +
                "   group by product, country_id with rollup having sum(profit) > 6000))tmp order by 1\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT product\n" +
                "\tFROM mysql_olap_t1\n" +
                "\tWHERE EXISTS (\n" +
                "\t\tSELECT product, country_id, sum(profit)\n" +
                "\t\tFROM mysql_olap_t1 mysql_olap_t2\n" +
                "\t\tWHERE mysql_olap_t1.product = mysql_olap_t2.product\n" +
                "\t\tGROUP BY product, country_id\n" +
                "\t\tHAVING sum(profit) > 6000 WITH ROLLUP\n" +
                "\t)\n" +
                ") tmp\n" +
                "ORDER BY 1", stmt.toString());
    }



}