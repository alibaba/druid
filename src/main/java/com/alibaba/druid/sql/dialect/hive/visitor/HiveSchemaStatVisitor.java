package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveShowTablesStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

public class HiveSchemaStatVisitor extends SchemaStatVisitor implements HiveASTVisitor {

    @Override
    public void endVisit(HiveCreateTableStatement x) {
        
    }

    @Override
    public boolean visit(HiveCreateTableStatement x) {
        return visit((SQLCreateTableStatement) x);
    }

    @Override
    public void endVisit(HiveCreateTableStatement.PartitionedBy x) {

    }

    @Override
    public boolean visit(HiveCreateTableStatement.PartitionedBy x) {
        return false;
    }

    @Override
    public void endVisit(HiveShowTablesStatement x) {
        
    }

    @Override
    public boolean visit(HiveShowTablesStatement x) {
        return false;
    }
}
