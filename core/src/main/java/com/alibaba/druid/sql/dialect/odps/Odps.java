package com.alibaba.druid.sql.dialect.odps;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Odps {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.odps);
}
