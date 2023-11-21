package com.alibaba.druid.sql.dialect.hive.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HiveAddJarStatement extends SQLStatementImpl {
    public HiveAddJarStatement() {
        this.dbType = DbType.hive;
    }

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof HiveASTVisitor) {
            accept0((HiveASTVisitor) visitor);
        }
    }

    protected void accept0(HiveASTVisitor v) {
        v.visit(this);
        v.endVisit(this);
    }
}
