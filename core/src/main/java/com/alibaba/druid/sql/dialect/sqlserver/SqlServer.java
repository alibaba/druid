package com.alibaba.druid.sql.dialect.sqlserver;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class SqlServer {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.sqlserver);
}
