package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class StarRocksLoadStatement extends SQLStatementImpl {
    private SQLName label;
    private List<DataDescription> dataDescriptions = new ArrayList<>();
    private List<SQLAssignItem> brokerProperties = new ArrayList<>();
    private List<SQLAssignItem> properties = new ArrayList<>();

    public StarRocksLoadStatement() {
        dbType = DbType.starrocks;
    }

    public StarRocksLoadStatement(DbType dbType) {
        super(dbType);
    }

    public SQLName getLabel() {
        return label;
    }

    public void setLabel(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.label = x;
    }

    public List<DataDescription> getDataDescriptions() {
        return dataDescriptions;
    }

    public void setDataDescriptions(List<DataDescription> dataDescriptions) {
        this.dataDescriptions = dataDescriptions;
    }

    public void addDataDescription(DataDescription x) {
        if (x != null) {
            x.setParent(this);
        }
        this.dataDescriptions.add(x);
    }

    public List<SQLAssignItem> getBrokerProperties() {
        return brokerProperties;
    }

    public void setBrokerProperties(List<SQLAssignItem> brokerProperties) {
        this.brokerProperties = brokerProperties;
    }

    public void addBrokerProperty(SQLAssignItem x) {
        if (x != null) {
            x.setParent(this);
        }
        this.brokerProperties.add(x);
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
        if (visitor instanceof StarRocksASTVisitor) {
            StarRocksASTVisitor v = (StarRocksASTVisitor) visitor;
            if (v.visit(this)) {
                acceptChild(visitor, label);
                acceptChild(visitor, (List) dataDescriptions);
                acceptChild(visitor, (List) brokerProperties);
                acceptChild(visitor, (List) properties);
            }
            v.endVisit(this);
        }
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<>();
        if (label != null) {
            children.add(label);
        }
        children.addAll(dataDescriptions);
        children.addAll(brokerProperties);
        children.addAll(properties);
        return children;
    }

    public StarRocksLoadStatement clone() {
        StarRocksLoadStatement x = new StarRocksLoadStatement();
        if (this.label != null) {
            x.setLabel(this.label.clone());
        }
        for (DataDescription item : this.dataDescriptions) {
            DataDescription cloned = item.clone();
            cloned.setParent(x);
            x.dataDescriptions.add(cloned);
        }
        for (SQLAssignItem item : this.brokerProperties) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.brokerProperties.add(cloned);
        }
        for (SQLAssignItem item : this.properties) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.properties.add(cloned);
        }
        return x;
    }

    public static class DataDescription extends SQLObjectImpl {
        private List<SQLExpr> filePaths = new ArrayList<>();
        private SQLName tableName;
        private List<SQLName> partitions = new ArrayList<>();
        private SQLExpr columnTerminatedBy;
        private SQLExpr rowTerminatedBy;
        private SQLExpr format;
        private List<SQLExpr> columnList = new ArrayList<>();
        private List<SQLAssignItem> columnMappings = new ArrayList<>();
        private SQLExpr whereCondition;

        public List<SQLExpr> getFilePaths() {
            return filePaths;
        }

        public void setFilePaths(List<SQLExpr> filePaths) {
            this.filePaths = filePaths;
        }

        public void addFilePath(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.filePaths.add(x);
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

        public List<SQLName> getPartitions() {
            return partitions;
        }

        public void setPartitions(List<SQLName> partitions) {
            this.partitions = partitions;
        }

        public void addPartition(SQLName x) {
            if (x != null) {
                x.setParent(this);
            }
            this.partitions.add(x);
        }

        public SQLExpr getColumnTerminatedBy() {
            return columnTerminatedBy;
        }

        public void setColumnTerminatedBy(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.columnTerminatedBy = x;
        }

        public SQLExpr getRowTerminatedBy() {
            return rowTerminatedBy;
        }

        public void setRowTerminatedBy(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.rowTerminatedBy = x;
        }

        public SQLExpr getFormat() {
            return format;
        }

        public void setFormat(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.format = x;
        }

        public List<SQLExpr> getColumnList() {
            return columnList;
        }

        public void setColumnList(List<SQLExpr> columnList) {
            this.columnList = columnList;
        }

        public void addColumn(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.columnList.add(x);
        }

        public List<SQLAssignItem> getColumnMappings() {
            return columnMappings;
        }

        public void setColumnMappings(List<SQLAssignItem> columnMappings) {
            this.columnMappings = columnMappings;
        }

        public void addColumnMapping(SQLAssignItem x) {
            if (x != null) {
                x.setParent(this);
            }
            this.columnMappings.add(x);
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

        @Override
        protected void accept0(SQLASTVisitor v) {
            boolean visitChildren;
            if (v instanceof StarRocksASTVisitor) {
                visitChildren = ((StarRocksASTVisitor) v).visit(this);
            } else {
                visitChildren = true;
            }
            if (visitChildren) {
                acceptChild(v, (List) filePaths);
                acceptChild(v, tableName);
                acceptChild(v, (List) partitions);
                acceptChild(v, columnTerminatedBy);
                acceptChild(v, rowTerminatedBy);
                acceptChild(v, format);
                acceptChild(v, (List) columnList);
                acceptChild(v, (List) columnMappings);
                acceptChild(v, whereCondition);
            }
            if (v instanceof StarRocksASTVisitor) {
                ((StarRocksASTVisitor) v).endVisit(this);
            }
        }

        public List<SQLObject> getChildren() {
            List<SQLObject> children = new ArrayList<>();
            children.addAll(filePaths);
            if (tableName != null) {
                children.add(tableName);
            }
            children.addAll(partitions);
            if (columnTerminatedBy != null) {
                children.add(columnTerminatedBy);
            }
            if (rowTerminatedBy != null) {
                children.add(rowTerminatedBy);
            }
            if (format != null) {
                children.add(format);
            }
            children.addAll(columnList);
            children.addAll(columnMappings);
            if (whereCondition != null) {
                children.add(whereCondition);
            }
            return children;
        }

        public DataDescription clone() {
            DataDescription x = new DataDescription();
            for (SQLExpr item : this.filePaths) {
                SQLExpr cloned = item.clone();
                cloned.setParent(x);
                x.filePaths.add(cloned);
            }
            if (this.tableName != null) {
                x.setTableName(this.tableName.clone());
            }
            for (SQLName item : this.partitions) {
                SQLName cloned = item.clone();
                cloned.setParent(x);
                x.partitions.add(cloned);
            }
            if (this.columnTerminatedBy != null) {
                x.setColumnTerminatedBy(this.columnTerminatedBy.clone());
            }
            if (this.rowTerminatedBy != null) {
                x.setRowTerminatedBy(this.rowTerminatedBy.clone());
            }
            if (this.format != null) {
                x.setFormat(this.format.clone());
            }
            for (SQLExpr item : this.columnList) {
                SQLExpr cloned = item.clone();
                cloned.setParent(x);
                x.columnList.add(cloned);
            }
            for (SQLAssignItem item : this.columnMappings) {
                SQLAssignItem cloned = item.clone();
                cloned.setParent(x);
                x.columnMappings.add(cloned);
            }
            if (this.whereCondition != null) {
                x.setWhereCondition(this.whereCondition.clone());
            }
            return x;
        }
    }
}
