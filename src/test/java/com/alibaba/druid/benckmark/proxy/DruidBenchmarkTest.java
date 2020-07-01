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
package com.alibaba.druid.benckmark.proxy;

import junit.framework.TestCase;

import com.alibaba.druid.benckmark.proxy.sqlcase.SelectNow;

public class DruidBenchmarkTest extends TestCase {

    public void test_druid_benchmark() throws Exception {

        BenchmarkExecutor executor = new BenchmarkExecutor();
        executor.getSqlExecutors().add(createExecutorDirect());
        executor.getSqlExecutors().add(createExecutorDruid());

        executor.setExecuteCount(10);
        executor.setLoopCount(1000 * 100);
        executor.getCaseList().add(new SelectNow());
        // executor.getCaseList().add(new SelectSysUser());
        // executor.getCaseList().add(new Select1());
        // executor.getCaseList().add(new SelectEmptyTable());

        executor.execute();
    }

    public DirectSQLExecutor createExecutorDirect() {
        String name = "direct";
        String jdbcUrl = "jdbc:mysql://a.b.c.d/dragoon_v25masterdb?useUnicode=true&characterEncoding=UTF-8";
        String user = "dragoon25";
        String password = "dragoon25";
        return new DirectSQLExecutor(name, jdbcUrl, user, password);
    }

    public DirectSQLExecutor createExecutorDruid() {
        String name = "druid";
        String jdbcUrl = "jdbc:wrap-jdbc:filters=default:name=benchmark:jdbc:mysql://a.b.c.d/dragoon_v25masterdb?useUnicode=true&characterEncoding=UTF-8";
        String user = "dragoon25";
        String password = "dragoon25";
        return new DirectSQLExecutor(name, jdbcUrl, user, password);
    }
}
