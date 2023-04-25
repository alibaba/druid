package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLDDLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.LinkedList;
import java.util.List;

public class StarRocksCreateResourceStatement extends SQLStatementImpl implements SQLDDLStatement, SQLCreateStatement {
    private SQLName name;
    private List<SQLAssignItem> properties = new LinkedList<>();
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

    public List<SQLAssignItem> getProperties() {
        return properties;
    }

    public void setProperties(List<SQLAssignItem> properties) {
        this.properties = properties;
    }

    public void addProperty(SQLAssignItem assignItem) {
        if (assignItem != null) {
            assignItem.setParent(this);
        }
        this.properties.add(assignItem);
    }

    public void addProperty(SQLExpr key, SQLExpr value) {
        addProperty(new SQLAssignItem(key, value));
    }

    public void addProperty(String key, String value) {
        addProperty(new SQLCharExpr(key), new SQLCharExpr(value));
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
