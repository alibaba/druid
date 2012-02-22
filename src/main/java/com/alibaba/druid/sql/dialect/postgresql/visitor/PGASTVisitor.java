package com.alibaba.druid.sql.dialect.postgresql.visitor;

import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGTruncateStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface PGASTVisitor extends SQLASTVisitor {

    void endVisit(PGSelectQueryBlock.WindowClause x);

    boolean visit(PGSelectQueryBlock.WindowClause x);

    void endVisit(PGSelectQueryBlock.FetchClause x);

    boolean visit(PGSelectQueryBlock.FetchClause x);

    void endVisit(PGSelectQueryBlock.ForClause x);

    boolean visit(PGSelectQueryBlock.ForClause x);

    void endVisit(PGSelectQueryBlock.WithQuery x);

    boolean visit(PGSelectQueryBlock.WithQuery x);

    void endVisit(PGSelectQueryBlock.WithClause x);

    boolean visit(PGSelectQueryBlock.WithClause x);

    void endVisit(PGSelectQueryBlock.IntoClause x);

    boolean visit(PGSelectQueryBlock.IntoClause x);

    void endVisit(PGTruncateStatement x);

    boolean visit(PGTruncateStatement x);
}
