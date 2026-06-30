package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class StarRocksBackupStatement extends SQLStatementImpl {
    private SQLName snapshotName;
    private SQLName repository;
    private List<SQLExpr> onTables = new ArrayList<>();
    private List<SQLAssignItem> properties = new ArrayList<>();

    public StarRocksBackupStatement() {
        dbType = DbType.starrocks;
    }

    public StarRocksBackupStatement(DbType dbType) {
        super(dbType);
    }

    public SQLName getSnapshotName() {
        return snapshotName;
    }

    public void setSnapshotName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.snapshotName = x;
    }

    public SQLName getRepository() {
        return repository;
    }

    public void setRepository(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.repository = x;
    }

    public List<SQLExpr> getOnTables() {
        return onTables;
    }

    public void setOnTables(List<SQLExpr> onTables) {
        this.onTables = onTables;
    }

    public void addOnTable(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.onTables.add(x);
    }

    public List<SQLAssignItem> getProperties() {
        return properties;
    }

    public void setProperties(List<SQLAssignItem> properties) {
        this.properties = properties;
    }

    public void addProperty(SQLAssignItem x) {
        if (x != null) {
            x.setParent(this);
        }
        this.properties.add(x);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, snapshotName);
            acceptChild(visitor, repository);
            acceptChild(visitor, (List) onTables);
            acceptChild(visitor, (List) properties);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<>();
        if (snapshotName != null) {
            children.add(snapshotName);
        }
        if (repository != null) {
            children.add(repository);
        }
        children.addAll(onTables);
        children.addAll(properties);
        return children;
    }

    public StarRocksBackupStatement clone() {
        StarRocksBackupStatement x = new StarRocksBackupStatement();
        if (this.snapshotName != null) {
            x.setSnapshotName(this.snapshotName.clone());
        }
        if (this.repository != null) {
            x.setRepository(this.repository.clone());
        }
        for (SQLExpr item : this.onTables) {
            SQLExpr cloned = item.clone();
            cloned.setParent(x);
            x.onTables.add(cloned);
        }
        for (SQLAssignItem item : this.properties) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.properties.add(cloned);
        }
        return x;
    }
}
