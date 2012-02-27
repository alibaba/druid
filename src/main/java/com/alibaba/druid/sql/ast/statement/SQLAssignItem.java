package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAssignItem extends SQLObjectImpl {

    private static final long serialVersionUID = 1L;

    private SQLExpr           target;
    private SQLExpr           value;

    public SQLAssignItem(){
    }

    public SQLAssignItem(SQLExpr target, SQLExpr value){
        this.target = target;
        this.value = value;
    }

    public SQLExpr getTarget() {
        return target;
    }

    public void setTarget(SQLExpr target) {
        this.target = target;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    public void output(StringBuffer buf) {
        target.output(buf);
        buf.append(" = ");
        value.output(buf);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.target);
            acceptChild(visitor, this.value);
        }
        visitor.endVisit(this);
    }

}