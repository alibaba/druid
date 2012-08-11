package com.alibaba.druid.stat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.ReflectionUtils;

public class DruidDataSourceStatJNDIStatStrategy implements DruidDataSourceStatStrategy {

    private final static Log                                      LOG           = LogFactory.getLog(DruidDataSourceStatJNDIStatStrategy.class);

    private final static List<Object>                             strategyList  = new ArrayList<Object>();

    private final static HashMap<Object, HashMap<String, Method>> classMethodHM = new HashMap<Object, HashMap<String, Method>>();

    public DruidDataSourceStatJNDIStatStrategy(){
        init();
    }

    private void init() {
        try {
            // cache the class
            String className = "com.alibaba.druid.stat.DruidDataSourceStatDefaultStrategy";
            Class<?> clazzFromWebContainer = ReflectionUtils.getClassFromWebContainer(className);
            Class<?> clazzFromCurClassLoader = ReflectionUtils.getClassFromCurrentClassLoader(className);
            if (clazzFromCurClassLoader != null) {
                Object clazzFromCurClassLoaderInstance = clazzFromCurClassLoader.newInstance();
                if (!strategyList.contains(clazzFromCurClassLoaderInstance)) {
                    strategyList.add(clazzFromCurClassLoaderInstance);
                }
            }
            if (clazzFromWebContainer != null) {
                Object clazzFromWebContainerInstance = clazzFromWebContainer.newInstance();
                if (!strategyList.contains(clazzFromWebContainerInstance)) {
                    if (clazzFromCurClassLoader != null && clazzFromWebContainerInstance != clazzFromCurClassLoader) {
                        strategyList.add(clazzFromWebContainerInstance);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private Object invoke(String methodName, Integer id) {
        Object result = null;
        try {
            HashMap<String, Method> hm = null;
            Object invokeRes = null;
            Object obj = null;
            Method method = null;
            for (int i = 0; i < strategyList.size(); i++) {
                obj = strategyList.get(i);
                hm = classMethodHM.get(obj);
                if (hm != null) {
                    if (hm.containsKey(methodName)) {
                        method = hm.get(methodName);
                        if (id == null) {
                            invokeRes = ReflectionUtils.callObjectMethod(obj, method);
                        } else {
                            invokeRes = ReflectionUtils.callObjectMethod(obj, method, id);
                        }
                        result = invokeMapResHandler(result, invokeRes);
                    } else {
                        result = cacheMethodAndRtnInvodeRes(hm, obj, methodName, id);
                    }
                } else {
                    hm = new HashMap<String, Method>();
                    result = cacheMethodAndRtnInvodeRes(hm, obj, methodName, id);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    private Object cacheMethodAndRtnInvodeRes(HashMap<String, Method> methodHM, Object obj, String methodName,
                                              Integer id) throws Exception {
        Method method = null;
        Object invokeRes = null;
        if (id == null) {
            method = ReflectionUtils.getObjectMethod(obj, methodName);
            invokeRes = ReflectionUtils.callObjectMethod(obj, method);
        } else {
            method = ReflectionUtils.getObjectMethod(obj, methodName, id);
            invokeRes = ReflectionUtils.callObjectMethod(obj, method, id);
        }
        methodHM.put(methodName, method);
        classMethodHM.put(obj, methodHM);
        return invokeRes;
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

    public Object getDruidDataSourceById(Integer id) {
        return invoke("getDruidDataSourceById", id);
    }

    public Object getSqlStatById(Integer id) {
        return invoke("getSqlStatById", id);
    }

    public void resetAll() {
        invoke("resetAll", null);
    }

}
