package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class SearchClause extends OracleSQLObject {

    private static final long serialVersionUID = 1L;

    public static enum Type {
        DEPTH, BREADTH
    }

    private Type                          type;

    private final List<OracleOrderByItem> items = new ArrayList<OracleOrderByItem>();

    private SQLIdentifierExpr             orderingColumn;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<OracleOrderByItem> getItems() {
        return items;
    }

    public SQLIdentifierExpr getOrderingColumn() {
        return orderingColumn;
    }

    public void setOrderingColumn(SQLIdentifierExpr orderingColumn) {
        this.orderingColumn = orderingColumn;
    }

    @Override
    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, items);
            acceptChild(visitor, orderingColumn);
        }
        visitor.endVisit(this);
    }

}
