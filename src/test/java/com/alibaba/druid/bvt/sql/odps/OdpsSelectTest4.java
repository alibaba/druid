/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsSelectTest4 extends TestCase {

    public void test_distribute_by() throws Exception {
        String sql = "select total_day_cnt * EXP(-datediff(to_date('20150819', 'yyyymmdd'), last_time, 'dd') / 60) from dual";//
        Assert.assertEquals("SELECT total_day_cnt * EXP(-datediff(TO_DATE('20150819', 'yyyymmdd'), last_time, 'dd') / 60)"
                + "\nFROM dual", SQLUtils.formatOdps(sql));
    }
    
    public void test_distribute_by_lcase() throws Exception {
        String sql = "select total_day_cnt * EXP(-datediff(to_date('20150819', 'yyyymmdd'), last_time, 'dd') / 60) from dual";//
        
        Assert.assertEquals("select total_day_cnt * EXP(-datediff(to_date('20150819', 'yyyymmdd'), last_time, 'dd') / 60)"
                + "\nfrom dual", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
