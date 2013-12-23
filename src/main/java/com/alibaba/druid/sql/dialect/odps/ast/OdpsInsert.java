package com.alibaba.druid.sql.dialect.odps.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;


public class OdpsInsert extends SQLObjectImpl {
    private boolean                overwrite  = false;
    private List<SQLAssignItem>    partitions = new ArrayList<SQLAssignItem>();
    protected SQLExprTableSource   tableSource;

    private SQLSelect              query;

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public List<SQLAssignItem> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<SQLAssignItem> partitions) {
        this.partitions = partitions;
    }

    
    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSource = tableSource;
    }

    public void setTableSource(SQLName tableName) {
        this.setTableSource(new SQLExprTableSource(tableName));
    }

    public SQLSelect getQuery() {
        return query;
    }

    public void setQuery(SQLSelect query) {
        if (query != null) {
            query.setParent(this);
        }
        this.query = query;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OdpsASTVisitor) visitor);
    }
    
    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, partitions);
            acceptChild(visitor, query);
        }
        visitor.endVisit(this);
    }

}
