package com.alibaba.druid.sql.dialect.postgresql.visitor;

import com.alibaba.druid.sql.dialect.postgresql.ast.PGAggregateExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithQuery;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGAnalytic;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGParameter;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGFunctionTableSource;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGTruncateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface PGASTVisitor extends SQLASTVisitor {

    void endVisit(PGSelectQueryBlock x);

    boolean visit(PGSelectQueryBlock x);

    void endVisit(PGSelectQueryBlock.WindowClause x);

    boolean visit(PGSelectQueryBlock.WindowClause x);

    void endVisit(PGSelectQueryBlock.FetchClause x);

    boolean visit(PGSelectQueryBlock.FetchClause x);

    void endVisit(PGSelectQueryBlock.ForClause x);

    boolean visit(PGSelectQueryBlock.ForClause x);

    void endVisit(PGWithQuery x);

    boolean visit(PGWithQuery x);

    void endVisit(PGWithClause x);

    boolean visit(PGWithClause x);

    void endVisit(PGTruncateStatement x);

    boolean visit(PGTruncateStatement x);

    void endVisit(PGDeleteStatement x);

    boolean visit(PGDeleteStatement x);

    void endVisit(PGInsertStatement x);

    boolean visit(PGInsertStatement x);

    void endVisit(PGSelectStatement x);

    boolean visit(PGSelectStatement x);

    void endVisit(PGUpdateStatement x);

    boolean visit(PGUpdateStatement x);

    void endVisit(PGAggregateExpr x);

    boolean visit(PGAggregateExpr x);

    void endVisit(PGAnalytic x);

    boolean visit(PGAnalytic x);

    void endVisit(PGParameter x);

    boolean visit(PGParameter x);

    void endVisit(PGFunctionTableSource x);

    boolean visit(PGFunctionTableSource x);

}
