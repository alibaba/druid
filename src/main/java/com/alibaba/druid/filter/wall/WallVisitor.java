package com.alibaba.druid.filter.wall;

import java.util.List;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface WallVisitor extends SQLASTVisitor {

    List<Violation> getViolations();

    Set<String> getPermitFunctions();

    Set<String> getPermitSchemas();

    Set<String> getPermitTables();
    
    boolean containsPermitTable(String name);

    Set<String> getPermitNames();
    
    String toSQL(SQLObject obj);
}
