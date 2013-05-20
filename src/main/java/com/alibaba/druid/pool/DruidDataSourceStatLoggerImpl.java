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
package com.alibaba.druid.pool;

import static com.alibaba.druid.util.JdbcSqlStatUtils.rtrim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class DruidDataSourceStatLoggerImpl implements DruidDataSourceStatLogger {

    private static Log LOG = LogFactory.getLog(DruidDataSourceStatLoggerImpl.class);

    @Override
    public void log(DruidDataSourceStatValue statValue) {
        if (LOG.isInfoEnabled()) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();

            map.put("url", statValue.url);
            map.put("dbType", statValue.dbType);
            map.put("name", statValue.name);
            map.put("activeCount", statValue.activeCount);

            if (statValue.activePeak > 0) {
                map.put("activePeak", statValue.activePeak);
                map.put("activePeakTime", statValue.getActivePeakTime());
            }
            map.put("poolingCount", statValue.poolingCount);
            if (statValue.poolingPeak > 0) {
                map.put("poolingPeak", statValue.poolingPeak);
                map.put("poolingPeakTime", statValue.getPoolingPeakTime());
            }
            map.put("connectCount", statValue.connectCount);
            map.put("closeCount", statValue.closeCount);

            if (statValue.waitThreadCount > 0) {
                map.put("waitThreadCount", statValue.waitThreadCount);
            }
            if (statValue.notEmptyWaitCount > 0) {
                map.put("notEmptyWaitCount", statValue.notEmptyWaitCount);
            }
            if (statValue.getNotEmptyWaitMillis() > 0) {
                map.put("notEmptyWaitMillis", statValue.getNotEmptyWaitMillis());
            }
            if (statValue.logicConnectErrorCount > 0) {
                map.put("logicConnectErrorCount", statValue.logicConnectErrorCount);
            }
            if (statValue.physicalConnectCount > 0) {
                map.put("physicalConnectCount", statValue.physicalConnectCount);
            }
            if (statValue.physicalCloseCount > 0) {
                map.put("physicalCloseCount", statValue.physicalCloseCount);
            }
            if (statValue.physicalConnectErrorCount > 0) {
                map.put("physicalConnectErrorCount", statValue.physicalConnectErrorCount);
            }
            if (statValue.executeCount > 0) {
                map.put("executeCount", statValue.executeCount);
            }
            if (statValue.errorCount > 0) {
                map.put("errorCount", statValue.errorCount);
            }
            if (statValue.commitCount > 0) {
                map.put("commitCount", statValue.commitCount);
            }
            if (statValue.rollbackCount > 0) {
                map.put("rollbackCount", statValue.rollbackCount);
            }
            if (statValue.pstmtCacheHitCount > 0) {
                map.put("pstmtCacheHitCount", statValue.pstmtCacheHitCount);
            }
            if (statValue.pstmtCacheMissCount > 0) {
                map.put("pstmtCacheMissCount", statValue.pstmtCacheMissCount);
            }
            if (statValue.startTransactionCount > 0) {
                map.put("startTransactionCount", statValue.startTransactionCount);
                map.put("transactionHistogram", rtrim(statValue.transactionHistogram));
            }
            if (statValue.connectCount > 0) {
                map.put("connectionHoldTimeHistogram", rtrim(statValue.connectionHoldTimeHistogram));
            }
            if (statValue.clobOpenCount > 0) {
                map.put("clobOpenCount", statValue.clobOpenCount);
            }
            if (statValue.blobOpenCount > 0) {
                map.put("blobOpenCount", statValue.blobOpenCount);
            }

            ArrayList<Map<String, Object>> sqlList = new ArrayList<Map<String, Object>>();
            if (statValue.sqlList.size() > 0) {
                for (JdbcSqlStatValue sqlStat : statValue.sqlList) {
                    Map<String, Object> sqlStatMap = new LinkedHashMap<String, Object>();
                    sqlStatMap.put("sql", sqlStat.getSql());

                    if (sqlStat.getExecuteCount() > 0) {
                        sqlStatMap.put("executeCount", sqlStat.getExecuteCount());
                        sqlStatMap.put("executeMillisMax", sqlStat.getExecuteMillisMax());
                        sqlStatMap.put("executeMillisTotal", sqlStat.getExecuteMillisTotal());

                        sqlStatMap.put("executeHistogram", rtrim(sqlStat.getExecuteHistogram()));
                        sqlStatMap.put("executeAndResultHoldHistogram",
                                       rtrim(sqlStat.getExecuteAndResultHoldHistogram()));
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

            LOG.info(text);
        }
    }

}
