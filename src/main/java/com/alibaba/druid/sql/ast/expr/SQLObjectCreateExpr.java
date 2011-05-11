package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLObjectCreateExpr extends SQLExprImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    public String objType;
    public final List<SQLExpr> paramList = new ArrayList<SQLExpr>();

    public SQLObjectCreateExpr() {

    }

    public SQLObjectCreateExpr(String objType) {

        this.objType = objType;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.paramList);
        }

        visitor.endVisit(this);
    }
}
