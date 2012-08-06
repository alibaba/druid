package com.alibaba.druid.stat;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.ReflectionUtils;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class DruidStatManagerFacade {

    private final static DruidStatManagerFacade instance = new DruidStatManagerFacade();

    private boolean                             inited   = false;

    protected final ReentrantLock               lock     = new ReentrantLock(true);

    private DruidDataSourceStatManager          druidDataSourceStatManager;
    private JdbcStatManager                     jdbcStatManager;

    private Set<DruidDataSource>                datasourceInstances;

    private DruidStatManagerFacade() {
    }

    public static DruidStatManagerFacade getInstance() {
        return instance;
    }

    private void init() {
        if (inited) {
            return;
        }

        lock.lock();
        if (inited) {
            return;
        }

        try {
            initByClassLoader();

            inited = true;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private void initByClassLoader() {
        Class<?> druidDataSourceStatManagerClass = ReflectionUtils.getClassFromWebContainerOrCurrentClassLoader("com.alibaba.druid.stat.DruidDataSourceStatManager");

        druidDataSourceStatManager = (DruidDataSourceStatManager) ReflectionUtils.callStaticMethod(druidDataSourceStatManagerClass,
                                                                                                   "getInstance");
        datasourceInstances = (Set<DruidDataSource>) ReflectionUtils.callStaticMethod(druidDataSourceStatManagerClass,
                                                                                      "getDruidDataSourceInstances");

        Class<?> jdbcStatManagerClass = ReflectionUtils.getClassFromWebContainerOrCurrentClassLoader("com.alibaba.druid.stat.JdbcStatManager");
        jdbcStatManager = (JdbcStatManager) ReflectionUtils.callStaticMethod(jdbcStatManagerClass, "getInstance");

    }

    public Set<DruidDataSource> getDruidDataSourceInstances() {
        init();
        return datasourceInstances;
    }

    public void resetDataSourceStat() {
        init();
        druidDataSourceStatManager.reset();
    }

    public void resetSqlStat() {
        init();
        jdbcStatManager.reset();
    }

    public void resetAll() {
        init();
        resetSqlStat();
        resetDataSourceStat();

    }
}
