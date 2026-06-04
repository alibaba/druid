package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLDDLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class StarRocksCreateCatalogStatement extends SQLStatementImpl implements SQLDDLStatement, SQLCreateStatement {
    private boolean external;
    private boolean orReplace;
    private boolean ifNotExists;
    private SQLName name;
    private SQLExpr comment;
    private List<SQLAssignItem> properties = new ArrayList<>();

    public StarRocksCreateCatalogStatement() {
        dbType = DbType.starrocks;
    }

    public StarRocksCreateCatalogStatement(DbType dbType) {
        super(dbType);
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
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

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.comment = x;
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

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, comment);
            acceptChild(visitor, (List) properties);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<>();
        if (name != null) {
            children.add(name);
        }
        if (comment != null) {
            children.add(comment);
        }
        children.addAll(properties);
        return children;
    }

    public StarRocksCreateCatalogStatement clone() {
        StarRocksCreateCatalogStatement x = new StarRocksCreateCatalogStatement();
        x.external = this.external;
        x.orReplace = this.orReplace;
        x.ifNotExists = this.ifNotExists;
        if (this.name != null) {
            x.setName(this.name.clone());
        }
        if (this.comment != null) {
            x.setComment(this.comment.clone());
        }
        for (SQLAssignItem item : this.properties) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.properties.add(cloned);
        }
        return x;
    }
}
