package com.alibaba.druid.sql.dialect.clickhouse.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.CKASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class CKDropTableStatement extends SQLDropTableStatement {
    private String onClusterName;

    public CKDropTableStatement() {
        super(DbType.clickhouse);
    }

    public CKDropTableStatement(DbType dbType) {
        super(dbType);
    }

    public String getOnClusterName() {
        return onClusterName;
    }

    public void setOnClusterName(String onClusterName) {
        this.onClusterName = onClusterName;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof CKASTVisitor) {
            CKASTVisitor vv = (CKASTVisitor) v;
            if (vv.visit(this)) {
                acceptChild(vv, tableSources);
            }
            vv.endVisit(this);
            return;
        }

        super.accept0(v);
    }
}
