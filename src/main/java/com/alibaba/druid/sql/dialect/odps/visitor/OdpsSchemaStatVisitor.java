package com.alibaba.druid.sql.dialect.odps.visitor;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsert;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsertStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;


public class OdpsSchemaStatVisitor extends SchemaStatVisitor implements OdpsASTVisitor {

    @Override
    public void endVisit(OdpsCreateTableStatement x) {
        super.endVisit((SQLCreateTableStatement) x);
    }

    @Override
    public boolean visit(OdpsCreateTableStatement x) {
        return super.visit((SQLCreateTableStatement) x);
    }

    @Override
    public void endVisit(OdpsInsertStatement x) {
        
    }

    @Override
    public boolean visit(OdpsInsertStatement x) {
        return false;
    }

    @Override
    public void endVisit(OdpsInsert x) {
        
    }

    @Override
    public boolean visit(OdpsInsert x) {
        return false;
    }

    
}
