package com.alibaba.druid.sql.dialect.impala;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Impala {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.impala);
}
