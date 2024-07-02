package com.alibaba.druid.sql.dialect.infomix.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class InformixOutputVisitor extends SQLASTOutputVisitor {
    public InformixOutputVisitor(StringBuilder appender) {
        this(appender, false);
    }

    public InformixOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.informix;
    }

    protected void printSelectListBefore(SQLSelectQueryBlock x) {
        print(' ');

        SQLLimit limit = x.getLimit();
        if (limit == null) {
            return;
        }

        SQLExpr offset = limit.getOffset();
        SQLExpr first = limit.getRowCount();
        if (offset != null) {
            print0(ucase ? "SKIP " : "skip ");
            offset.accept(this);
        }

        print0(ucase ? " FIRST " : " first ");
        first.accept(this);
        print(' ');
    }

    protected void printFetchFirst(SQLSelectQueryBlock x) {
        // ignore
    }
}
