package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleSelectForUpdate extends OracleSQLObject {
    private static final long serialVersionUID = 1L;

    private final List<SQLExpr> of = new ArrayList<SQLExpr>();

    public OracleSelectForUpdate() {

    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.of);
        }

        visitor.endVisit(this);
    }

    public List<SQLExpr> getOf() {
        return this.of;
    }

    public static class SkipLock {
    }

    public static abstract class Type extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;

        public Type() {

        }
    }
}
