package com.alibaba.druid.stat.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        final long timeMillis = System.currentTimeMillis();

        List<DataSourceInfo> dataSourceStatList = new ArrayList<DataSourceInfo>();
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            DataSourceInfo dataSourceStat = new DataSourceInfo();

            dataSourceStat.setUrl(dataSource.getUrl());
            dataSourceStat.setDbType(dataSource.getDbType());

            dataSourceStatList.add(dataSourceStat);
            
            Collection<JdbcSqlStat> sqlStats = dataSource.getDataSourceStat().getSqlStatMap().values();
            
            List<SqlInfo> sqlStatInfoList = new ArrayList<SqlInfo>(sqlStats.size());
            for (JdbcSqlStat sqlStat : dataSource.getDataSourceStat().getSqlStatMap().values()) {
                SqlInfo sqlStatInfo = new SqlInfo();
                
                sqlStatInfo.setSql(sqlStat.getSql());
                
                sqlStatInfoList.add(sqlStatInfo);
            }
        }

        store.saveDataSource(timeMillis, dataSourceStatList);
    }

    private final class CollectTask implements Runnable {

        public void run() {
            collect();
        }
    }

}
