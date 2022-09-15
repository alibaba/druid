package com.alibaba.druid.sql.dialect.blink.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;

public class BlinkCreateTableStatement extends SQLCreateTableStatement {
    private SQLExpr periodFor;

    public BlinkCreateTableStatement() {
        dbType = DbType.blink;
    }

    public SQLExpr getPeriodFor() {
        return periodFor;
    }

    public void setPeriodFor(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.periodFor = x;
    }
}
