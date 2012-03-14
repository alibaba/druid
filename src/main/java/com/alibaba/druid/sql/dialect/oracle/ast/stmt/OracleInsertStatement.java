package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleErrorLoggingClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleInsertStatement extends SQLInsertStatement implements OracleStatement {

    private static final long        serialVersionUID = 1L;

    private OracleReturningClause    returning;
    private OracleErrorLoggingClause errorLogging;
    private List<OracleHint>         hints            = new ArrayList<OracleHint>();

    public List<OracleHint> getHints() {
        return hints;
    }

    public void setHints(List<OracleHint> hints) {
        this.hints = hints;
    }

    public OracleReturningClause getReturning() {
        return returning;
    }

    public void setReturning(OracleReturningClause returning) {
        this.returning = returning;
    }

    public OracleErrorLoggingClause getErrorLogging() {
        return errorLogging;
    }

    public void setErrorLogging(OracleErrorLoggingClause errorLogging) {
        this.errorLogging = errorLogging;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSource);
            this.acceptChild(visitor, columns);
            this.acceptChild(visitor, values);
            this.acceptChild(visitor, query);
            this.acceptChild(visitor, returning);
            this.acceptChild(visitor, errorLogging);
        }

        visitor.endVisit(this);
    }
}
