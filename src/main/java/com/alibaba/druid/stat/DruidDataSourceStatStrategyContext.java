package com.alibaba.druid.stat;

import java.util.List;
import java.util.Map;

public class DruidDataSourceStatStrategyContext {

    private DruidDataSourceStatStrategy druidDataSourceStatStrategy;

    public DruidDataSourceStatStrategyContext(DruidDataSourceStatStrategy druidDataSourceStatStrategy){
        this.druidDataSourceStatStrategy = druidDataSourceStatStrategy;
    }

    public Map<String, Object> getSqlStatData(Integer id) {
        return druidDataSourceStatStrategy.getSqlStatData(id);
    }

    public List<Map<String, Object>> getSqlStatDataList() {
        return druidDataSourceStatStrategy.getSqlStatDataList();
    }

    public List<String> getActiveConnectionStackTraceByDataSourceId(Integer id) {
        return druidDataSourceStatStrategy.getActiveConnectionStackTraceByDataSourceId(id);
    }

    public Map<String, Object> returnJSONBasicStat() {
        return druidDataSourceStatStrategy.returnJSONBasicStat();
    }

    public List<Object> getDataSourceStatList() {
        return druidDataSourceStatStrategy.getDataSourceStatList();
    }

    public Map<String, Object> getDataSourceStatData(Integer id) {
        return druidDataSourceStatStrategy.getDataSourceStatData(id);
    }

    public List<Map<String, Object>> getPoolingConnectionInfoByDataSourceId(Integer id) {
        return druidDataSourceStatStrategy.getPoolingConnectionInfoByDataSourceId(id);
    }

    public void resetAll() {
        druidDataSourceStatStrategy.resetAll();
    }

    public Object getDruidDataSourceById(Integer id) {
        return druidDataSourceStatStrategy.getDruidDataSourceById(id);
    }

    public Object getSqlStatById(Integer id) {
        return druidDataSourceStatStrategy.getSqlStatById(id);
    }

}
