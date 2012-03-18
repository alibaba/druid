package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

public class PGTruncateStatement extends SQLTruncateStatement implements PGSQLStatement{

    private static final long  serialVersionUID = 1L;

    private boolean            only;
    private Boolean            restartIdentity;
    private Boolean            cascade;

    public boolean isOnly() {
        return only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public Boolean getRestartIdentity() {
        return restartIdentity;
    }

    public void setRestartIdentity(Boolean restartIdentity) {
        this.restartIdentity = restartIdentity;
    }

    public Boolean getCascade() {
        return cascade;
    }

    public void setCascade(Boolean cascade) {
        this.cascade = cascade;
    }

    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSources);
        }
        visitor.endVisit(this);
    }
}
