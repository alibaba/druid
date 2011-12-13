package com.alibaba.druid.pool;

import javax.management.ObjectName;
import javax.sql.DataSource;

public interface ManagedDataSource extends DataSource {

    boolean isEnable();

    void setEnable(boolean value);

    ObjectName getObjectName();

    void setObjectName(ObjectName objectName);
}
