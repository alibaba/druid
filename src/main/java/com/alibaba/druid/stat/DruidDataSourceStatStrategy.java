package com.alibaba.druid.stat;

import java.util.List;
import java.util.Map;

public interface DruidDataSourceStatStrategy {

    public Map<String, Object> getSqlStatData(Integer id);

    public List<Map<String, Object>> getSqlStatDataList();

    public List<String> getActiveConnectionStackTraceByDataSourceId(Integer id);

    public Map<String, Object> returnJSONBasicStat();

    public List<Object> getDataSourceStatList();

    public Map<String, Object> getDataSourceStatData(Integer id);

    public List<Map<String, Object>> getPoolingConnectionInfoByDataSourceId(Integer id);

    public void resetAll();

    public Object getDruidDataSourceById(Integer id);

    public Object getSqlStatById(Integer id);
}
