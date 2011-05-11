package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDeleteStatement extends SQLStatementImpl {
    private static final long serialVersionUID = 1L;
    private SQLName tableName;
    private SQLExpr where;

    public SQLDeleteStatement() {

    }

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        this.tableName = tableName;
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr where) {
        this.where = where;
    }

    public void output(StringBuffer buf) {
        buf.append("DELETE FROM ");
        this.tableName.output(buf);
        if (this.where != null) {
            buf.append(" WHERE ");
            this.where.output(buf);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableName);
            acceptChild(visitor, where);
        }

        visitor.endVisit(this);
    }

}
