package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLUpdateStatement extends SQLStatementImpl {
    private static final long serialVersionUID = 1L;

    private SQLName tableName;

    private final List<SQLUpdateSetItem> items = new ArrayList<SQLUpdateSetItem>();
    private SQLExpr where;

    public SQLUpdateStatement() {

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

    public List<SQLUpdateSetItem> getItems() {
        return items;
    }

    @Override
    public void output(StringBuffer buf) {
        buf.append("UPDATE ");

        this.tableName.output(buf);

        buf.append(" SET ");
        for (int i = 0, size = items.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            items.get(i).output(buf);
        }

        if (this.where != null) {
            buf.append(" WHERE ");
            this.where.output(buf);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableName);
            acceptChild(visitor, items);
            acceptChild(visitor, where);
        }
        visitor.endVisit(this);
    }
}
