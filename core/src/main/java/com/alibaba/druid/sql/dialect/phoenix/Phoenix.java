package com.alibaba.druid.sql.dialect.phoenix;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Phoenix {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.phoenix);
}
