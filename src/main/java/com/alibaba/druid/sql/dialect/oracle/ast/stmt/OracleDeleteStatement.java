package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleDeleteStatement extends SQLDeleteStatement {
    private static final long serialVersionUID = 1L;

    private boolean only = false;
    private String alias;
    private final List<OracleHint> hints = new ArrayList<OracleHint>();

    public OracleDeleteStatement() {

    }

    public List<OracleHint> getHints() {
        return this.hints;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.getTableName());
            acceptChild(visitor, this.getWhere());
        }

        visitor.endVisit(this);
    }

    public boolean isOnly() {
        return this.only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
