package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleStorageClause extends OracleSQLObjectImpl {

    private static final long serialVersionUID = 1L;

    private SQLExpr           initial;
    private SQLExpr           freeLists;
    private SQLExpr           freeListGroups;
    private SQLExpr           bufferPool;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, initial);
            acceptChild(visitor, freeLists);
            acceptChild(visitor, freeListGroups);
            acceptChild(visitor, bufferPool);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getInitial() {
        return initial;
    }

    public void setInitial(SQLExpr initial) {
        this.initial = initial;
    }

    public SQLExpr getFreeLists() {
        return freeLists;
    }

    public void setFreeLists(SQLExpr freeLists) {
        this.freeLists = freeLists;
    }

    public SQLExpr getFreeListGroups() {
        return freeListGroups;
    }

    public void setFreeListGroups(SQLExpr freeListGroups) {
        this.freeListGroups = freeListGroups;
    }

    public SQLExpr getBufferPool() {
        return bufferPool;
    }

    public void setBufferPool(SQLExpr bufferPool) {
        this.bufferPool = bufferPool;
    }

}
