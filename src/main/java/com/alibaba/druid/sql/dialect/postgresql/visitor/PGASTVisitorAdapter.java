package com.alibaba.druid.sql.dialect.postgresql.visitor;

import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.FetchClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.ForClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.IntoClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WindowClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WithClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WithQuery;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGTruncateStatement;
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
    public void endVisit(WithQuery x) {

    }

    @Override
    public boolean visit(WithQuery x) {

        return true;
    }

    @Override
    public void endVisit(WithClause x) {

    }

    @Override
    public boolean visit(WithClause x) {

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

}
