package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLDropStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

/**
 * @author gaoWL
 */
public class OracleDropPackageStatement extends OracleStatementImpl implements SQLDropStatement, OracleStatement {
    private SQLName name;

    private boolean body;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    @Override
    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public boolean isBody() {
        return body;
    }

    public void setBody(boolean body) {
        this.body = body;
    }

}
