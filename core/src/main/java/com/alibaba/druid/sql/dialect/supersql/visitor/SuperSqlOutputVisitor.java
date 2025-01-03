package com.alibaba.druid.sql.dialect.supersql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoOutputVisitor;

public class SuperSqlOutputVisitor extends PrestoOutputVisitor implements SuperSqlASTVisitor {
    public SuperSqlOutputVisitor(StringBuilder appender) {
        super(appender, DbType.supersql);
    }

    public SuperSqlOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, DbType.supersql, parameterized);
    }
}
