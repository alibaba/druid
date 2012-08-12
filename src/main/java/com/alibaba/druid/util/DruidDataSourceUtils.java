package com.alibaba.druid.util;

import java.lang.reflect.Method;
import java.util.Map;

import javax.management.ObjectName;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class DruidDataSourceUtils {

    private final static Log LOG = LogFactory.getLog(DruidDataSourceUtils.class);

    public static String getUrl(Object druidDatasource) {
        try {
            Method method = druidDatasource.getClass().getMethod("getUrl");
            Object obj = method.invoke(druidDatasource);
            return (String) obj;
        } catch (Exception e) {
            LOG.error("getUrl error", e);
            return null;
        }
    }
    
    public static ObjectName getObjectName(Object druidDatasource) {
        try {
            Method method = druidDatasource.getClass().getMethod("getObjectName");
            Object obj = method.invoke(druidDatasource);
            return (ObjectName) obj;
        } catch (Exception e) {
            LOG.error("getObjectName error", e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getStatData(Object druidDatasource) {
        try {
            Method method = druidDatasource.getClass().getMethod("getStatData");
            Object obj = method.invoke(druidDatasource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getStatData error", e);
            return null;
        }
    }
}
