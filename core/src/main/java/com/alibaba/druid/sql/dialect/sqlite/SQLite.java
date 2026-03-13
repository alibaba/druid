package com.alibaba.druid.sql.dialect.sqlite;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class SQLite {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.sqlite);
}
