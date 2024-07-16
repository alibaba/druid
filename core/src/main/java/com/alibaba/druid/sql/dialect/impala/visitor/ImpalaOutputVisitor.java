package com.alibaba.druid.sql.dialect.impala.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;

public class ImpalaOutputVisitor extends HiveOutputVisitor {
    public ImpalaOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.impala;
    }

    public ImpalaOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.impala;
    }

    @Override
    protected void printJoinHint(SQLJoinTableSource x) {
        if (!x.getHints().isEmpty()) {
            print(' ');
            for (SQLHint joinHint : x.getHints()) {
                if (joinHint instanceof SQLCommentHint) {
                    print0((joinHint).toString());
                } else if (joinHint instanceof SQLExprHint) {
                    print0("[");
                    joinHint.accept(this);
                    print0("]");
                }
            }
        }
    }
}
