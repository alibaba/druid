package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

public class HiveSchemaStatVisitor extends SchemaStatVisitor implements HiveASTVisitor {

    @Override
    public void endVisit(HiveCreateTableStatement x) {
        
    }

    @Override
    public boolean visit(HiveCreateTableStatement x) {
        return false;
    }

}
