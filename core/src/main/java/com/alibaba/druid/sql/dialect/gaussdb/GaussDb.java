package com.alibaba.druid.sql.dialect.gaussdb;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class GaussDb {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.gaussdb);
}
