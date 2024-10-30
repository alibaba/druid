package com.alibaba.druid.sql.dialect.gaussdb.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbDistributeBy;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbObject;
import com.alibaba.druid.sql.dialect.gaussdb.visitor.GaussDbASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class GaussDbCreateTableStatement extends SQLCreateTableStatement implements GaussDbObject {
    protected GaussDbDistributeBy distributeBy;

    public GaussDbCreateTableStatement() {
        super(DbType.gaussdb);
    }

    public void setDistributeBy(GaussDbDistributeBy distributeBy) {
        if (distributeBy != null) {
            distributeBy.setParent(this);
        }
        this.distributeBy = distributeBy;
    }

    public GaussDbDistributeBy getDistributeBy() {
        return distributeBy;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof GaussDbASTVisitor) {
            accept0((GaussDbASTVisitor) v);
            return;
        }
        if (v.visit(this)) {
            acceptChild(v);
        }
        v.endVisit(this);
    }

    @Override
    public void accept0(GaussDbASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.distributeBy);
            acceptChild((SQLASTVisitor) visitor);
        }
    }
}
