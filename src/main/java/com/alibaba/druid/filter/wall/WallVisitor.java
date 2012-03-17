package com.alibaba.druid.filter.wall;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface WallVisitor extends SQLASTVisitor {

    WallConfig getConfig();

    List<Violation> getViolations();
    
    boolean isPermitTable(String name);

    String toSQL(SQLObject obj);
}
