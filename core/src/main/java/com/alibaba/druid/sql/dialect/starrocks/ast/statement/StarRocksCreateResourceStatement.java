package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLDDLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.HashMap;
import java.util.Map;

public class StarRocksCreateResourceStatement extends SQLStatementImpl implements SQLDDLStatement, SQLCreateStatement {
    private SQLName name;
    private Map<SQLCharExpr, SQLExpr> properties = new HashMap<SQLCharExpr, SQLExpr>();
    private boolean external;

    public StarRocksCreateResourceStatement() {
        dbType = DbType.starrocks;
    }

    public StarRocksCreateResourceStatement(DbType dbType) {
        super(dbType);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.name = x;
    }

    public void addProperty(SQLCharExpr name, SQLExpr value) {
        if (value != null) {
            value.setParent(this);
        }
        name.setParent(this);
        properties.put(name, value);
    }

    public void addProperty(String name, SQLExpr value) {
        addProperty(
                new SQLCharExpr(SQLUtils.forcedNormalize(name, getDbType())),
                value
        );
    }

    public Map<SQLCharExpr, SQLExpr> getProperties() {
        return properties;
    }

    public void setProperties(Map<SQLCharExpr, SQLExpr> properties) {
        this.properties = properties;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.preVisit(this);
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
