package com.alibaba.druid.sql.dialect.teradata;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class TeraData {
    public static final SQLDialect dialect = SQLDialect.of(DbType.teradata);
}
