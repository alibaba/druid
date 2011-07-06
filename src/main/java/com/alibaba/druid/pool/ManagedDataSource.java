package com.alibaba.druid.pool;

import javax.sql.DataSource;

public interface ManagedDataSource extends DataSource {

    boolean isEnable();

    void setEnable(boolean value);
}
