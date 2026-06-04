package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLDDLStatement;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class StarRocksCreateRoutineLoadStatement extends SQLStatementImpl implements SQLDDLStatement, SQLCreateStatement {
    private SQLName name;
    private SQLName tableName;
    private List<SQLExpr> columns = new ArrayList<>();
    private SQLExpr whereCondition;
    private List<SQLAssignItem> properties = new ArrayList<>();
    private List<SQLAssignItem> dataSourceProperties = new ArrayList<>();
    private String dataSourceType;
    private boolean orReplace;

    public StarRocksCreateRoutineLoadStatement() {
        dbType = DbType.starrocks;
    }

    public StarRocksCreateRoutineLoadStatement(DbType dbType) {
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

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.tableName = x;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public void setColumns(List<SQLExpr> columns) {
        this.columns = columns;
    }

    public void addColumn(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.columns.add(x);
    }

    public SQLExpr getWhereCondition() {
        return whereCondition;
    }

    public void setWhereCondition(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.whereCondition = x;
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

    public List<SQLAssignItem> getDataSourceProperties() {
        return dataSourceProperties;
    }

    public void setDataSourceProperties(List<SQLAssignItem> dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    public void addDataSourceProperty(SQLAssignItem x) {
        if (x != null) {
            x.setParent(this);
        }
        this.dataSourceProperties.add(x);
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof StarRocksASTVisitor) {
            StarRocksASTVisitor v = (StarRocksASTVisitor) visitor;
            if (v.visit(this)) {
                acceptChild(visitor, name);
                acceptChild(visitor, tableName);
                acceptChild(visitor, (List) columns);
                acceptChild(visitor, whereCondition);
                acceptChild(visitor, (List) properties);
                acceptChild(visitor, (List) dataSourceProperties);
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
        if (tableName != null) {
            children.add(tableName);
        }
        children.addAll(columns);
        if (whereCondition != null) {
            children.add(whereCondition);
        }
        children.addAll(properties);
        children.addAll(dataSourceProperties);
        return children;
    }

    public StarRocksCreateRoutineLoadStatement clone() {
        StarRocksCreateRoutineLoadStatement x = new StarRocksCreateRoutineLoadStatement();
        if (this.name != null) {
            x.setName(this.name.clone());
        }
        if (this.tableName != null) {
            x.setTableName(this.tableName.clone());
        }
        for (SQLExpr item : this.columns) {
            SQLExpr cloned = item.clone();
            cloned.setParent(x);
            x.columns.add(cloned);
        }
        if (this.whereCondition != null) {
            x.setWhereCondition(this.whereCondition.clone());
        }
        for (SQLAssignItem item : this.properties) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.properties.add(cloned);
        }
        for (SQLAssignItem item : this.dataSourceProperties) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.dataSourceProperties.add(cloned);
        }
        x.dataSourceType = this.dataSourceType;
        x.orReplace = this.orReplace;
        return x;
    }
}
