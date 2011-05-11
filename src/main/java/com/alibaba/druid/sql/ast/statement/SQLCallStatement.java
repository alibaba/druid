package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLCallStatement extends SQLStatementImpl {
    private static final long serialVersionUID = 1L;

    private SQLName procedureName;

    private final List<SQLExpr> parameters = new ArrayList<SQLExpr>();

    public SQLName getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(SQLName procedureName) {
        this.procedureName = procedureName;
    }

    public List<SQLExpr> getParameters() {
        return parameters;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.procedureName);
            acceptChild(visitor, this.parameters);
        }
        visitor.endVisit(this);
    }
}
