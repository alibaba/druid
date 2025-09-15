package com.alibaba.druid.sql.dialect.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public final class MySQL {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.mysql);
}
