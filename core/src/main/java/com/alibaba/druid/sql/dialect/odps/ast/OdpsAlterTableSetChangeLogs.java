package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

public class OdpsAlterTableSetChangeLogs extends OdpsObjectImpl
        implements SQLAlterTableItem {
    private SQLExpr value;

    @Override
    public void accept0(OdpsASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, value);
        }
        v.endVisit(this);
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.value = x;
    }
}
