package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by wenshao on 16/9/25.
 */
public class SQLLimit extends SQLObjectImpl {

    public SQLLimit() {

    }

    public SQLLimit(SQLExpr rowCount) {
        this.setRowCount(rowCount);
    }

    private SQLExpr rowCount;
    private SQLExpr offset;

    public SQLExpr getRowCount() {
        return rowCount;
    }

    public void setRowCount(SQLExpr rowCount) {
        if (rowCount != null) {
            rowCount.setParent(this);
        }
        this.rowCount = rowCount;
    }

    public SQLExpr getOffset() {
        return offset;
    }

    public void setOffset(SQLExpr offset) {
        if (offset != null) {
            offset.setParent(this);
        }
        this.offset = offset;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, offset);
            acceptChild(visitor, rowCount);
        }
        visitor.endVisit(this);
    }

}
