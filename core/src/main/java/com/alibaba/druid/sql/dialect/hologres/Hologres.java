package com.alibaba.druid.sql.dialect.hologres;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Hologres {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.hologres);
}
