package com.alibaba.druid.sql.dialect.hive.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HiveCreateFunctionStatement extends SQLCreateFunctionStatement implements SQLCreateStatement {
    protected SQLExpr className;
    protected SQLExpr locationn;
    protected SQLExpr symbol;
    protected ResourceType resourceType;

    public void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof HiveASTVisitor) {
            accept0((HiveASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    protected void accept0(HiveASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, name);
            this.acceptChild(visitor, className);
            this.acceptChild(visitor, locationn);
            this.acceptChild(visitor, symbol);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getClassName() {
        return className;
    }

    public void setClassName(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.className = x;
    }

    public SQLExpr getLocationn() {
        return locationn;
    }

    public void setLocationn(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.locationn = x;
    }

    public SQLExpr getSymbol() {
        return symbol;
    }

    public void setSymbol(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.symbol = x;
    }

    public ResourceType getResourceType()
    {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType)
    {
        this.resourceType = resourceType;
    }

    public static enum ResourceType {
        JAR, FILE, ARCHIVE
    }
}
