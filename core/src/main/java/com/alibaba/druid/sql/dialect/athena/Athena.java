package com.alibaba.druid.sql.dialect.athena;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Athena {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.athena);
}
