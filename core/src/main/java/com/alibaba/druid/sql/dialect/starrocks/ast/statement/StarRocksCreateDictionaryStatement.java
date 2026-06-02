package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLDDLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class StarRocksCreateDictionaryStatement extends SQLStatementImpl implements SQLDDLStatement, SQLCreateStatement {
    private SQLName name;
    private SQLName sourceTable;
    private List<SQLAssignItem> columnMappings = new ArrayList<>();
    private List<SQLAssignItem> properties = new ArrayList<>();

    public StarRocksCreateDictionaryStatement() {
        dbType = DbType.starrocks;
    }

    public StarRocksCreateDictionaryStatement(DbType dbType) {
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

    public SQLName getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.sourceTable = x;
    }

    public List<SQLAssignItem> getColumnMappings() {
        return columnMappings;
    }

    public void setColumnMappings(List<SQLAssignItem> columnMappings) {
        this.columnMappings = columnMappings;
    }

    public void addColumnMapping(SQLAssignItem assignItem) {
        if (assignItem != null) {
            assignItem.setParent(this);
        }
        this.columnMappings.add(assignItem);
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
        acceptChild(v, sourceTable);
        acceptChild(v, (List) columnMappings);
        acceptChild(v, (List) properties);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<>();
        if (name != null) {
            children.add(name);
        }
        if (sourceTable != null) {
            children.add(sourceTable);
        }
        children.addAll(columnMappings);
        children.addAll(properties);
        return children;
    }

    public StarRocksCreateDictionaryStatement clone() {
        StarRocksCreateDictionaryStatement x = new StarRocksCreateDictionaryStatement();
        if (this.name != null) {
            x.setName(this.name.clone());
        }
        if (this.sourceTable != null) {
            x.setSourceTable(this.sourceTable.clone());
        }
        for (SQLAssignItem item : this.columnMappings) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.columnMappings.add(cloned);
        }
        for (SQLAssignItem item : this.properties) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.properties.add(cloned);
        }
        return x;
    }
}
