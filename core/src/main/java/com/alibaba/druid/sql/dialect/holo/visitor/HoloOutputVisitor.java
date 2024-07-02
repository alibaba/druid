package com.alibaba.druid.sql.dialect.holo.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;

public class HoloOutputVisitor extends PGOutputVisitor {
    public HoloOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.hologres;
    }

    public HoloOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.hologres;
    }
}
