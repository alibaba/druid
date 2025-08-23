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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import junit.framework.TestCase;


import com.alibaba.druid.stat.JdbcSqlStat;

public class SqlStatisticTest extends TestCase {
    public void test_sql_stat() throws Exception {
        JdbcSqlStat stat = new JdbcSqlStat("SELECT * FROM t_user");
        assertEquals(null, stat.getExecuteLastStartTime());
        assertEquals(null, stat.getExecuteNanoSpanMaxOccurTime());
        assertEquals(null, stat.getExecuteErrorLastTime());

        stat.error(new Exception());
        assertNotNull(stat.getExecuteErrorLast());
        assertNotNull(stat.getExecuteErrorLastTime());
    }
}
