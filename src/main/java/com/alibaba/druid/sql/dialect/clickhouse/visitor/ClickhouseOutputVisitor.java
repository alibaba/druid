package com.alibaba.druid.sql.dialect.clickhouse.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class ClickhouseOutputVisitor extends SQLASTOutputVisitor implements ClickhouseVisitor {
    public ClickhouseOutputVisitor(Appendable appender) {
        super(appender);
    }

    public ClickhouseOutputVisitor(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public ClickhouseOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLWithSubqueryClause.Entry x) {
        if (x.getExpr() != null) {
            x.getExpr().accept(this);
        } else if (x.getSubQuery() != null) {
            print('(');
            println();
            SQLSelect query = x.getSubQuery();
            if (query != null) {
                query.accept(this);
            } else {
                x.getReturningStatement().accept(this);
            }
            println();
            print(')');
        }
        print(' ');
        print0(ucase ? "AS " : "as ");
        print0(x.getAlias());

        return false;
    }
}
