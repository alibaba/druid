package com.alibaba.druid.sql.dialect.clickhouse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class CK {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.clickhouse);
}
