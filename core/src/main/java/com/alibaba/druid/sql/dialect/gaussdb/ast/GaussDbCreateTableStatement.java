package com.alibaba.druid.sql.dialect.gaussdb.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.gaussdb.visitor.GaussDbASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class GaussDbCreateTableStatement extends SQLCreateTableStatement implements GaussDbObject {
    protected SQLExpr engine;
    protected GaussDbDistributeBy distributeBy;
    protected SQLPartitionBy partitionBy;
    protected SQLExpr autoIncrement;
    protected SQLExpr charset;
    protected SQLExpr collate;

    public GaussDbCreateTableStatement() {
        super(DbType.gaussdb);
    }

    public void setEngine(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.engine = x;
    }

    public SQLExpr getEngine() {
        return engine;
    }

    public void setAutoIncrement(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.autoIncrement = x;
    }

    public SQLExpr getAutoIncrement() {
        return autoIncrement;
    }

    public void setCharset(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.charset = x;
    }

    public SQLExpr getCharset() {
        return charset;
    }

    public void setCollate(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.collate = x;
    }

    public SQLExpr getCollate() {
        return collate;
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

    public void setPartitionBy(SQLPartitionBy partitionBy) {
        if (partitionBy != null) {
            partitionBy.setParent(this);
        }
        this.partitionBy = partitionBy;
    }

    public SQLPartitionBy getPartitionBy() {
        return partitionBy;
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
            acceptChild(visitor, this.engine);
            acceptChild(visitor, this.partitionBy);
            acceptChild(visitor, this.collate);
            acceptChild(visitor, this.charset);
            acceptChild(visitor, this.autoIncrement);
            acceptChild((SQLASTVisitor) visitor);
        }
    }
}
