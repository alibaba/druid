package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowBinLogEventsStatement extends MySqlStatementImpl {

    private static final long serialVersionUID = 1L;

    private SQLExpr           in;
    private SQLExpr           from;
    private Limit             limit;

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, in);
            acceptChild(visitor, from);
            acceptChild(visitor, limit);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getIn() {
        return in;
    }

    public void setIn(SQLExpr in) {
        this.in = in;
    }

    public SQLExpr getFrom() {
        return from;
    }

    public void setFrom(SQLExpr from) {
        this.from = from;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

}
