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
package com.alibaba.druid.pool;

import static com.alibaba.druid.util.JdbcSqlStatUtils.rtrim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * @author wenshao<szujobs@hotmail.com>
 * @since 0.2.19
 */
public class DruidDataSourceStatLoggerImpl extends DruidDataSourceStatLoggerAdapter {

    private static Log LOG    = LogFactory.getLog(DruidDataSourceStatLoggerImpl.class);

    private Log        logger = LOG;

    public DruidDataSourceStatLoggerImpl(){
        this.configFromProperties(System.getProperties());
    }

    /**
     * @since 0.2.21
     */
    @Override
    public void configFromProperties(Properties properties) {
        String property = properties.getProperty("druid.stat.loggerName");
        if (property != null && property.length() > 0) {
            setLoggerName(property);
        }
    }

    public Log getLogger() {
        return logger;
    }

    @Override
    public void setLoggerName(String loggerName) {
        logger = LogFactory.getLog(loggerName);
    }

    @Override
    public void setLogger(Log logger) {
        if (logger == null) {
            throw new IllegalArgumentException("logger can not be null");
        }
        this.logger = logger;
    }

    public boolean isLogEnable() {
        return logger.isInfoEnabled();
    }

    public void log(String value) {
        logger.info(value);
    }

    @Override
    public void log(DruidDataSourceStatValue statValue) {
        if (!isLogEnable()) {
            return;
        }
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("url", statValue.url);
        map.put("dbType", statValue.getDbType());
        map.put("name", statValue.getName());
        map.put("activeCount", statValue.getActiveCount());

        if (statValue.getActivePeak() > 0) {
            map.put("activePeak", statValue.getActivePeak());
            map.put("activePeakTime", statValue.getActivePeakTime());
        }
        map.put("poolingCount", statValue.getPoolingCount());
        if (statValue.getPoolingPeak() > 0) {
            map.put("poolingPeak", statValue.getPoolingPeak());
            map.put("poolingPeakTime", statValue.getPoolingPeakTime());
        }
        map.put("connectCount", statValue.getConnectCount());
        map.put("closeCount", statValue.getCloseCount());

        if (statValue.getWaitThreadCount() > 0) {
            map.put("waitThreadCount", statValue.getWaitThreadCount());
        }

        if (statValue.getNotEmptyWaitCount() > 0) {
            map.put("notEmptyWaitCount", statValue.getNotEmptyWaitCount());
        }

        if (statValue.getNotEmptyWaitMillis() > 0) {
            map.put("notEmptyWaitMillis", statValue.getNotEmptyWaitMillis());
        }

        if (statValue.getLogicConnectErrorCount() > 0) {
            map.put("logicConnectErrorCount", statValue.getLogicConnectErrorCount());
        }

        if (statValue.getPhysicalConnectCount() > 0) {
            map.put("physicalConnectCount", statValue.getPhysicalConnectCount());
        }

        if (statValue.getPhysicalCloseCount() > 0) {
            map.put("physicalCloseCount", statValue.getPhysicalCloseCount());
        }

        if (statValue.getPhysicalConnectErrorCount() > 0) {
            map.put("physicalConnectErrorCount", statValue.getPhysicalConnectErrorCount());
        }

        if (statValue.getExecuteCount() > 0) {
            map.put("executeCount", statValue.getExecuteCount());
        }

        if (statValue.getErrorCount() > 0) {
            map.put("errorCount", statValue.getErrorCount());
        }

        if (statValue.getCommitCount() > 0) {
            map.put("commitCount", statValue.getCommitCount());
        }

        if (statValue.getRollbackCount() > 0) {
            map.put("rollbackCount", statValue.getRollbackCount());
        }

        if (statValue.getPstmtCacheHitCount() > 0) {
            map.put("pstmtCacheHitCount", statValue.getPstmtCacheHitCount());
        }

        if (statValue.getPstmtCacheMissCount() > 0) {
            map.put("pstmtCacheMissCount", statValue.getPstmtCacheMissCount());
        }

        if (statValue.getStartTransactionCount() > 0) {
            map.put("startTransactionCount", statValue.getStartTransactionCount());
            map.put("transactionHistogram", rtrim(statValue.getTransactionHistogram()));
        }

        if (statValue.getConnectCount() > 0) {
            map.put("connectionHoldTimeHistogram", rtrim(statValue.getConnectionHoldTimeHistogram()));
        }

        if (statValue.getClobOpenCount() > 0) {
            map.put("clobOpenCount", statValue.getClobOpenCount());
        }

        if (statValue.getBlobOpenCount() > 0) {
            map.put("blobOpenCount", statValue.getBlobOpenCount());
        }

        if (statValue.getSqlSkipCount() > 0) {
            map.put("sqlSkipCount", statValue.getSqlSkipCount());
        }

        ArrayList<Map<String, Object>> sqlList = new ArrayList<Map<String, Object>>();
        if (statValue.sqlList.size() > 0) {
            for (JdbcSqlStatValue sqlStat : statValue.getSqlList()) {
                Map<String, Object> sqlStatMap = new LinkedHashMap<String, Object>();
                sqlStatMap.put("sql", sqlStat.getSql());

                if (sqlStat.getExecuteCount() > 0) {
                    sqlStatMap.put("executeCount", sqlStat.getExecuteCount());
                    sqlStatMap.put("executeMillisMax", sqlStat.getExecuteMillisMax());
                    sqlStatMap.put("executeMillisTotal", sqlStat.getExecuteMillisTotal());

                    sqlStatMap.put("executeHistogram", rtrim(sqlStat.getExecuteHistogram()));
                    sqlStatMap.put("executeAndResultHoldHistogram", rtrim(sqlStat.getExecuteAndResultHoldHistogram()));
                }

                long executeErrorCount = sqlStat.getExecuteErrorCount();
                if (executeErrorCount > 0) {
                    sqlStatMap.put("executeErrorCount", executeErrorCount);
                }

                int runningCount = sqlStat.getRunningCount();
                if (runningCount > 0) {
                    sqlStatMap.put("runningCount", runningCount);
                }

                int concurrentMax = sqlStat.getConcurrentMax();
                if (concurrentMax > 0) {
                    sqlStatMap.put("concurrentMax", concurrentMax);
                }

                if (sqlStat.getFetchRowCount() > 0) {
                    sqlStatMap.put("fetchRowCount", sqlStat.getFetchRowCount());
                    sqlStatMap.put("fetchRowCount", sqlStat.getFetchRowCountMax());
                    sqlStatMap.put("fetchRowHistogram", rtrim(sqlStat.getFetchRowHistogram()));
                }

                if (sqlStat.getUpdateCount() > 0) {
                    sqlStatMap.put("updateCount", sqlStat.getUpdateCount());
                    sqlStatMap.put("updateCountMax", sqlStat.getUpdateCountMax());
                    sqlStatMap.put("updateHistogram", rtrim(sqlStat.getUpdateHistogram()));
                }

                if (sqlStat.getInTransactionCount() > 0) {
                    sqlStatMap.put("inTransactionCount", sqlStat.getInTransactionCount());
                }

                if (sqlStat.getClobOpenCount() > 0) {
                    sqlStatMap.put("clobOpenCount", sqlStat.getClobOpenCount());
                }

                if (sqlStat.getBlobOpenCount() > 0) {
                    sqlStatMap.put("blobOpenCount", sqlStat.getBlobOpenCount());
                }

                sqlList.add(sqlStatMap);
            }

            map.put("sqlList", sqlList);
        }

        String text = JSONUtils.toJSONString(map);

        log(text);
    }

}
