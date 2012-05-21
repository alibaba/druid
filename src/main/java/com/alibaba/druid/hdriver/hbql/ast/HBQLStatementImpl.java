package com.alibaba.druid.hdriver.hbql.ast;

import com.alibaba.druid.hdriver.hbql.visitor.HBQLVisitor;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HBQLStatementImpl extends SQLStatementImpl {

    private static final long serialVersionUID = 1L;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof HBQLVisitor) {
            accept0((HBQLVisitor) visitor);
        } else {
            throw new IllegalArgumentException("not support visitor type : " + visitor.getClass().getName());
        }
    }

    public void accept0(HBQLVisitor visitor) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
}
