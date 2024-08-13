package com.alibaba.druid.sql.dialect.hologres.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;

public class HologresOutputVisitor extends PGOutputVisitor {
    public HologresOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.hologres;
    }

    public HologresOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.hologres;
    }
}
