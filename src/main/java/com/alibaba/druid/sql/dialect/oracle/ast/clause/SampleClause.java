package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class SampleClause extends OracleSQLObjectImpl {

    private static final long serialVersionUID = 1L;

    private boolean           block            = false;

    private List<SQLExpr>     percent          = new ArrayList<SQLExpr>();

    private SQLExpr           seedValue;

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public List<SQLExpr> getPercent() {
        return percent;
    }

    public void setPercent(List<SQLExpr> percent) {
        this.percent = percent;
    }

    public SQLExpr getSeedValue() {
        return seedValue;
    }

    public void setSeedValue(SQLExpr seedValue) {
        this.seedValue = seedValue;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, seedValue);
            acceptChild(visitor, percent);
        }
        visitor.endVisit(this);
    }

}
