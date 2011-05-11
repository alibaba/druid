package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLUpdateSetItem extends SQLObjectImpl {
    private static final long serialVersionUID = 1L;

    private SQLName column;
    private SQLExpr value;

    public SQLUpdateSetItem() {

    }

    public SQLName getColumn() {
        return column;
    }

    public void setColumn(SQLName column) {
        this.column = column;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    public void output(StringBuffer buf) {
        column.output(buf);
        buf.append(" = ");
        value.output(buf);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, column);
            acceptChild(visitor, value);
        }

        visitor.endVisit(this);
    }

}
