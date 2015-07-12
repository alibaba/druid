package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterViewRenameStatement extends SQLStatementImpl {

    private SQLName name;
    private SQLName to;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLName getTo() {
        return to;
    }

    public void setTo(SQLName to) {
        if (to != null) {
            to.setParent(this);
        }
        this.to = to;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, to);
        }
        visitor.endVisit(this);
    }
}
