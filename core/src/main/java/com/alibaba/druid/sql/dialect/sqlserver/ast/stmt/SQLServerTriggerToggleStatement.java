package com.alibaba.druid.sql.dialect.sqlserver.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * Represents the SQL Server {@code ENABLE TRIGGER ... ON table} and
 * {@code DISABLE TRIGGER ... ON table} statements. The trigger spec is either
 * {@code ALL}, {@code ALL SERVER}, or a (comma-separated) list of trigger names.
 */
public class SQLServerTriggerToggleStatement extends SQLStatementImpl implements SQLServerStatement {
    private final boolean enable;
    private boolean all;
    private boolean allServer;
    private SQLName on;

    public SQLServerTriggerToggleStatement(boolean enable) {
        super(DbType.sqlserver);
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public boolean isAllServer() {
        return allServer;
    }

    public void setAllServer(boolean allServer) {
        this.allServer = allServer;
    }

    public SQLName getOn() {
        return on;
    }

    public void setOn(SQLName on) {
        if (on != null) {
            on.setParent(this);
        }
        this.on = on;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof SQLServerASTVisitor) {
            accept0((SQLServerASTVisitor) visitor);
        }
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, on);
        }
        visitor.endVisit(this);
    }
}
