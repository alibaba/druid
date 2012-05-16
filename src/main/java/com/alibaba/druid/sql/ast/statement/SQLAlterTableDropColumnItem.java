package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableDropColumnItem extends SQLObjectImpl implements SQLAlterTableItem {

    private static final long serialVersionUID = 1L;
    private SQLName           columnName;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columnName);
        }
        visitor.endVisit(this);
    }

    public SQLName getColumnName() {
        return columnName;
    }

    public void setColumnName(SQLName columnName) {
        this.columnName = columnName;
    }

}
