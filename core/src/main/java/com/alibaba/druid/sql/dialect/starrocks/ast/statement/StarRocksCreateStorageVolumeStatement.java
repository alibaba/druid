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

public class StarRocksCreateStorageVolumeStatement extends SQLStatementImpl implements SQLDDLStatement, SQLCreateStatement {
    private boolean ifNotExists;
    private SQLName name;
    private SQLExpr type;
    private List<SQLExpr> locations = new ArrayList<>();
    private SQLExpr comment;
    private List<SQLAssignItem> properties = new ArrayList<>();

    public StarRocksCreateStorageVolumeStatement() {
        dbType = DbType.starrocks;
    }

    public StarRocksCreateStorageVolumeStatement(DbType dbType) {
        super(dbType);
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

    public SQLExpr getType() {
        return type;
    }

    public void setType(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.type = x;
    }

    public List<SQLExpr> getLocations() {
        return locations;
    }

    public void setLocations(List<SQLExpr> locations) {
        this.locations = locations;
    }

    public void addLocation(SQLExpr location) {
        if (location != null) {
            location.setParent(this);
        }
        this.locations.add(location);
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
    protected void accept0(SQLASTVisitor v) {
        // visitor dispatch will be wired in StarRocksASTVisitor
        acceptChild(v, name);
        acceptChild(v, type);
        acceptChild(v, (List) locations);
        acceptChild(v, comment);
        acceptChild(v, (List) properties);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<>();
        if (name != null) {
            children.add(name);
        }
        if (type != null) {
            children.add(type);
        }
        children.addAll(locations);
        if (comment != null) {
            children.add(comment);
        }
        children.addAll(properties);
        return children;
    }

    public StarRocksCreateStorageVolumeStatement clone() {
        StarRocksCreateStorageVolumeStatement x = new StarRocksCreateStorageVolumeStatement();
        x.ifNotExists = this.ifNotExists;
        if (this.name != null) {
            x.setName(this.name.clone());
        }
        if (this.type != null) {
            x.setType(this.type.clone());
        }
        for (SQLExpr loc : this.locations) {
            SQLExpr cloned = loc.clone();
            cloned.setParent(x);
            x.locations.add(cloned);
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
