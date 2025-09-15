package com.alibaba.druid.sql.dialect.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Oracle {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.oracle);
}
