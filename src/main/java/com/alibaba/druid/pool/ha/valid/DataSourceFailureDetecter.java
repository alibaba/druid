package com.alibaba.druid.pool.ha.valid;

import com.alibaba.druid.pool.DruidDataSource;


public interface DataSourceFailureDetecter {
    boolean isValid(DruidDataSource dataSource);
}
