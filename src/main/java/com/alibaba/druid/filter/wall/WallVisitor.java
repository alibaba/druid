package com.alibaba.druid.filter.wall;

import java.util.List;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface WallVisitor extends SQLASTVisitor {

    List<Violation> getViolations();
}
