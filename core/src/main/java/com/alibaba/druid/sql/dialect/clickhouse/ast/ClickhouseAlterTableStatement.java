package com.alibaba.druid.sql.dialect.clickhouse.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;

public abstract class ClickhouseAlterTableStatement extends SQLStatementImpl {
    private SQLName tableName;

    private SQLName clusterName;

    public ClickhouseAlterTableStatement(DbType dbType) {
        super(dbType);
    }

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        this.tableName = tableName;
    }

    public SQLName getClusterName() {
        return clusterName;
    }

    public void setClusterName(SQLName clusterName) {
        this.clusterName = clusterName;
    }
}
