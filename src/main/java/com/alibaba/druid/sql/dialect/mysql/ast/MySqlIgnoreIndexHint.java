package com.alibaba.druid.sql.dialect.mysql.ast;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlIgnoreIndexHint extends MySqlIndexHintImpl {

    private static final long serialVersionUID = 1L;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getIndexList());
        }
        visitor.endVisit(this);
    }

}
