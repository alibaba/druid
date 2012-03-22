package com.alibaba.druid.wall;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface WallVisitor extends SQLASTVisitor {

    WallConfig getConfig();
    
    WallProvider getProvider();

    List<Violation> getViolations();
    
    boolean isPermitTable(String name);

    String toSQL(SQLObject obj);
}
