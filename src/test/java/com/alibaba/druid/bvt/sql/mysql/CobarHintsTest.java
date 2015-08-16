/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql;

import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcUtils;

public class CobarHintsTest extends TestCase {
    public void test_0 () throws Exception {
        String sql = "/*!cobar: select,4,ireport.dm_mdm_mem_prod_noeff_sdt0.admin_member_seq=45654723*/ " +
        		"select  product_id, noeff_days,total_cnt from (" +
        		"select   product_id," +
        		"             noeff_days," +
        		"             count(*) over()  as total_cnt                        " +
        		"       from   (                   " +
        		"          select   product_id," +
        		"                   noeff_days               " +
        		"                   from ireport.dm_mdm_mem_prod_noeff_sdt0" +
        		"                   where admin_member_seq = 45654723" +
        		") b    Order by       product_id desc  ) a limit 25 offset (1-1)*20";

        String mergedSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.POSTGRESQL);
        System.out.println(mergedSql);
    }
}
