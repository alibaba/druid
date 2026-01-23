package com.alibaba.druid.sql.dialect.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Hive {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.hive);
}
