package com.alibaba.druid.sql.dialect.synapse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Synapse {
    public static final SQLDialect dialect = SQLDialect.of(DbType.synapse);
}
