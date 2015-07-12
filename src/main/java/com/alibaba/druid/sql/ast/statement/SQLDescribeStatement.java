package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDescribeStatement extends SQLStatementImpl {

    protected SQLName object;

    public SQLName getObject() {
        return object;
    }

    public void setObject(SQLName object) {
        this.object = object;
    }
    
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
}
