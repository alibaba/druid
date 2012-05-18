package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlAlterTableChangeColumn extends MySqlObjectImpl implements SQLAlterTableItem {

    private static final long   serialVersionUID = 1L;

    private SQLName             columnName;

    private SQLColumnDefinition newColumnDefinition;

    private Boolean             first;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columnName);
            acceptChild(visitor, newColumnDefinition);
        }
    }

    public SQLName getColumnName() {
        return columnName;
    }

    public void setColumnName(SQLName columnName) {
        this.columnName = columnName;
    }

    public SQLColumnDefinition getNewColumnDefinition() {
        return newColumnDefinition;
    }

    public void setNewColumnDefinition(SQLColumnDefinition newColumnDefinition) {
        this.newColumnDefinition = newColumnDefinition;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

}
