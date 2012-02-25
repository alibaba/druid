package com.alibaba.druid.sql.dialect.hive.ast.stmt;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.hive.ast.HiveStatementImpl;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;

public class HiveShowTablesStatement extends HiveStatementImpl {

    private static final long serialVersionUID = 1L;
    private SQLCharExpr       pattern;


    public SQLCharExpr getPattern() {
        return pattern;
    }

    public void setPattern(SQLCharExpr pattern) {
        this.pattern = pattern;
    }

    @Override
    public void accept0(HiveASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, pattern);
        }
        visitor.endVisit(this);
    }

}
