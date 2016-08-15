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
package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsSelectTest3 extends TestCase {

    public void test_distribute_by() throws Exception {
        String sql = "select RANK() OVER (PARTITION BY ui ORDER BY duration DESC) rank from dual";//
        Assert.assertEquals("SELECT RANK() OVER (PARTITION BY ui ORDER BY duration DESC) AS rank"
                + "\nFROM dual", SQLUtils.formatOdps(sql));
    }
    

}
