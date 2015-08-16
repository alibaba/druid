/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import javax.management.ObjectName;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DruidDataSourceUtils {

    private final static Log LOG = LogFactory.getLog(DruidDataSourceUtils.class);

    public static String getUrl(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getUrl();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getUrl");
            Object obj = method.invoke(druidDataSource);
            return (String) obj;
        } catch (Exception e) {
            LOG.error("getUrl error", e);
            return null;
        }
    }
    
    public static long getID(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getID();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getID");
            Object obj = method.invoke(druidDataSource);
            return (Long) obj;
        } catch (Exception e) {
            LOG.error("getID error", e);
            return -1;
        }
    }
    
    public static String getName(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getName();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getName");
            Object obj = method.invoke(druidDataSource);
            return (String) obj;
        } catch (Exception e) {
            LOG.error("getUrl error", e);
            return null;
        }
    }

    public static ObjectName getObjectName(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getObjectName();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getObjectName");
            Object obj = method.invoke(druidDataSource);
            return (ObjectName) obj;
        } catch (Exception e) {
            LOG.error("getObjectName error", e);
            return null;
        }
    }

    public static Object getSqlStat(Object druidDataSource, int sqlId) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getSqlStat(sqlId);
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getSqlStat", int.class);
            return method.invoke(druidDataSource, sqlId);
        } catch (Exception e) {
            LOG.error("getSqlStat error", e);
            return null;
        }
    }

    public static boolean isRemoveAbandoned(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).isRemoveAbandoned();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("isRemoveAbandoned");
            Object obj = method.invoke(druidDataSource);
            return (Boolean) obj;
        } catch (Exception e) {
            LOG.error("isRemoveAbandoned error", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getStatDataForMBean(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getStatDataForMBean();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getStatDataForMBean");
            Object obj = method.invoke(druidDataSource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getStatDataForMBean error", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getStatData(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getStatData();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getStatData");
            Object obj = method.invoke(druidDataSource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getStatData error", e);
            return null;
        }
    }

    @SuppressWarnings({ "rawtypes" })
    public static Map getSqlStatMap(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getSqlStatMap();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getSqlStatMap");
            Object obj = method.invoke(druidDataSource);
            return (Map) obj;
        } catch (Exception e) {
            LOG.error("getSqlStatMap error", e);
            return null;
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map<String, Object> getWallStatMap(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getWallStatMap();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getWallStatMap");
            Object obj = method.invoke(druidDataSource);
            return (Map) obj;
        } catch (Exception e) {
            LOG.error("getWallStatMap error", e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getPoolingConnectionInfo(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getPoolingConnectionInfo();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getPoolingConnectionInfo");
            Object obj = method.invoke(druidDataSource);
            return (List<Map<String, Object>>) obj;
        } catch (Exception e) {
            LOG.error("getPoolingConnectionInfo error", e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<String> getActiveConnectionStackTrace(Object druidDataSource) {
        if (druidDataSource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDataSource).getActiveConnectionStackTrace();
        }
        
        try {
            Method method = druidDataSource.getClass().getMethod("getActiveConnectionStackTrace");
            Object obj = method.invoke(druidDataSource);
            return (List<String>) obj;
        } catch (Exception e) {
            LOG.error("getActiveConnectionStackTrace error", e);
            return null;
        }
    }
}
