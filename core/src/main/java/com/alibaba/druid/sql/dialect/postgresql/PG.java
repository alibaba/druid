package com.alibaba.druid.sql.dialect.postgresql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class PG {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.postgresql);
}
