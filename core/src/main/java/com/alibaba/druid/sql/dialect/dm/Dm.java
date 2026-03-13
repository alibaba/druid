package com.alibaba.druid.sql.dialect.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Dm {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.dm);
}
