package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

public class OdpsQueryAliasStatement extends OdpsStatementImpl {
    private String variant;
    private SQLSelectStatement statement;

    public OdpsQueryAliasStatement() {

    }

    public OdpsQueryAliasStatement(String variant, SQLSelectStatement statement) {
        this.variant = variant;
        this.statement = statement;
    }

    @Override
    protected void accept0(OdpsASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, statement);
        }
        v.endVisit(this);
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public SQLSelectStatement getStatement() {
        return statement;
    }

    public void setStatement(SQLSelectStatement statement) {
        this.statement = statement;
    }
}
