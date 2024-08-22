package com.alibaba.druid.sql.dialect.doris.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.doris.visitor.DorisASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class DorisExprTableSource extends SQLExprTableSource implements DorisObject {
    protected List<SQLExpr> tablets;
    protected SQLExpr repeatable;

    public void addTablet(SQLExpr tablet) {
        if (tablets == null) {
            tablets = new ArrayList<>(2);
        }

        if (tablet != null) {
            tablet.setParent(this);
            tablets.add(tablet);
        }
    }

    public List<SQLExpr> getTablets() {
        if (tablets == null) {
            tablets = new ArrayList<>(2);
        }
        return tablets;
    }

    public SQLExpr getRepeatable() {
        return repeatable;
    }

    public void setRepeatable(SQLExpr repeatable) {
        if (repeatable != null) {
            repeatable.setParent(this);
        }
        this.repeatable = repeatable;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof DorisASTVisitor) {
            accept0((DorisASTVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    @Override
    public void accept0(DorisASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.tablets);
            acceptChild(visitor, this.repeatable);
        }
    }
}
