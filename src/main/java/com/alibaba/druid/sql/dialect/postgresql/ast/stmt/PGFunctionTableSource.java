package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSQLObject;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGParameter;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PGFunctionTableSource extends SQLExprTableSource implements PGSQLObject {

    private static final long       serialVersionUID = 1L;

    private final List<PGParameter> parameters       = new ArrayList<PGParameter>();

    public PGFunctionTableSource(){

    }

    public PGFunctionTableSource(SQLExpr expr){
        this.expr = expr;
    }

    public List<PGParameter> getParameters() {
        return parameters;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((PGASTVisitor) visitor);
    }

    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
            acceptChild(visitor, this.parameters);
        }
        visitor.endVisit(this);
    }
}
