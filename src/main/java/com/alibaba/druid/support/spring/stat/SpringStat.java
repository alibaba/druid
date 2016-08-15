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
package com.alibaba.druid.support.spring.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SpringStat {

    private ConcurrentMap<SpringMethodInfo, SpringMethodStat> methodStats = new ConcurrentHashMap<SpringMethodInfo, SpringMethodStat>(16, 0.75f, 1);

    public SpringStat(){

    }

    public void reset() {
        for (SpringMethodStat stat : methodStats.values()) {
            stat.reset();
        }
    }

    public SpringMethodStat getMethodStat(SpringMethodInfo methodInfo, boolean create) {
        SpringMethodStat methodStat = methodStats.get(methodInfo);
        if (methodStat != null) {
            return methodStat;
        }

        if (create) {
            methodStats.putIfAbsent(methodInfo, new SpringMethodStat(methodInfo));
            methodStat = methodStats.get(methodInfo);
        }

        return methodStat;
    }
    
    public List<SpringMethodStatValue> getStatList(boolean reset) {
        List<SpringMethodStatValue> statValueList = new ArrayList<SpringMethodStatValue>(this.methodStats.size());
        
        for (SpringMethodStat methodStat : this.methodStats.values()) {
            SpringMethodStatValue statValue = methodStat.getStatValue(reset);
            
            if (statValue.getRunningCount() == 0 && statValue.getExecuteCount() == 0) {
                continue;
            }
            
            statValueList.add(statValue);
        }
        
        return statValueList;
    }

    public List<Map<String, Object>> getMethodStatDataList() {
        List<Map<String, Object>> methodStatDataList = new ArrayList<Map<String, Object>>(this.methodStats.size());
        for (SpringMethodStat methodStat : this.methodStats.values()) {
            Map<String, Object> methodStatData = methodStat.getStatData();

            int runningCount = ((Number) methodStatData.get("RunningCount")).intValue();
            long executeCount = (Long) methodStatData.get("ExecuteCount");

            if (runningCount == 0 && executeCount == 0) {
                continue;
            }

            methodStatDataList.add(methodStatData);
        }
        return methodStatDataList;
    }

    public Map<String, Object> getMethodStatData(String clazz, String method) {
        for (SpringMethodStat methodStat : this.methodStats.values()) {
            SpringMethodInfo methodInfo = methodStat.getMethodInfo();
            if (methodInfo.getClassName().equals(clazz) && methodInfo.getSignature().equals(method)) {
                return methodStat.getStatData();
            }
        }
        
        return null;
    }
}
