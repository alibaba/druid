package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlLockTableStatement extends MySqlStatementImpl {

    private static final long  serialVersionUID = 1L;

    private SQLExprTableSource tableSource;

    private LockType           lockType;

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        this.tableSource = tableSource;
    }
    
    public void setTableSource(SQLName name) {
        this.tableSource = new SQLExprTableSource(name);
    }

    public LockType getLockType() {
        return lockType;
    }

    public void setLockType(LockType lockType) {
        this.lockType = lockType;
    }
    
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
        }
        visitor.endVisit(this);
    }

    public static enum LockType {
        READ("READ"), READ_LOCAL("READ LOCAL"), WRITE("WRITE"), LOW_PRIORITY_WRITE("LOW_PRIORITY WRITE");

        public final String name;

        LockType(String name){
            this.name = name;
        }
    }
}
