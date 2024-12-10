package com.alibaba.druid.sql.dialect.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class BQ {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.bigquery);
}
