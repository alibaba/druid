package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveShowTablesStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface HiveASTVisitor extends SQLASTVisitor {

    void endVisit(HiveShowTablesStatement x);

    boolean visit(HiveShowTablesStatement x);
    
    void endVisit(HiveCreateTableStatement x);
    
    boolean visit(HiveCreateTableStatement x);
    
    void endVisit(HiveCreateTableStatement.PartitionedBy x);
    
    boolean visit(HiveCreateTableStatement.PartitionedBy x);
}
