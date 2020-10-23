package com.alibaba.druid.sql.dialect.blink.vsitor;

import com.alibaba.druid.sql.ast.statement.SQLAlterTableUnarchivePartition;
import com.alibaba.druid.sql.dialect.blink.ast.BlinkCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface BlinkVisitor extends SQLASTVisitor  {
    boolean visit(BlinkCreateTableStatement x);
    void endVisit(BlinkCreateTableStatement x);
}
