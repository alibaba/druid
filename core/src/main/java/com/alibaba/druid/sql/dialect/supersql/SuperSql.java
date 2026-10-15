package com.alibaba.druid.sql.dialect.supersql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class SuperSql {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.supersql);
}
