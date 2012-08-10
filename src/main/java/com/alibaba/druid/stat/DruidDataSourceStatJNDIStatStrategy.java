package com.alibaba.druid.stat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.ReflectionUtils;

public class DruidDataSourceStatJNDIStatStrategy implements DruidDataSourceStatStrategy {

    private final static Log                                      LOG           = LogFactory.getLog(DruidDataSourceStatJNDIStatStrategy.class);

    private final static List<Object>                             strategyList  = new ArrayList<Object>();

    private final static HashMap<Object, HashMap<String, Method>> classMethodHM = new HashMap<Object, HashMap<String, Method>>();

    private final static String[]                                 methodNameArr = new String[] { "getSqlStatData",
            "getSqlStatDataList", "getActiveConnectionStackTraceByDataSourceId", "returnJSONBasicStat",
            "getDataSourceStatList", "getDataSourceStatData", "getPoolingConnectionInfoByDataSourceId", "resetAll",
            "getDruidDataSourceById", "getSqlStatById"                         };

    public DruidDataSourceStatJNDIStatStrategy(){
        init();
    }

    private void init() {
        try {
            // cache the class
            String className = "com.alibaba.druid.stat.DruidDataSourceStatDefaultStrategy";
            Class<?> clazzFromWebContainer = ReflectionUtils.getClassFromWebContainer(className);
            Class<?> clazzFromCurClassLoader = ReflectionUtils.getClassFromCurrentClassLoader(className);
            if (clazzFromWebContainer != null) strategyList.add(clazzFromWebContainer.newInstance());
            if (clazzFromCurClassLoader != null) strategyList.add(clazzFromCurClassLoader.newInstance());

            // cache the method
            Object obj = null;
            for (int i = 0; i < strategyList.size(); i++) {
                obj = strategyList.get(i);
                HashMap<String, Method> methodHM = null;
                String methodName = null;
                for (int j = 0; j < methodNameArr.length; j++) {
                    methodHM = new HashMap<String, Method>();
                    methodName = methodNameArr[j];
                    methodHM.put(methodName, ReflectionUtils.getObjectMethod(obj, methodName));
                }
                classMethodHM.put(obj, methodHM);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Object invoke(String methodName, Integer methodParam) {
        Object result = null;
        try {
            HashMap<String, Method> hm = null;
            Object invokeRes = null;
            Object obj = null;
            for (int i = 0; i < strategyList.size(); i++) {
                obj = strategyList.get(i);
                hm = classMethodHM.get(obj);
                if (hm.containsKey(methodName)) {
                    if (methodParam == null) {
                        invokeRes = hm.get(methodName).invoke(obj);
                    } else {
                        invokeRes = hm.get(methodName).invoke(obj, methodParam);
                    }
                    if (invokeRes instanceof Map) {
                        result = invokeMapResHandler(null, (Map<String, Object>) invokeRes);
                    } else if (invokeRes instanceof List) {
                        result = invokeListResHandler(null, (List<Map<String, Object>>) invokeRes);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    private Map<String, Object> invokeMapResHandler(Map<String, Object> finalRs, Map<String, Object> invokeRes) {
        Map<String, Object> res = null;
        if (finalRs == null) {
            res = finalRs;
        } else {
            finalRs.putAll(invokeRes);
            res = finalRs;
        }
        return res;
    }

    private List<?> invokeListResHandler(List<Map<String, Object>> finalRs, List<Map<String, Object>> invokeRes) {
        List<Map<String, Object>> res = null;
        if (finalRs == null) {
            res = finalRs;
        } else {
            finalRs.addAll(invokeRes);
            res = finalRs;
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getSqlStatData(Integer id) {
        return (Map<String, Object>) invoke("getSqlStatData", id);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDataSourceStatData(Integer id) {
        return (Map<String, Object>) invoke("getDataSourceStatData", id);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> returnJSONBasicStat() {
        return (Map<String, Object>) invoke("returnJSONBasicStat", null);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getSqlStatDataList() {
        return (List<Map<String, Object>>) invoke("getSqlStatDataList", null);
    }

    @SuppressWarnings("unchecked")
    public List<String> getActiveConnectionStackTraceByDataSourceId(Integer id) {
        return (List<String>) invoke("getActiveConnectionStackTraceByDataSourceId", id);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getDataSourceStatList() {
        return (List<Object>) invoke("getDataSourceStatList", null);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getPoolingConnectionInfoByDataSourceId(Integer id) {
        return (List<Map<String, Object>>) invoke("getPoolingConnectionInfoByDataSourceId", id);
    }

    public void resetAll() {
        invoke("resetAll", null);
    }

    public DruidDataSource getDruidDataSourceById(Integer id) {
        return (DruidDataSource) invoke("getDruidDataSourceById", id);
    }

    public JdbcSqlStat getSqlStatById(Integer id) {
        return (JdbcSqlStat) invoke("getSqlStatById", id);
    }

}
