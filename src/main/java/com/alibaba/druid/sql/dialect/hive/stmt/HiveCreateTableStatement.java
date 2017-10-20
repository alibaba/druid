package com.alibaba.druid.sql.dialect.hive.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;

public class HiveCreateTableStatement extends SQLCreateTableStatement {
    public HiveCreateTableStatement() {
        this.dbType = JdbcConstants.HIVE;
    }
}
