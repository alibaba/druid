package com.alibaba.druid.support.spring.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SpringStat {

    private ConcurrentMap<MethodInfo, SpringMethodStat> methodStats = new ConcurrentHashMap<MethodInfo, SpringMethodStat>();

    public SpringStat(){

    }

    public void reset() {
        for (SpringMethodStat stat : methodStats.values()) {
            stat.reset();
        }
    }

    public SpringMethodStat getMethodStat(MethodInfo methodInfo, boolean create) {
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
}
