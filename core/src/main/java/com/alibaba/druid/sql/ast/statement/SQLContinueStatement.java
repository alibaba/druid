package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLContinueStatement extends SQLStatementImpl {
    @Override
    public void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
