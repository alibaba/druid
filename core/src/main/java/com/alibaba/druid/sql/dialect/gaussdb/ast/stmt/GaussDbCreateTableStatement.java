package com.alibaba.druid.sql.dialect.gaussdb.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbDistributeBy;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbObject;
import com.alibaba.druid.sql.dialect.gaussdb.visitor.GaussDbASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class GaussDbCreateTableStatement extends SQLCreateTableStatement implements GaussDbObject {
    protected GaussDbDistributeBy distributeBy;
    protected SQLExpr toGroup;
    protected SQLExpr toNode;
    private SQLExpr onCommitExpr;
    private SQLExpr compressType;

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

    public void setToGroup(SQLExpr toGroup) {
        if (toGroup != null) {
            toGroup.setParent(this);
        }
        this.toGroup = toGroup;
    }

    public SQLExpr getToGroup() {
        return toGroup;
    }

    public void setToNode(SQLExpr toNode) {
        if (toNode != null) {
            toNode.setParent(this);
        }
        this.toNode = toNode;
    }

    public SQLExpr getToNode() {
        return toNode;
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

    public SQLExpr getOnCommitExpr() {
        return onCommitExpr;
    }

    public void setOnCommitExpr(SQLExpr onCommitExpr) {
        this.onCommitExpr = onCommitExpr;
    }

    public SQLExpr getCompressType() {
        return compressType;
    }

    public void setCompressType(SQLExpr compressType) {
        this.compressType = compressType;
    }
}
