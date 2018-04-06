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
package com.alibaba.druid.bvt.proxy;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.stat.JdbcConnectionStat;

public class ConnectionStatisticTest extends TestCase {

    public void test_connection_stat() throws Exception {
        JdbcConnectionStat.Entry stat = new JdbcConnectionStat.Entry(null, 1001L);
        Assert.assertEquals(null, stat.getEstablishTime());
        Assert.assertEquals(null, stat.getConnectStackTrace());
        Assert.assertEquals(null, stat.getLastStatementStatckTrace());

        stat.setLastStatementStatckTrace(new Exception());
        Assert.assertNotNull(stat.getLastStatementStatckTrace());

        stat.error(new Exception());
        Assert.assertNotNull(stat.getLastErrorTime());
    }
}
