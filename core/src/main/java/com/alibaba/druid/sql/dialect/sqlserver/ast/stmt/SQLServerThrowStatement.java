package com.alibaba.druid.sql.dialect.sqlserver.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLServerThrowStatement extends SQLStatementImpl implements SQLServerStatement {
    private SQLExpr errorNumber;
    private SQLExpr message;
    private SQLExpr state;

    public SQLServerThrowStatement() {
        super(DbType.sqlserver);
    }

    public SQLExpr getErrorNumber() {
        return errorNumber;
    }

    public void setErrorNumber(SQLExpr errorNumber) {
        if (errorNumber != null) {
            errorNumber.setParent(this);
        }
        this.errorNumber = errorNumber;
    }

    public SQLExpr getMessage() {
        return message;
    }

    public void setMessage(SQLExpr message) {
        if (message != null) {
            message.setParent(this);
        }
        this.message = message;
    }

    public SQLExpr getState() {
        return state;
    }

    public void setState(SQLExpr state) {
        if (state != null) {
            state.setParent(this);
        }
        this.state = state;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof SQLServerASTVisitor) {
            accept0((SQLServerASTVisitor) visitor);
        }
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, errorNumber);
            acceptChild(visitor, message);
            acceptChild(visitor, state);
        }
        visitor.endVisit(this);
    }
}
