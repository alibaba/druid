package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterDatabaseStatement extends SQLStatementImpl {

    private SQLName name;

    private boolean upgradeDataDirectoryName;
    
    public SQLAlterDatabaseStatement() {
        
    }
    
    public SQLAlterDatabaseStatement(String dbType) {
        this.setDbType(dbType);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public boolean isUpgradeDataDirectoryName() {
        return upgradeDataDirectoryName;
    }

    public void setUpgradeDataDirectoryName(boolean upgradeDataDirectoryName) {
        this.upgradeDataDirectoryName = upgradeDataDirectoryName;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }
}
