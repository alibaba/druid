package com.alibaba.druid.filter.wall;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface WallVisitor extends SQLASTVisitor {

    WallProvider getProvider();

    List<Violation> getViolations();

    boolean containsPermitTable(String name);

    boolean containsPermitObjects(String name);

    String toSQL(SQLObject obj);
}
