package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLRevokeStatement extends SQLStatementImpl {

    private final List<SQLExpr> privileges = new ArrayList<SQLExpr>();

    private SQLObject           on;
    private SQLExpr             from;
    // mysql
    private SQLObjectType       objectType;

    public SQLRevokeStatement(){

    }

    public SQLRevokeStatement(String dbType){
        super(dbType);
    }

    public SQLObject getOn() {
        return on;
    }

    public void setOn(SQLObject on) {
        this.on = on;
    }

    public SQLExpr getFrom() {
        return from;
    }

    public void setFrom(SQLExpr from) {
        this.from = from;
    }

    public List<SQLExpr> getPrivileges() {
        return privileges;
    }

    public SQLObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(SQLObjectType objectType) {
        this.objectType = objectType;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, on);
            acceptChild(visitor, from);
        }
        visitor.endVisit(this);
    }
}
