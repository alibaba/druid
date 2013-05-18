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
            map.put("activePeak", statValue.activePeak);

            map.put("activePeakTime", statValue.getActivePeakTime());
            map.put("poolingCount", statValue.poolingCount);
            map.put("poolingPeak", statValue.poolingPeak);
            map.put("poolingPeakTime", statValue.getPoolingPeakTime());
            map.put("connectCount", statValue.connectCount);

            map.put("closeCount", statValue.closeCount);
            map.put("waitThreadCount", statValue.waitThreadCount);
            map.put("notEmptyWaitCount", statValue.notEmptyWaitCount);
            map.put("notEmptyWaitMillis", statValue.getNotEmptyWaitMillis());
            map.put("logicConnectErrorCount", statValue.logicConnectErrorCount);

            map.put("physicalConnectCount", statValue.physicalConnectCount);
            map.put("physicalCloseCount", statValue.physicalCloseCount);
            map.put("physicalConnectErrorCount", statValue.physicalConnectErrorCount);
            map.put("executeCount", statValue.executeCount);
            if (statValue.errorCount > 0) {
                map.put("errorCount", statValue.errorCount);
            }
            if (statValue.commitCount > 0) {
                map.put("commitCount", statValue.commitCount);
            }
            if (statValue.rollbackCount > 0) {
                map.put("rollbackCount", statValue.rollbackCount);
            }
            map.put("pstmtCacheHitCount", statValue.pstmtCacheHitCount);
            map.put("pstmtCacheMissCount", statValue.pstmtCacheMissCount);
            if (statValue.startTransactionCount > 0) {
                map.put("startTransactionCount", statValue.startTransactionCount);
                map.put("transactionHistogram", statValue.transactionHistogram);
            }
            map.put("connectionHoldTimeHistogram", statValue.connectionHoldTimeHistogram);
            if (statValue.clobOpenCount > 0) {
                map.put("clobOpenCount", statValue.clobOpenCount);
            }
            if (statValue.blobOpenCount > 0) {
                map.put("blobOpenCount", statValue.blobOpenCount);
            }

            ArrayList<Map<String, Object>> sqlList = new ArrayList<Map<String, Object>>();
            for (JdbcSqlStatValue sqlStat : statValue.sqlList) {
                Map<String, Object> sqlStatMap = new LinkedHashMap<String, Object>();
                sqlStatMap.put("sql", sqlStat.sql);
                sqlStatMap.put("executeCount", sqlStat.getExecuteCount());
                if (sqlStat.executeErrorCount > 0) {
                    sqlStatMap.put("executeErrorCount", sqlStat.executeErrorCount);
                }
                if (sqlStat.runningCount > 0) {
                    sqlStatMap.put("runningCount", sqlStat.runningCount);
                }
                sqlStatMap.put("concurrentMax", sqlStat.concurrentMax);

                sqlStatMap.put("executeMillisMax", sqlStat.getExecuteMillisMax());
                sqlStatMap.put("executeMillisTotal", sqlStat.getExecuteMillisTotal());

                sqlStatMap.put("executeHistogram", sqlStat.getExecuteHistogram());
                sqlStatMap.put("executeAndResultHoldHistogram", sqlStat.getExecuteAndResultHoldHistogram());
                if (sqlStat.fetchRowCount > 0) {
                    sqlStatMap.put("fetchRowCount", sqlStat.fetchRowCount);
                    sqlStatMap.put("fetchRowCount", sqlStat.fetchRowCountMax);
                    sqlStatMap.put("fetchRowHistogram", sqlStat.getFetchRowHistogram());
                }

                if (sqlStat.updateCount > 0) {
                    sqlStatMap.put("updateCount", sqlStat.updateCount);
                    sqlStatMap.put("updateCountMax", sqlStat.updateCountMax);
                    sqlStatMap.put("updateHistogram", sqlStat.getUpdateHistogram());
                }

                if (sqlStat.inTransactionCount > 0) {
                    sqlStatMap.put("inTransactionCount", sqlStat.inTransactionCount);
                }

                if (sqlStat.clobOpenCount > 0) {
                    sqlStatMap.put("clobOpenCount", sqlStat.clobOpenCount);
                }

                if (sqlStat.blobOpenCount > 0) {
                    sqlStatMap.put("blobOpenCount", sqlStat.blobOpenCount);
                }

            }

            String text = JSONUtils.toJSONString(map);

            LOG.info(text);
        }
    }

}
