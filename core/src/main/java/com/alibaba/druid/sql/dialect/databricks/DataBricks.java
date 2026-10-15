package com.alibaba.druid.sql.dialect.databricks;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class DataBricks {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.databricks);
}
