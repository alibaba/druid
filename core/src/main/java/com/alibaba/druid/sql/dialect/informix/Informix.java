package com.alibaba.druid.sql.dialect.informix;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;

public class Informix {
    public static final SQLDialect DIALECT = SQLDialect.of(DbType.informix);
}
