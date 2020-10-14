package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * @author lijun.cailj 2017/11/16
 */
public class MysqlShowHtcStatement extends MySqlStatementImpl implements MySqlShowStatement {
    private boolean full = false;
    private boolean isHis = false;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public boolean isHis() {
        return isHis;
    }

    public void setHis(boolean his) {
        isHis = his;
    }
}
