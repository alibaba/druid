package com.alibaba.druid.sql.dialect.sqlserver.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLServerSelectUnPivot extends SQLServerSelectBasePivot {

    private final List<SQLExpr> items = new ArrayList<>();

    private final List<SQLExpr> pivotIn = new ArrayList<>();


    public List<SQLExpr> getItems() {
        return items;
    }

    public List<SQLExpr> getPivotIn() {
        return pivotIn;
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.pivotFor);
            acceptChild(visitor, this.pivotIn);
        }

        visitor.endVisit(this);
    }
}
