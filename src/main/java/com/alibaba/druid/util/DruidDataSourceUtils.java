/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.management.ObjectName;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class DruidDataSourceUtils {

    private final static Log LOG = LogFactory.getLog(DruidDataSourceUtils.class);

    public static String getUrl(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getUrl();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getUrl");
            Object obj = method.invoke(druidDatasource);
            return (String) obj;
        } catch (Exception e) {
            LOG.error("getUrl error", e);
            return null;
        }
    }
    
    public static long getID(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getID();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getID");
            Object obj = method.invoke(druidDatasource);
            return (Long) obj;
        } catch (Exception e) {
            LOG.error("getID error", e);
            return -1;
        }
    }
    
    public static String getName(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getName();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getName");
            Object obj = method.invoke(druidDatasource);
            return (String) obj;
        } catch (Exception e) {
            LOG.error("getUrl error", e);
            return null;
        }
    }

    public static ObjectName getObjectName(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getObjectName();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getObjectName");
            Object obj = method.invoke(druidDatasource);
            return (ObjectName) obj;
        } catch (Exception e) {
            LOG.error("getObjectName error", e);
            return null;
        }
    }

    public static Object getSqlStat(Object druidDatasource, int sqlId) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getSqlStat(sqlId);
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getSqlStat", int.class);
            Object obj = method.invoke(druidDatasource, sqlId);
            return obj;
        } catch (Exception e) {
            LOG.error("getSqlStat error", e);
            return null;
        }
    }

    public static boolean isRemoveAbandoned(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).isRemoveAbandoned();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("isRemoveAbandoned");
            Object obj = method.invoke(druidDatasource);
            return (Boolean) obj;
        } catch (Exception e) {
            LOG.error("isRemoveAbandoned error", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getStatDataForMBean(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getStatDataForMBean();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getStatDataForMBean");
            Object obj = method.invoke(druidDatasource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getStatDataForMBean error", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getStatData(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getStatData();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getStatData");
            Object obj = method.invoke(druidDatasource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getStatData error", e);
            return null;
        }
    }

    @SuppressWarnings({ "rawtypes" })
    public static Map getSqlStatMap(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getSqlStatMap();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getSqlStatMap");
            Object obj = method.invoke(druidDatasource);
            return (Map) obj;
        } catch (Exception e) {
            LOG.error("getSqlStatMap error", e);
            return null;
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map<String, Object> getWallStatMap(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getWallStatMap();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getWallStatMap");
            Object obj = method.invoke(druidDatasource);
            return (Map) obj;
        } catch (Exception e) {
            LOG.error("getWallStatMap error", e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getPoolingConnectionInfo(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getPoolingConnectionInfo();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getPoolingConnectionInfo");
            Object obj = method.invoke(druidDatasource);
            return (List<Map<String, Object>>) obj;
        } catch (Exception e) {
            LOG.error("getPoolingConnectionInfo error", e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<String> getActiveConnectionStackTrace(Object druidDatasource) {
        if (druidDatasource.getClass() == DruidDataSource.class) {
            return ((DruidDataSource) druidDatasource).getActiveConnectionStackTrace();
        }
        
        try {
            Method method = druidDatasource.getClass().getMethod("getActiveConnectionStackTrace");
            Object obj = method.invoke(druidDatasource);
            return (List<String>) obj;
        } catch (Exception e) {
            LOG.error("getActiveConnectionStackTrace error", e);
            return null;
        }
    }
}
