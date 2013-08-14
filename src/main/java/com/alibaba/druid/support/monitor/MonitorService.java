package com.alibaba.druid.support.monitor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.druid.support.monitor.dao.MonitorDao;

public class MonitorService {

    private final static long        DEFAULT_TIME_BETWEEN_COLLECT = 60 * 5;

    private ScheduledExecutorService scheduler;
    private int                      schedulerThreadSize          = 1;

    private long                     timeBeetweenSqlCollect       = DEFAULT_TIME_BETWEEN_COLLECT;
    private long                     timeBeetweenSpringCollect    = DEFAULT_TIME_BETWEEN_COLLECT;
    private long                     timeBeetweenWebUriCollect    = DEFAULT_TIME_BETWEEN_COLLECT;
    private TimeUnit                 timeUnit                     = TimeUnit.SECONDS;

    private MonitorDao               dao;

    public void start() {
        if (scheduler == null) {
            scheduler = new ScheduledThreadPoolExecutor(schedulerThreadSize);
        }

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                collectSql();
            }
        }, timeBeetweenSqlCollect, timeBeetweenSqlCollect, timeUnit);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                collectSpringMethod();
            }
        }, timeBeetweenSpringCollect, timeBeetweenSpringCollect, timeUnit);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                collectWebURI();
            }
        }, timeBeetweenWebUriCollect, timeBeetweenWebUriCollect, timeUnit);
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    private void collectSql() {

    }

    private void collectSpringMethod() {

    }

    private void collectWebURI() {

    }

    public MonitorDao getDao() {
        return dao;
    }

    public void setDao(MonitorDao dao) {
        this.dao = dao;
    }

    public long getTimeBeetweenSqlCollect() {
        return timeBeetweenSqlCollect;
    }

    public void setTimeBeetweenSqlCollect(long timeBeetweenSqlCollect) {
        this.timeBeetweenSqlCollect = timeBeetweenSqlCollect;
    }

    public long getTimeBeetweenSpringCollect() {
        return timeBeetweenSpringCollect;
    }

    public void setTimeBeetweenSpringCollect(long timeBeetweenSpringCollect) {
        this.timeBeetweenSpringCollect = timeBeetweenSpringCollect;
    }

    public long getTimeBeetweenWebUriCollect() {
        return timeBeetweenWebUriCollect;
    }

    public void setTimeBeetweenWebUriCollect(long timeBeetweenWebUriCollect) {
        this.timeBeetweenWebUriCollect = timeBeetweenWebUriCollect;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

}
