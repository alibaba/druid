package com.alibaba.druid.sql.dialect.hive.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HiveCreateFunctionStatement extends SQLCreateFunctionStatement implements SQLCreateStatement {
    protected boolean declare;
    protected SQLExpr className;
    protected SQLExpr location;
    protected SQLExpr symbol;
    protected ResourceType resourceType;
    protected String code;

    public HiveCreateFunctionStatement() {
    }

    public void cloneTo(HiveCreateFunctionStatement x) {
        // replicate base SQLCreateFunctionStatement.clone() field copying
        // (use public accessors for base private/package-private fields)
        if (getDefiner() != null) {
            x.setDefiner(getDefiner().clone());
        }
        x.setCreate(isCreate());
        x.setOrReplace(isOrReplace());
        if (getName() != null) {
            x.setName(getName().clone());
        }
        if (getBlock() != null) {
            x.setBlock(getBlock().clone());
        }
        for (com.alibaba.druid.sql.ast.SQLParameter p : getParameters()) {
            com.alibaba.druid.sql.ast.SQLParameter p2 = p.clone();
            p2.setParent(x);
            x.getParameters().add(p2);
        }
        x.setJavaCallSpec(getJavaCallSpec());
        if (getAuthid() != null) {
            x.setAuthid(getAuthid().clone());
        }
        if (getReturnDataType() != null) {
            x.setReturnDataType(getReturnDataType().clone());
        }
        x.setComment(getComment());
        x.setDeterministic(isDeterministic());
        x.setPipelined(isPipelined());
        x.setLanguage(getLanguage());

        // own fields of HiveCreateFunctionStatement
        x.declare = declare;
        if (className != null) {
            x.setClassName(className.clone());
        }
        if (location != null) {
            x.setLocation(location.clone());
        }
        if (symbol != null) {
            x.setSymbol(symbol.clone());
        }
        x.resourceType = resourceType;
        x.code = code;
    }

    @Override
    public HiveCreateFunctionStatement clone() {
        HiveCreateFunctionStatement x = new HiveCreateFunctionStatement();
        cloneTo(x);
        return x;
    }

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
            this.acceptChild(visitor, location);
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

    public SQLExpr getLocation() {
        return location;
    }

    public void setLocation(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.location = x;
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

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isDeclare() {
        return declare;
    }

    public void setDeclare(boolean declare) {
        this.declare = declare;
    }

    public static enum ResourceType {
        JAR, FILE, ARCHIVE, CODE
    }
}
