package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableSetFileFormat extends SQLObjectImpl implements SQLAlterTableItem {
    private SQLExpr value;

    @Override
    public void accept0(SQLASTVisitor v) {
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
