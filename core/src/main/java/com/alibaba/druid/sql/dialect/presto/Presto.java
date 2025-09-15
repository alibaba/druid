package com.alibaba.druid.sql.dialect.presto;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Presto {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.presto);
}
