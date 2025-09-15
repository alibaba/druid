package com.alibaba.druid.sql.dialect.supersql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoOutputVisitor;
import com.alibaba.druid.sql.dialect.supersql.SuperSql;

public class SuperSqlOutputVisitor extends PrestoOutputVisitor implements SuperSqlASTVisitor {
    public SuperSqlOutputVisitor(StringBuilder appender) {
        super(appender, DbType.supersql, SuperSql.DIALECT);
    }

    public SuperSqlOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, DbType.supersql, SuperSql.DIALECT, parameterized);
    }

    @Override
    public void printInsertOverWrite(SQLInsertStatement x) {
        print0(ucase ? "INSERT OVERWRITE TABLE " : "insert overwrite table ");
    }
}
