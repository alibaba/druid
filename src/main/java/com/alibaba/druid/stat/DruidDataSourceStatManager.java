package com.alibaba.druid.stat;

import java.util.Set;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.ConcurrentIdentityHashMap;

public class DruidDataSourceStatManager implements DruidDataSourceStatManagerMBean {

    // global instances
    private static final Object                                             PRESENT   = new Object();
    private static final ConcurrentIdentityHashMap<DruidDataSource, Object> instances = new ConcurrentIdentityHashMap<DruidDataSource, Object>();

    public static void add(DruidDataSource dataSource) {
        instances.put(dataSource, PRESENT);
    }

    public static void remove(DruidDataSource dataSource) {
        instances.remove(dataSource);
    }

    public static Set<DruidDataSource> getInstances() {
        return instances.keySet();
    }
}
