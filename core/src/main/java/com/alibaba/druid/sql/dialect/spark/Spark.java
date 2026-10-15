package com.alibaba.druid.sql.dialect.spark;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Spark {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.spark);
}
