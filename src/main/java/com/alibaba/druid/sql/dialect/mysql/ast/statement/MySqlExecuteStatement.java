package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlExecuteStatement extends MySqlStatementImpl {
    private static final long serialVersionUID = 1L;

    private SQLName statementName;
    private final List<SQLExpr> parameters = new ArrayList<SQLExpr>();

    public SQLName getStatementName() {
        return statementName;
    }

    public void setStatementName(SQLName statementName) {
        this.statementName = statementName;
    }

    public List<SQLExpr> getParameters() {
        return parameters;
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, statementName);
            acceptChild(visitor, parameters);
        }
        visitor.endVisit(this);
    }
}
