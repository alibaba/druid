/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.Constants;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import javax.management.ObjectName;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import static com.alibaba.druid.util.Utils.getBoolean;

public class DruidDataSourceUtils {
    private static final Log LOG = LogFactory.getLog(DruidDataSourceUtils.class);

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

    @SuppressWarnings("rawtypes")
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

    @SuppressWarnings({"rawtypes", "unchecked"})
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

    public static void configFromPropety(DruidDataSource druidDataSource, Properties properties) {
       {
           String property = properties.getProperty("druid.name");
           if (property != null) {
               druidDataSource.setName(property);
           }
       }
       {
           String property = properties.getProperty("druid.url");
           if (property != null) {
               druidDataSource.setUrl(property);
           }
       }
       {
           String property = properties.getProperty("druid.username");
           if (property != null) {
               druidDataSource.setUsername(property);
           }
       }
       {
           String property = properties.getProperty("druid.password");
           if (property != null) {
               druidDataSource.setPassword(property);
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.testWhileIdle");
           if (value != null) {
               druidDataSource.setTestWhileIdle(value);
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.testOnBorrow");
           if (value != null) {
               druidDataSource.setTestOnBorrow(value);
           }
       }
       {
           String property = properties.getProperty("druid.validationQuery");
           if (property != null && property.length() > 0) {
               druidDataSource.setValidationQuery(property);
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.useGlobalDataSourceStat");
           if (value != null) {
               druidDataSource.setUseGlobalDataSourceStat(value);
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.useGloalDataSourceStat"); // compatible for early versions
           if (value != null) {
               druidDataSource.setUseGlobalDataSourceStat(value);
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.asyncInit"); // compatible for early versions
           if (value != null) {
               druidDataSource.setAsyncInit(value);
           }
       }
       {
           String property = properties.getProperty("druid.filters");

           if (property != null && property.length() > 0) {
               try {
                   druidDataSource.setFilters(property);
               } catch (SQLException e) {
                   LOG.error("setFilters error", e);
               }
           }
       }
       {
           String property = properties.getProperty(Constants.DRUID_TIME_BETWEEN_LOG_STATS_MILLIS);
           if (property != null && property.length() > 0) {
               try {
                   long value = Long.parseLong(property);
                   druidDataSource.setTimeBetweenLogStatsMillis(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property '" + Constants.DRUID_TIME_BETWEEN_LOG_STATS_MILLIS + "'", e);
               }
           }
       }
       {
           String property = properties.getProperty(Constants.DRUID_STAT_SQL_MAX_SIZE);
           if (property != null && property.length() > 0) {
               try {
                   int value = Integer.parseInt(property);
                   if (druidDataSource.getDataSourceStat() != null) {
                       druidDataSource.getDataSourceStat().setMaxSqlSize(value);
                   }
               } catch (NumberFormatException e) {
                   LOG.error("illegal property '" + Constants.DRUID_STAT_SQL_MAX_SIZE + "'", e);
               }
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.clearFiltersEnable");
           if (value != null) {
               druidDataSource.setClearFiltersEnable(value);
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.resetStatEnable");
           if (value != null) {
               druidDataSource.setResetStatEnable(value);
           }
       }
       {
           String property = properties.getProperty("druid.notFullTimeoutRetryCount");
           if (property != null && property.length() > 0) {
               try {
                   int value = Integer.parseInt(property);
                   druidDataSource.setNotFullTimeoutRetryCount(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.notFullTimeoutRetryCount'", e);
               }
           }
       }
       {
           String property = properties.getProperty("druid.timeBetweenEvictionRunsMillis");
           if (property != null && property.length() > 0) {
               try {
                   long value = Long.parseLong(property);
                   druidDataSource.setTimeBetweenEvictionRunsMillis(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.timeBetweenEvictionRunsMillis'", e);
               }
           }
       }
       {
           String property = properties.getProperty("druid.maxWaitThreadCount");
           if (property != null && property.length() > 0) {
               try {
                   int value = Integer.parseInt(property);
                   druidDataSource.setMaxWaitThreadCount(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.maxWaitThreadCount'", e);
               }
           }
       }
       {
           String property = properties.getProperty("druid.maxWait");
           if (property != null && property.length() > 0) {
               try {
                   int value = Integer.parseInt(property);
                   druidDataSource.setMaxWait(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.maxWait'", e);
               }
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.failFast");
           if (value != null) {
               druidDataSource.setFailFast(value);
           }
       }
       {
           String property = properties.getProperty("druid.phyTimeoutMillis");
           if (property != null && property.length() > 0) {
               try {
                   long value = Long.parseLong(property);
                   druidDataSource.setPhyTimeoutMillis(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.phyTimeoutMillis'", e);
               }
           }
       }
       {
           String property = properties.getProperty("druid.phyMaxUseCount");
           if (property != null && property.length() > 0) {
               try {
                   long value = Long.parseLong(property);
                   druidDataSource.setPhyMaxUseCount(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.phyMaxUseCount'", e);
               }
           }
       }
       {
           String property = properties.getProperty("druid.minEvictableIdleTimeMillis");
           if (property != null && property.length() > 0) {
               try {
                   long value = Long.parseLong(property);
                   druidDataSource.setMinEvictableIdleTimeMillis(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.minEvictableIdleTimeMillis'", e);
               }
           }
       }
       {
           String property = properties.getProperty("druid.maxEvictableIdleTimeMillis");
           if (property != null && property.length() > 0) {
               try {
                   long value = Long.parseLong(property);
                   druidDataSource.setMaxEvictableIdleTimeMillis(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.maxEvictableIdleTimeMillis'", e);
               }
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.keepAlive");
           if (value != null) {
               druidDataSource.setKeepAlive(value);
           }
       }
       {
           String property = properties.getProperty("druid.keepAliveBetweenTimeMillis");
           if (property != null && property.length() > 0) {
               try {
                   long value = Long.parseLong(property);
                   druidDataSource.setKeepAliveBetweenTimeMillis(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.keepAliveBetweenTimeMillis'", e);
               }
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.poolPreparedStatements");
           if (value != null) {
               druidDataSource.setPoolPreparedStatements(value);
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.initVariants");
           if (value != null) {
               druidDataSource.setInitVariants(value);
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.initGlobalVariants");
           if (value != null) {
               druidDataSource.setInitGlobalVariants(value);
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.useUnfairLock");
           if (value != null) {
               druidDataSource.setUseUnfairLock(value);
           }
       }
       {
           String property = properties.getProperty("druid.driverClassName");
           if (property != null) {
               druidDataSource.setDriverClassName(property);
           }
       }
       {
           String property = properties.getProperty("druid.initialSize");
           if (property != null && property.length() > 0) {
               try {
                   int value = Integer.parseInt(property);
                   druidDataSource.setInitialSize(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.initialSize'", e);
               }
           }
       }
       {
           String property = properties.getProperty("druid.minIdle");
           if (property != null && property.length() > 0) {
               try {
                   int value = Integer.parseInt(property);
                   druidDataSource.setMinIdle(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.minIdle'", e);
               }
           }
       }
       {
           String property = properties.getProperty("druid.maxActive");
           if (property != null && property.length() > 0) {
               try {
                   int value = Integer.parseInt(property);
                   druidDataSource.setMaxActive(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.maxActive'", e);
               }
           }
       }
       {
           Boolean value = getBoolean(properties, "druid.killWhenSocketReadTimeout");
           if (value != null) {
               druidDataSource.setKillWhenSocketReadTimeout(value);
           }
       }
       {
           String property = properties.getProperty("druid.connectProperties");
           if (property != null) {
               druidDataSource.setConnectionProperties(property);
           }
       }
       {
           String property = properties.getProperty("druid.maxPoolPreparedStatementPerConnectionSize");
           if (property != null && property.length() > 0) {
               try {
                   int value = Integer.parseInt(property);
                   druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(value);
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.maxPoolPreparedStatementPerConnectionSize'", e);
               }
           }
       }
       {
           String property = properties.getProperty("druid.initConnectionSqls");
           if (property != null && property.length() > 0) {
               try {
                   StringTokenizer tokenizer = new StringTokenizer(property, ";");
                   druidDataSource.setConnectionInitSqls(Collections.list(tokenizer));
               } catch (NumberFormatException e) {
                   LOG.error("illegal property 'druid.initConnectionSqls'", e);
               }
           }
       }
       {
           String property = System.getProperty("druid.load.spifilter.skip");
           if (property != null && !"false".equals(property)) {
               druidDataSource.setLoadSpifilterSkip(true);
           }
       }
       {
           String property = System.getProperty("druid.checkExecuteTime");
           if (property != null && !"false".equals(property)) {
               druidDataSource.setCheckExecuteTime(true);
           }
       }
   }
}
