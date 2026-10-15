package com.alibaba.druid.sql.dialect.oscar;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Oscar {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.oscar);
}
