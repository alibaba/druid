package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

/**
 * @author gaoWL
 */
public class OracleAlterPackageStatement extends OracleStatementImpl implements SQLAlterStatement, OracleStatement {
    private SQLName name;

    private boolean compile;

    private boolean body;

    private boolean pack;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public boolean isCompile() {
        return compile;
    }

    public void setCompile(boolean compile) {
        this.compile = compile;
    }

    public boolean isBody() {
        return body;
    }

    public void setBody(boolean body) {
        this.body = body;
    }

    public boolean isPack() {
        return pack;
    }

    public void setPack(boolean pack) {
        this.pack = pack;
    }

}
