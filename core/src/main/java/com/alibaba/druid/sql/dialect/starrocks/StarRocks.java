package com.alibaba.druid.sql.dialect.starrocks;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class StarRocks {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.starrocks);
}
