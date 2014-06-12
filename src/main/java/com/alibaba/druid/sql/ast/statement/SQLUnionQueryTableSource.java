package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLUnionQueryTableSource extends SQLTableSourceImpl {

    private SQLUnionQuery union;

    public SQLUnionQueryTableSource(){

    }

    public SQLUnionQueryTableSource(String alias){
        super(alias);
    }

    public SQLUnionQueryTableSource(SQLUnionQuery union, String alias){
        super(alias);
        this.setUnion(union);
    }

    public SQLUnionQueryTableSource(SQLUnionQuery union){
        this.setUnion(union);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, union);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("(");
        this.union.output(buf);
        buf.append(")");
    }

    public SQLUnionQuery getUnion() {
        return union;
    }

    public void setUnion(SQLUnionQuery union) {
        if (union != null) {
            union.setParent(this);
        }
        this.union = union;
    }
}
