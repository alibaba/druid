package com.alibaba.druid.sql.dialect.oscar.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.oscar.visitor.OscarASTVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OscarConnectToStatement extends SQLStatementImpl implements OscarStatement {
    private SQLName target;

    public OscarConnectToStatement() {
        super(DbType.oscar);
    }

    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((PGASTVisitor) visitor);
    }

    @Override
    public void accept0(OscarASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, target);
        }
        v.endVisit(this);
    }

    public SQLName getTarget() {
        return target;
    }

    public void setTarget(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.target = x;
    }
}
