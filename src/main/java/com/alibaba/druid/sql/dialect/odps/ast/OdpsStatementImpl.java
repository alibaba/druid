package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;


public abstract class OdpsStatementImpl extends SQLStatementImpl {
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OdpsASTVisitor) visitor);
    }
    
    protected abstract void accept0(OdpsASTVisitor visitor);
    
    public String toString() {
        return SQLUtils.toOdpsString(this);
    }
}
