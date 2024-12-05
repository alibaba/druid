package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class SQLRaiseStatement extends SQLStatementImpl {
    private SQLExpr message;

    public SQLExpr getMessage() {
        return message;
    }

    public void setMessage(SQLExpr message) {
        this.message = message;
    }

    public void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        if (message != null) {
            return Collections.<SQLObject>singletonList(message);
        }
        return Collections.emptyList();
    }
}
