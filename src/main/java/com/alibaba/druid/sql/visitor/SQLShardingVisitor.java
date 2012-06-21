package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;

public class SQLShardingVisitor extends SQLASTVisitorAdapter {
    
    public void postVisit(SQLObject x) {
    }

    public void preVisit(SQLObject x) {
    }

    public boolean visit(SQLInsertStatement x) {
        return false;
    }
}
