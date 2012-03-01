package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleAlterTableModify extends OracleAlterTableItem {

    private static final long         serialVersionUID = 1L;

    private List<SQLColumnDefinition> columns          = new ArrayList<SQLColumnDefinition>();

    @Override
    public void accept0(OracleASTVisitor visitor) {
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
