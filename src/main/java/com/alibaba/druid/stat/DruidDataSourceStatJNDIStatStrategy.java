package com.alibaba.druid.stat;

import java.util.List;
import java.util.Map;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.ReflectionUtils;

public class DruidDataSourceStatJNDIStatStrategy implements DruidDataSourceStatStrategy {

    private final static Log LOG = LogFactory.getLog(DruidDataSourceStatJNDIStatStrategy.class);

    private Object getStatDefaultStrategy() {
        try {
            Class<?> clazz = ReflectionUtils.getClassFromWebContainerOrCurrentClassLoader("com.alibaba.druid.stat.DruidDataSourceStatDefaultStrategy");
            return clazz.newInstance();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getSqlStatData(Integer id) {
        return (Map<String, Object>) ReflectionUtils.callObjectMethod(getStatDefaultStrategy(), "getSqlStatData", id);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getSqlStatDataList() {
        return (List<Map<String, Object>>) ReflectionUtils.callObjectMethod(getStatDefaultStrategy(), "getSqlStatDataList");
    }

    @SuppressWarnings("unchecked")
    public List<String> getActiveConnectionStackTraceByDataSourceId(Integer id) {
        return (List<String>) ReflectionUtils.callObjectMethod(getStatDefaultStrategy(),
                                                               "getActiveConnectionStackTraceByDataSourceId", id);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> returnJSONBasicStat() {
        return (Map<String, Object>) ReflectionUtils.callObjectMethod(getStatDefaultStrategy(), "returnJSONBasicStat");
    }

    @SuppressWarnings("unchecked")
    public List<Object> getDataSourceStatList() {
        return (List<Object>) ReflectionUtils.callObjectMethod(getStatDefaultStrategy(), "getDataSourceStatList");
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDataSourceStatData(Integer id) {
        return (Map<String, Object>) ReflectionUtils.callObjectMethod(getStatDefaultStrategy(), "getDataSourceStatData", id);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getPoolingConnectionInfoByDataSourceId(Integer id) {
        return (List<Map<String, Object>>) ReflectionUtils.callObjectMethod(getStatDefaultStrategy(),
                                                                            "getPoolingConnectionInfoByDataSourceId",
                                                                            id);
    }

    public void resetAll() {
        ReflectionUtils.callObjectMethod(getStatDefaultStrategy(), "resetAll");
    }

    public DruidDataSource getDruidDataSourceById(Integer id) {
        return (DruidDataSource) ReflectionUtils.callObjectMethod(getStatDefaultStrategy(), "getDruidDataSourceById");
    }

    public JdbcSqlStat getSqlStatById(Integer id) {
        return (JdbcSqlStat) ReflectionUtils.callObjectMethod(getStatDefaultStrategy(), "getSqlStatById", id);
    }

}
