package com.alibaba.druid.sql.dialect.postgresql.visitor;

import com.alibaba.druid.sql.dialect.postgresql.ast.PGCurrentOfExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.FetchClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.ForClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.IntoClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WindowClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGTruncateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithQuery;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class PGASTVisitorAdapter extends SQLASTVisitorAdapter implements PGASTVisitor {

    @Override
    public void endVisit(WindowClause x) {

    }

    @Override
    public boolean visit(WindowClause x) {

        return true;
    }

    @Override
    public void endVisit(FetchClause x) {

    }

    @Override
    public boolean visit(FetchClause x) {

        return true;
    }

    @Override
    public void endVisit(ForClause x) {

    }

    @Override
    public boolean visit(ForClause x) {

        return true;
    }

    @Override
    public void endVisit(PGWithQuery x) {

    }

    @Override
    public boolean visit(PGWithQuery x) {

        return true;
    }

    @Override
    public void endVisit(PGWithClause x) {

    }

    @Override
    public boolean visit(PGWithClause x) {

        return true;
    }

    @Override
    public void endVisit(IntoClause x) {

    }

    @Override
    public boolean visit(IntoClause x) {

        return true;
    }

    @Override
    public void endVisit(PGTruncateStatement x) {

    }

    @Override
    public boolean visit(PGTruncateStatement x) {

        return true;
    }
    
    @Override
    public void endVisit(PGDeleteStatement x) {
        
    }
    
    @Override
    public boolean visit(PGDeleteStatement x) {
        
        return true;
    }
    
    @Override
    public void endVisit(PGCurrentOfExpr x) {
        
    }
    
    @Override
    public boolean visit(PGCurrentOfExpr x) {
        return true;
    }

}
