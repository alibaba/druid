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
package com.alibaba.druid.benckmark.wall;

import junit.framework.TestCase;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallBenchmarkTest_mysql extends TestCase {

    WallProvider            provider = new MySqlWallProvider();

    public final static int COUNT    = 1000 * 1000;

    public void test_0() throws Exception {
        String sql = "SELECT t1.department_id, t2.*\n" + //
                     "FROM hr_info t1, x2 t2\n" + //
                     "WHERE t2.department_id = t1.department_id";
        for (int i = 0; i < 10; ++i) {
            provider.clearCache();
            long startMillis = System.currentTimeMillis();
            perf(sql);
            long millis = System.currentTimeMillis() - startMillis;
            System.out.println("millis : " + millis);
        }
    }

    public void perf(String sql) {
        for (int i = 0; i < COUNT; ++i) {
        	String text = sql + " AND FID = " + i;
            provider.check(text);
        }
    }
}
