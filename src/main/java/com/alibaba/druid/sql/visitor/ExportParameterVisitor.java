package com.alibaba.druid.sql.visitor;

import java.util.List;


public interface ExportParameterVisitor extends SQLASTVisitor {
    List<Object> getParameters();
}
