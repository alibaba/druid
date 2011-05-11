package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleUpdateSetListClause extends OracleUpdateSetClause {
    private static final long serialVersionUID = 1L;

    private final List<OracleUpdateSetListItem> items = new ArrayList<OracleUpdateSetListItem>();

    public OracleUpdateSetListClause() {

    }

    public List<OracleUpdateSetListItem> getItems() {
        return this.items;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
        }

        visitor.endVisit(this);
    }
}
