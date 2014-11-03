package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSQLObjectImpl;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

public class PGValuesQuery extends PGSQLObjectImpl implements SQLSelectQuery {

    private List<SQLExpr> values = new ArrayList<SQLExpr>();

    public List<SQLExpr> getValues() {
        return values;
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, values);
        }
        visitor.endVisit(this);
    }

}
