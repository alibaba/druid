package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleAlterProcedureStatement extends OracleStatementImpl {

    private SQLExpr           name;

    private boolean           compile          = false;
    private boolean           reuseSettings    = false;

    private static final long serialVersionUID = 1L;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr name) {
        this.name = name;
    }

    public boolean isCompile() {
        return compile;
    }

    public void setCompile(boolean compile) {
        this.compile = compile;
    }

    public boolean isReuseSettings() {
        return reuseSettings;
    }

    public void setReuseSettings(boolean reuseSettings) {
        this.reuseSettings = reuseSettings;
    }

}
