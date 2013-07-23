/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.wall;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class WallSqlStat {

    private volatile long                            executeCount;
    private volatile long                            effectRowCount;

    final static AtomicLongFieldUpdater<WallSqlStat> executeCountUpdater   = AtomicLongFieldUpdater.newUpdater(WallSqlStat.class,
                                                                                                               "executeCount");
    final static AtomicLongFieldUpdater<WallSqlStat> effectRowCountUpdater = AtomicLongFieldUpdater.newUpdater(WallSqlStat.class,
                                                                                                               "effectRowCount");
    
    private final Map<String, WallSqlTableStat>      tableStats;

    private final List<Violation>                    violations;

    private final Map<String, WallSqlFunctionStat>   functionStats;

    private final boolean                            syntaxError;

    public WallSqlStat(Map<String, WallSqlTableStat> tableStats, Map<String, WallSqlFunctionStat> functionStats,
                       boolean syntaxError){
        this(tableStats, functionStats, Collections.<Violation> emptyList(), syntaxError);
    }

    public WallSqlStat(Map<String, WallSqlTableStat> tableStats, Map<String, WallSqlFunctionStat> functionStats,
                       List<Violation> violations, boolean syntaxError){
        this.violations = violations;
        this.tableStats = tableStats;
        this.functionStats = functionStats;
        this.syntaxError = syntaxError;
    }

    public long incrementAndGetExecuteCount() {
        return executeCountUpdater.incrementAndGet(this);
    }

    public long getExecuteCount() {
        return executeCount;
    }
    
    public long incrementAndGetEffectRowCount() {
        return effectRowCountUpdater.incrementAndGet(this);
    }
    
    public long addAndGetEffectRowCount(long delta) {
        return effectRowCountUpdater.addAndGet(this, delta);
    }
    
    public long getEffectRowCount() {
        return effectRowCount;
    }

    public Map<String, WallSqlTableStat> getTableStats() {
        return tableStats;
    }

    public Map<String, WallSqlFunctionStat> getFunctionStats() {
        return functionStats;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public boolean isSyntaxError() {
        return syntaxError;
    }
}
