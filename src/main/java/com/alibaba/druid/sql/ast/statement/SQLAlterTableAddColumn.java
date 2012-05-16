package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableAddColumn extends SQLObjectImpl implements SQLAlterTableItem {

    private static final long         serialVersionUID = 1L;

    private List<SQLColumnDefinition> columns          = new ArrayList<SQLColumnDefinition>();

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columns);
        }
        visitor.endVisit(this);
    }

    public List<SQLColumnDefinition> getColumns() {
        return columns;
    }

    public void setColumns(List<SQLColumnDefinition> columns) {
        this.columns = columns;
    }

}
