package com.alibaba.druid.sql.dialect.doris;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Doris {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.doris);
}
