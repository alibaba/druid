package com.alibaba.druid.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.ReflectionUtils;

/**
 * JNDI数据源通过反射调用Druid相关对象获得相关统计数据， 并处理了WebApp与WebContainer两种数据源同时存在场景的数据合并
 * 
 * @author septer<awnugdpygl@163.com>
 */
public class DruidDataSourceStatReflectStatStrategy implements DruidDataSourceStatStrategy {

    private final static Log LOG                              = LogFactory.getLog(DruidDataSourceStatReflectStatStrategy.class);

    private static Object    clazzFromWebContainer_instacne   = null;
    private static Object    clazzFromCurClassLoader_instacne = null;

    public DruidDataSourceStatReflectStatStrategy(){
        init();
    }

    private void init() {
        try {
            // cache the class
            String className = "com.alibaba.druid.stat.DruidDataSourceStatDefaultStrategy";
            Class<?> clazzFromWebContainer = ReflectionUtils.getClassFromWebContainer(className);
            Class<?> clazzFromCurClassLoader = ReflectionUtils.getClassFromCurrentClassLoader(className);

            if (clazzFromWebContainer != null) clazzFromWebContainer_instacne = clazzFromCurClassLoader.newInstance();
            if (clazzFromCurClassLoader != null && clazzFromCurClassLoader != clazzFromWebContainer) {
                clazzFromCurClassLoader_instacne = clazzFromCurClassLoader.newInstance();
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Object invokeMapResHandler(Object finalRs, Object invokeRes) {
        Object res = null;
        if (finalRs == null) {
            if (invokeRes instanceof Map) {
                res = (Map<String, Object>) invokeRes;
            } else if (invokeRes instanceof List) {
                res = (List<Map<String, Object>>) invokeRes;
            } else {
                res = invokeRes;
            }
        } else {
            if (invokeRes instanceof Map) {
                Map<String, Object> finalRs_ = (Map<String, Object>) finalRs;
                Map<String, Object> invokeRes_ = (Map<String, Object>) invokeRes;
                if (invokeRes != null) finalRs_.putAll(invokeRes_);
                res = finalRs_;
            } else if (invokeRes instanceof List) {
                List<Map<String, Object>> finalRs_ = (List<Map<String, Object>>) finalRs;
                List<Map<String, Object>> invokeRes_ = (List<Map<String, Object>>) invokeRes;
                if (invokeRes != null) finalRs_.addAll(invokeRes_);
                res = finalRs_;
            } else {
                final List list = new ArrayList();
                list.add(finalRs);
                list.add(invokeRes);
                res = list;// merge the others class type,but in this
            }
        }
        return res;
    }

    private Object mergeStatData(String methodName, Integer methodParam) {
        Object result = null;
        if (clazzFromWebContainer_instacne != null) {
            /*ReflectStatStrategy strategy = new WebAppReflectStatStrategy();
            result = invokeMapResHandler(result, strategy.invoke(clazzFromWebContainer_instacne, methodName,
                                                                 methodParam));
*/
            result = invokeMapResHandler(result, ReflectHelper.getInstance().invoke(clazzFromWebContainer_instacne,
                                                                                    methodName, methodParam));
        }
        if (clazzFromCurClassLoader_instacne != null) {
            /*ReflectStatStrategy strategy = new WebContainerReflectStatStrategy();
            result = invokeMapResHandler(result, strategy.invoke(clazzFromWebContainer_instacne, methodName,
                                                                 methodParam));*/

            result = invokeMapResHandler(result, ReflectHelper.getInstance().invoke(clazzFromCurClassLoader_instacne,
                                                                                    methodName, methodParam));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getSqlStatData(Integer id) {
        return (Map<String, Object>) mergeStatData("getSqlStatData", id);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDataSourceStatData(Integer id) {
        return (Map<String, Object>) mergeStatData("getDataSourceStatData", id);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> returnJSONBasicStat() {
        return (Map<String, Object>) mergeStatData("returnJSONBasicStat", null);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getSqlStatDataList() {
        return (List<Map<String, Object>>) mergeStatData("getSqlStatDataList", null);
    }

    @SuppressWarnings("unchecked")
    public List<String> getActiveConnectionStackTraceByDataSourceId(Integer id) {
        return (List<String>) mergeStatData("getActiveConnectionStackTraceByDataSourceId", id);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getDataSourceStatList() {
        return (List<Object>) mergeStatData("getDataSourceStatList", null);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getPoolingConnectionInfoByDataSourceId(Integer id) {
        return (List<Map<String, Object>>) mergeStatData("getPoolingConnectionInfoByDataSourceId", id);
    }

    public Object getDruidDataSourceById(Integer id) {
        return mergeStatData("getDruidDataSourceById", id);
    }

    public Object getSqlStatById(Integer id) {
        return mergeStatData("getSqlStatById", id);
    }

    public void resetAll() {
        mergeStatData("resetAll", null);
    }

}
