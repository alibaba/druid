package com.alibaba.druid.stat.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.stat.service.dto.DataSourceInfo;
import com.alibaba.druid.stat.service.dto.SqlInfo;
import com.alibaba.druid.stat.service.impl.DruidStatMemoryStore;

public class DruidStatService {

    private ScheduledExecutorService scheduler;

    private ScheduledFuture<?>       collectPlan;

    private int                      collectPeriodSeconds = 60 * 5;

    private DruidStatStore           store;

    public int getCollectPeriodSeconds() {
        return collectPeriodSeconds;
    }

    public void setCollectPeriodSeconds(int collectPeriodSeconds) {
        this.collectPeriodSeconds = collectPeriodSeconds;
    }

    public synchronized void start() {
        if (store != null) {
            store = new DruidStatMemoryStore();
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        collectPlan = scheduler.scheduleAtFixedRate(new CollectTask(), collectPeriodSeconds, collectPeriodSeconds,
                                                    TimeUnit.SECONDS);
    }

    public synchronized void stop() {
        if (collectPlan == null) {
            collectPlan.cancel(true);
        }
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    public void collect() {
        List<DataSourceInfo> dataSourceStatList = new ArrayList<DataSourceInfo>();
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            DataSourceInfo dataSourceStat = new DataSourceInfo();

            dataSourceStat.setCollectTimeMillis(System.currentTimeMillis());

            dataSourceStat.setId(dataSource.getID());
            dataSourceStat.setUrl(dataSource.getUrl());
            dataSourceStat.setDbType(dataSource.getDbType());

            dataSourceStat.setActiveCount(dataSource.getActiveCount());
            dataSourceStat.setActivePeak(dataSource.getActivePeak());
            dataSourceStat.setCloseCount((int) dataSource.getCloseCount());
            dataSourceStat.setConnectCount((int) dataSource.getConnectCount());
            dataSourceStat.setConnectErrorCount((int) dataSource.getConnectErrorCount());
            dataSourceStat.setCreateCount((int) dataSource.getCreateCount());
            dataSourceStat.setCreateErrorCount((int) dataSource.getCreateErrorCount());
            dataSourceStat.setDestoryCount((int) dataSource.getDestroyCount());
            dataSourceStat.setExecuteCount((int) dataSource.getExecuteCount());
            dataSourceStat.setPoolingCount(dataSource.getPoolingCount());
            dataSourceStat.setConnectionHoldHistogram(dataSource.getDataSourceStat().getConnectionHistogramValues());
            dataSourceStat.setTransactionHistogram(dataSource.getTransactionHistogramValues());

            Collection<JdbcSqlStat> sqlStats = dataSource.getDataSourceStat().getSqlStatMap().values();

            Map<String, SqlInfo> sqlStatInfoMap = new HashMap<String, SqlInfo>(sqlStats.size());
            for (JdbcSqlStat sqlStat : dataSource.getDataSourceStat().getSqlStatMap().values()) {

                if (sqlStat.getExecuteCount() == 0 && sqlStat.getRunningCount() == 0) {
                    continue;
                }

                SqlInfo sqlStatInfo = DruidStatServiceUtils.createSqlInfo(sqlStat);

                SqlInfo oldsqlStatInfo = sqlStatInfoMap.get(sqlStatInfo.getSql());
                if (oldsqlStatInfo != null) {
                    oldsqlStatInfo.merge(sqlStatInfo);
                } else {
                    sqlStatInfoMap.put(sqlStatInfo.getSql(), sqlStatInfo);
                }
            }

            dataSourceStat.setSqlList(new ArrayList<SqlInfo>(sqlStatInfoMap.values()));

            dataSourceStatList.add(dataSourceStat);
        }

        store.saveDataSource(dataSourceStatList);
    }

    private final class CollectTask implements Runnable {

        public void run() {
            collect();
        }
    }

}
