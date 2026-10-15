package com.alibaba.druid.sql.dialect.h2;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class H2 {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.h2);
}
