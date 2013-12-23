package com.alibaba.druid.sql.dialect.odps.visitor;

import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsert;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsertStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface OdpsASTVisitor extends SQLASTVisitor {

    void endVisit(OdpsCreateTableStatement x);

    boolean visit(OdpsCreateTableStatement x);

    void endVisit(OdpsInsertStatement x);

    boolean visit(OdpsInsertStatement x);
    
    void endVisit(OdpsInsert x);
    
    boolean visit(OdpsInsert x);
}
