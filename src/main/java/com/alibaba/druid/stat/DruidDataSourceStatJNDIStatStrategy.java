package com.alibaba.druid.stat;

import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.ReflectionUtils;

public class DruidDataSourceStatJNDIStatStrategy implements DruidDataSourceStatStrategy{
    private final static Log LOG = LogFactory.getLog(DruidDataSourceStatJNDIStatStrategy.class);

	private Object getStrategyB() {
		Class<?> clazz = ReflectionUtils.getClassFromWebContainerOrCurrentClassLoader("com.alibaba.druid.stat.DruidDataSourceStatDefaultStrategy");
		Object object = null;
        try {
            object = clazz.newInstance();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
		return object;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getSqlStatData(Integer id) {
		return (Map<String, Object>) ReflectionUtils.callObjectMethod(getStrategyB(), "getSqlStatData", id);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getSqlStatDataList() {
		return (List<Map<String, Object>>) ReflectionUtils.callObjectMethod(getStrategyB(), "getSqlStatDataList");
	}

	@SuppressWarnings("unchecked")
	public List<String> getActiveConnectionStackTraceByDataSourceId(Integer id) {
		return (List<String>) ReflectionUtils.callObjectMethod(getStrategyB(), "getActiveConnectionStackTraceByDataSourceId", id);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> returnJSONBasicStat() {
		return (Map<String, Object>) ReflectionUtils.callObjectMethod(getStrategyB(), "returnJSONBasicStat");
	}

	@SuppressWarnings("unchecked")
	public List<Object> getDataSourceStatList() {
		return (List<Object>) ReflectionUtils.callObjectMethod(getStrategyB(), "getDataSourceStatList");
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getDataSourceStatData(Integer id) {
		return (Map<String, Object>) ReflectionUtils.callObjectMethod(getStrategyB(), "getDataSourceStatData", id);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPoolingConnectionInfoByDataSourceId(Integer id) {
		return (List<Map<String, Object>>) ReflectionUtils.callObjectMethod(getStrategyB(), "getPoolingConnectionInfoByDataSourceId", id);
	}

	public void resetAll() {
		ReflectionUtils.callObjectMethod(getStrategyB(), "resetAll");
	}

}
