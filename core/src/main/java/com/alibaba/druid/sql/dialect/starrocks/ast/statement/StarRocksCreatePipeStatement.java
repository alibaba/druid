package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLDDLStatement;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class StarRocksCreatePipeStatement extends SQLStatementImpl implements SQLDDLStatement, SQLCreateStatement {
    private boolean orReplace;
    private boolean ifNotExists;
    private SQLName name;
    private List<SQLAssignItem> properties = new ArrayList<>();
    private SQLStatement body;

    public StarRocksCreatePipeStatement() {
        dbType = DbType.starrocks;
    }

    public StarRocksCreatePipeStatement(DbType dbType) {
        super(dbType);
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
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

    public SQLStatement getBody() {
        return body;
    }

    public void setBody(SQLStatement x) {
        if (x != null) {
            x.setParent(this);
        }
        this.body = x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof StarRocksASTVisitor) {
            StarRocksASTVisitor v = (StarRocksASTVisitor) visitor;
            if (v.visit(this)) {
                acceptChild(visitor, name);
                acceptChild(visitor, (List) properties);
                acceptChild(visitor, body);
            }
            v.endVisit(this);
        }
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<>();
        if (name != null) {
            children.add(name);
        }
        children.addAll(properties);
        if (body != null) {
            children.add(body);
        }
        return children;
    }

    public StarRocksCreatePipeStatement clone() {
        StarRocksCreatePipeStatement x = new StarRocksCreatePipeStatement();
        x.orReplace = this.orReplace;
        x.ifNotExists = this.ifNotExists;
        if (this.name != null) {
            x.setName(this.name.clone());
        }
        for (SQLAssignItem item : this.properties) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.properties.add(cloned);
        }
        if (this.body != null) {
            x.setBody(this.body.clone());
        }
        return x;
    }
}
