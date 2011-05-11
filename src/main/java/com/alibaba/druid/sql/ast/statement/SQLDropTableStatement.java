package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDropTableStatement extends SQLStatementImpl {
    private static final long serialVersionUID = 1L;

    private SQLName name;

    public SQLDropTableStatement() {

    }

    public SQLDropTableStatement(SQLName name) {

        this.name = name;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    @Override
    public void output(StringBuffer buf) {
        buf.append("DROP TABLE ");
        this.name.output(buf);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }
}
