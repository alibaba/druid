package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * version 1.0
 * Author zzy
 * Date 2019-06-03 15:50
 */
public class MySqlAlterTableLock extends MySqlObjectImpl implements SQLAlterTableItem {

    private SQLExpr lockType;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, lockType);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getLockType() {
        return lockType;
    }

    public void setLockType(SQLExpr lockType) {
        this.lockType = lockType;
    }
}
