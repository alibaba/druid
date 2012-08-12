package com.alibaba.druid.support.http.stat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class WebAppStatUtils {

    private final static Log LOG = LogFactory.getLog(WebAppStatUtils.class);

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getStatData(Object webStat) {
        try {
            Method method = webStat.getClass().getMethod("getStatData");
            Object obj = method.invoke(webStat);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getStatData error", e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getURIStatDataList(Object webStat) {
        try {
            Method method = webStat.getClass().getMethod("getURIStatDataList");
            Object obj = method.invoke(webStat);
            return (List<Map<String, Object>>) obj;
        } catch (Exception e) {
            LOG.error("getURIStatDataList error", e);
            return null;
        }
    }
}
