package com.alibaba.druid.sql.dialect.sqlserver.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLServerTryCatchStatement extends SQLStatementImpl implements SQLServerStatement {
    public SQLServerTryCatchStatement() {
        super(DbType.sqlserver);
    }

    private final List<SQLStatement> tryStatements = new ArrayList<SQLStatement>();
    private final List<SQLStatement> catchStatements = new ArrayList<SQLStatement>();

    public List<SQLStatement> getTryStatements() {
        return tryStatements;
    }

    public List<SQLStatement> getCatchStatements() {
        return catchStatements;
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
            acceptChild(visitor, tryStatements);
            acceptChild(visitor, catchStatements);
        }
        visitor.endVisit(this);
    }
}
