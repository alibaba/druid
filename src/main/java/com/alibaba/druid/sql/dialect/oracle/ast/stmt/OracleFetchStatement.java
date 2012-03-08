package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleFetchStatement extends OracleStatementImpl {

    private static final long serialVersionUID = 1L;

    private SQLName           cursorName;

    private List<SQLExpr>     into             = new ArrayList<SQLExpr>();

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, cursorName);
            acceptChild(visitor, into);
        }
        visitor.endVisit(this);
    }

    public SQLName getCursorName() {
        return cursorName;
    }

    public void setCursorName(SQLName cursorName) {
        this.cursorName = cursorName;
    }

    public List<SQLExpr> getInto() {
        return into;
    }

    public void setInto(List<SQLExpr> into) {
        this.into = into;
    }

}
