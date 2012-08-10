package com.alibaba.druid.stat;

import java.util.List;
import java.util.Map;

import com.alibaba.druid.pool.DruidDataSource;

public interface DruidDataSourceStatStrategy {

    public Map<String, Object> getSqlStatData(Integer id);

    public List<Map<String, Object>> getSqlStatDataList();

    public List<String> getActiveConnectionStackTraceByDataSourceId(Integer id);

    public Map<String, Object> returnJSONBasicStat();

    public List<Object> getDataSourceStatList();

    public Map<String, Object> getDataSourceStatData(Integer id);

    public List<Map<String, Object>> getPoolingConnectionInfoByDataSourceId(Integer id);

    public void resetAll();

    public DruidDataSource getDruidDataSourceById(Integer id);

    public JdbcSqlStat getSqlStatById(Integer id);
}
