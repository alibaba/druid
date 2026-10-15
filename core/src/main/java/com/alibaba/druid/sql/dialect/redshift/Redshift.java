package com.alibaba.druid.sql.dialect.redshift;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Redshift {
    public static final SQLDialect dialect = SQLDialect.of(DbType.redshift);
}
