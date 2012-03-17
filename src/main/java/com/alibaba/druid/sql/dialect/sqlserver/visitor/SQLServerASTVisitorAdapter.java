package com.alibaba.druid.sql.dialect.sqlserver.visitor;

import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.Top;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class SQLServerASTVisitorAdapter extends SQLASTVisitorAdapter implements SQLServerASTVisitor {

    @Override
    public boolean visit(SQLServerSelectQueryBlock x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerSelectQueryBlock x) {

    }

    @Override
    public boolean visit(Top x) {
        return true;
    }

    @Override
    public void endVisit(Top x) {

    }

}
