package com.alibaba.druid.util;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class OdpsUtils {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.odps);
}
