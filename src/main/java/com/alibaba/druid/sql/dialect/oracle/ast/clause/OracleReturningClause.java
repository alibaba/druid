package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleReturningClause extends OracleSQLObjectImpl {

    private static final long serialVersionUID = 1L;

    private List<SQLExpr>     items            = new ArrayList<SQLExpr>();
    private List<SQLExpr>     values           = new ArrayList<SQLExpr>();

    public List<SQLExpr> getItems() {
        return items;
    }

    public void setItems(List<SQLExpr> items) {
        this.items = items;
    }

    public List<SQLExpr> getValues() {
        return values;
    }

    public void setValues(List<SQLExpr> values) {
        this.values = values;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, items);
            acceptChild(visitor, values);
        }
        visitor.endVisit(this);
    }

}
