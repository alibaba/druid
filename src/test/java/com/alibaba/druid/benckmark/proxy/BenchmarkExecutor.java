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

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class BenchmarkExecutor {

    private int                       loopCount    = 1000;
    private int                       executeCount = 10;

    private final List<SQLExecutor>   sqlExecList  = new ArrayList<SQLExecutor>();
    private final List<BenchmarkCase> caseList     = new ArrayList<BenchmarkCase>();

    public int getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(int executeCount) {
        this.executeCount = executeCount;
    }

    public List<BenchmarkCase> getCaseList() {
        return caseList;
    }

    public List<SQLExecutor> getSqlExecutors() {
        return sqlExecList;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public void execute() {
        for (BenchmarkCase benchmarkCase : caseList) {
            Map<SQLExecutor, List<Result>> sumList = new LinkedHashMap<SQLExecutor, List<Result>>();
            for (SQLExecutor sqlExec : sqlExecList) {
                // 预先执行一次
                {
                    Result result = executeLoop(sqlExec, benchmarkCase);
                    handleResult(sqlExec, result);
                    System.out.println();
                }

                List<Result> resultList = new ArrayList<Result>();

                for (int i = 0; i < executeCount; ++i) {
                    Result result = executeLoop(sqlExec, benchmarkCase);
                    resultList.add(result);
                    handleResult(sqlExec, result);
                }
                System.out.println();
                sumList.put(sqlExec, resultList);
            }

            for (Map.Entry<SQLExecutor, List<Result>> entry : sumList.entrySet()) {
                handleResultSummary(entry.getKey(), benchmarkCase, entry.getValue());
            }
            System.out.println();

        }
    }

    public void handleResultSummary(SQLExecutor sqlExec, BenchmarkCase benchmarkCase, List<Result> resultList) {
        int millis = 0;
        int youngGC = 0;
        int fullGC = 0;
        for (Result result : resultList) {
            millis += result.getMillis();
            youngGC += result.getYoungGC();
            fullGC += result.getFullGC();
        }

        NumberFormat format = NumberFormat.getInstance();
        System.out.println("SUM\t" + benchmarkCase.getName() + "\t" + sqlExec.getName() + "\t" + format.format(millis)
                           + "\tYoungGC " + youngGC + "\tFullGC " + fullGC);
    }

    public void handleResult(SQLExecutor sqlExec, Result result) {
        if (result.getError() != null) {
            result.getError().printStackTrace();
            return;
        }
        NumberFormat format = NumberFormat.getInstance();
        System.out.println(result.getName() + "\t" + sqlExec.getName() + "\t" + format.format(result.getMillis())
                           + "\tYoungGC " + result.getYoungGC() + "\tFullGC " + result.getFullGC());
    }

    private Result executeLoop(SQLExecutor sqlExec, BenchmarkCase benchmarkCase) {
        try {
            benchmarkCase.setUp(sqlExec);
        } catch (Exception e) {
            throw new RuntimeException("setup error", e);
        }

        long startMillis = System.currentTimeMillis();
        long startYoungGC = getYoungGC();
        long startFullGC = getFullGC();

        Throwable error = null;
        try {
            for (int i = 0; i < loopCount; ++i) {
                benchmarkCase.execute(sqlExec);
            }

        } catch (Throwable e) {
            error = e;
        }
        long time = System.currentTimeMillis() - startMillis;
        long youngGC = getYoungGC() - startYoungGC;
        long fullGC = getFullGC() - startFullGC;

        Result result = new Result();
        result.setName(benchmarkCase.getName());
        result.setMillis(time);
        result.setYoungGC(youngGC);
        result.setFullGC(fullGC);
        result.setError(error);

        try {
            benchmarkCase.tearDown(sqlExec);
        } catch (Exception e) {
            throw new RuntimeException("tearDown error", e);
        }

        return result;
    }

    public long getYoungGC() {
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName;
            if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=ParNew"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=ParNew");
            } else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=Copy"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=Copy");
            } else {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=PS Scavenge");
            }

            return (Long) mbeanServer.getAttribute(objectName, "CollectionCount");
        } catch (Exception e) {
            throw new RuntimeException("error");
        }
    }

    public long getFullGC() {
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName;

            if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep");
            } else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=MarkSweepCompact"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=MarkSweepCompact");
            } else {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=PS MarkSweep");
            }

            return (Long) mbeanServer.getAttribute(objectName, "CollectionCount");
        } catch (Exception e) {
            throw new RuntimeException("error");
        }
    }

    public static class Result {

        private String    name;
        private long      millis;
        private long      youngGC;
        private long      fullGC;
        private Throwable error;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getMillis() {
            return millis;
        }

        public void setMillis(long millis) {
            this.millis = millis;
        }

        public long getYoungGC() {
            return youngGC;
        }

        public void setYoungGC(long youngGC) {
            this.youngGC = youngGC;
        }

        public long getFullGC() {
            return fullGC;
        }

        public void setFullGC(long fullGC) {
            this.fullGC = fullGC;
        }

        public Throwable getError() {
            return error;
        }

        public void setError(Throwable error) {
            this.error = error;
        }

    }
}
