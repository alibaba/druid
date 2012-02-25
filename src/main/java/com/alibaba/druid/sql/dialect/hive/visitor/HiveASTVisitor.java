package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface HiveASTVisitor extends SQLASTVisitor {

    void endVisit(HiveCreateTableStatement x);

    boolean visit(HiveCreateTableStatement x);
}
