package com.alibaba.druid.sql.dialect.blink;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Blink {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.blink);
}
