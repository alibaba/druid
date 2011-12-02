package com.alibaba.druid.pool.ha.valid;

import com.alibaba.druid.pool.DruidDataSource;


public interface ValidDataSourceChecker {
    boolean isValid(DruidDataSource dataSource);
}
