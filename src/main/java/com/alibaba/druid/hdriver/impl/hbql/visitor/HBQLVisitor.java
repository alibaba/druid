package com.alibaba.druid.hdriver.impl.hbql.visitor;

import com.alibaba.druid.hdriver.impl.hbql.ast.HBQLShowStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface HBQLVisitor extends SQLASTVisitor {

    void endVisit(HBQLShowStatement x);

    boolean visit(HBQLShowStatement x);
}
