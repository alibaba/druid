package com.alibaba.druid.pool;

import java.util.Set;

import com.alibaba.druid.util.ConcurrentIdentityHashMap;

public class Global {

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
