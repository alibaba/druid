package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowCreateFunctionStatement extends MySqlStatementImpl {

    private static final long serialVersionUID = 1L;

    private SQLExpr           functionName;

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, functionName);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getFunctionName() {
        return functionName;
    }

    public void setFunctionName(SQLExpr functionName) {
        this.functionName = functionName;
    }

}
