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

import static com.alibaba.druid.util.Utils.trySetBooleanProperty;
import static com.alibaba.druid.util.Utils.trySetIntProperty;
import static com.alibaba.druid.util.Utils.trySetLongProperty;
import static com.alibaba.druid.util.Utils.trySetStringProperty;

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

    public static void configFromProperties(DruidDataSource druidDataSource, Properties properties) {
        trySetStringProperty(properties, "druid.name", druidDataSource::setName);
        trySetStringProperty(properties, "druid.url", druidDataSource::setUrl);
        trySetStringProperty(properties, "druid.username", druidDataSource::setUsername);
        trySetStringProperty(properties, "druid.password", druidDataSource::setPassword);
        trySetBooleanProperty(properties, "druid.testWhileIdle", druidDataSource::setTestWhileIdle);
        trySetBooleanProperty(properties, "druid.testOnBorrow", druidDataSource::setTestOnBorrow);
        {
            String property = properties.getProperty("druid.validationQuery");
            if (property != null && property.length() > 0) {
                druidDataSource.setValidationQuery(property);
            }
        }
        trySetBooleanProperty(properties, "druid.useGlobalDataSourceStat", druidDataSource::setUseGlobalDataSourceStat);
        trySetBooleanProperty(properties, "druid.useGloalDataSourceStat", druidDataSource::setUseGlobalDataSourceStat);
        trySetBooleanProperty(properties, "druid.asyncInit", druidDataSource::setAsyncInit);
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
        trySetLongProperty(properties, Constants.DRUID_TIME_BETWEEN_LOG_STATS_MILLIS, druidDataSource::setTimeBetweenLogStatsMillis);
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
        trySetBooleanProperty(properties, "druid.clearFiltersEnable", druidDataSource::setClearFiltersEnable);
        trySetBooleanProperty(properties, "druid.resetStatEnable", druidDataSource::setResetStatEnable);
        trySetIntProperty(properties, "druid.notFullTimeoutRetryCount", druidDataSource::setNotFullTimeoutRetryCount);
        trySetLongProperty(properties, "druid.timeBetweenEvictionRunsMillis", druidDataSource::setTimeBetweenEvictionRunsMillis);
        trySetIntProperty(properties, "druid.maxWaitThreadCount", druidDataSource::setMaxWaitThreadCount);
        trySetIntProperty(properties, "druid.maxWait", druidDataSource::setMaxWait);
        trySetBooleanProperty(properties, "druid.failFast", druidDataSource::setFailFast);
        trySetLongProperty(properties, "druid.phyTimeoutMillis", druidDataSource::setPhyTimeoutMillis);
        trySetLongProperty(properties, "druid.phyMaxUseCount", druidDataSource::setPhyMaxUseCount);
        trySetLongProperty(properties, "druid.minEvictableIdleTimeMillis", druidDataSource::setMinEvictableIdleTimeMillis);
        trySetLongProperty(properties, "druid.maxEvictableIdleTimeMillis", druidDataSource::setMaxEvictableIdleTimeMillis);
        trySetBooleanProperty(properties, "druid.keepAlive", druidDataSource::setKeepAlive);
        trySetLongProperty(properties, "druid.keepAliveBetweenTimeMillis", druidDataSource::setKeepAliveBetweenTimeMillis);
        trySetBooleanProperty(properties, "druid.poolPreparedStatements", druidDataSource::setPoolPreparedStatements);
        trySetBooleanProperty(properties, "druid.initVariants", druidDataSource::setInitVariants);
        trySetBooleanProperty(properties, "druid.initGlobalVariants", druidDataSource::setInitGlobalVariants);
        trySetBooleanProperty(properties, "druid.useUnfairLock", druidDataSource::setUseUnfairLock);
        trySetStringProperty(properties, "druid.driverClassName", druidDataSource::setDriverClassName);
        trySetIntProperty(properties, "druid.initialSize", druidDataSource::setInitialSize);
        trySetIntProperty(properties, "druid.minIdle", druidDataSource::setMinIdle);
        trySetIntProperty(properties, "druid.maxActive", druidDataSource::setMaxActive);
        trySetBooleanProperty(properties, "druid.killWhenSocketReadTimeout", druidDataSource::setKillWhenSocketReadTimeout);
        trySetStringProperty(properties, "druid.connectProperties", druidDataSource::setConnectionProperties);
        trySetIntProperty(properties, "druid.maxPoolPreparedStatementPerConnectionSize",
            druidDataSource::setMaxPoolPreparedStatementPerConnectionSize);
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
        // new added
        trySetIntProperty(properties, "druid.connectionErrorRetryAttempts", druidDataSource::setConnectionErrorRetryAttempts);
        trySetLongProperty(properties, "druid.timeBetweenConnectErrorMillis", druidDataSource::setTimeBetweenConnectErrorMillis);
        trySetBooleanProperty(properties, "druid.breakAfterAcquireFailure", druidDataSource::setBreakAfterAcquireFailure);
        trySetBooleanProperty(properties, "druid.testOnReturn", druidDataSource::setTestOnReturn);
        trySetBooleanProperty(properties, "druid.removeAbandoned", druidDataSource::setRemoveAbandoned);
        trySetBooleanProperty(properties, "druid.logAbandoned", druidDataSource::setLogAbandoned);
        trySetLongProperty(properties, "druid.removeAbandonedTimeoutMillis", druidDataSource::setRemoveAbandonedTimeoutMillis);
        trySetIntProperty(properties, "druid.validationQueryTimeout", druidDataSource::setValidationQueryTimeout);
        trySetIntProperty(properties, "druid.queryTimeout", druidDataSource::setQueryTimeout);
        trySetIntProperty(properties, "druid.connectTimeout", druidDataSource::setConnectTimeout);
        trySetIntProperty(properties, "druid.socketTimeout", druidDataSource::setSocketTimeout);
        trySetIntProperty(properties, "druid.transactionQueryTimeout", druidDataSource::setTransactionQueryTimeout);
        trySetIntProperty(properties, "druid.loginTimeout", druidDataSource::setLoginTimeout);
    }
}
