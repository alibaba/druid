package com.alibaba.druid.sql.dialect.databricks.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.spark.visitor.SparkOutputASTVisitor;

public class DatabricksOutputASTVisitor extends SparkOutputASTVisitor implements DatabricksASTASTVisitor {
    public DatabricksOutputASTVisitor(StringBuilder appender) {
        super(appender, DbType.databricks);
    }

    @Override
    protected void printTableOptionsPrefix(SQLCreateTableStatement x) {
        println();
        if (x instanceof HiveCreateTableStatement && ((HiveCreateTableStatement) x).getUsing() != null) {
            print0(ucase ? "OPTIONS (" : "options (");
        } else {
            print0(ucase ? "TBLPROPERTIES (" : "tblproperties (");
        }
        incrementIndent();
        println();
    }
}
