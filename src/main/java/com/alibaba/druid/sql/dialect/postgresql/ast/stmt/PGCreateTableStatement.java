package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.util.JdbcConstants;

public class PGCreateTableStatement extends SQLCreateTableStatement {
    private SQLExprTableSource inherits;

    public PGCreateTableStatement(){
        super(JdbcConstants.POSTGRESQL);
    }

}
