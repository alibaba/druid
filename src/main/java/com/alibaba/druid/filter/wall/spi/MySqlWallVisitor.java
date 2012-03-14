package com.alibaba.druid.filter.wall.spi;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.filter.wall.Violation;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

public class MySqlWallVisitor extends MySqlASTVisitorAdapter {

    private final List<Violation> violations;

    public MySqlWallVisitor() {
        this(new ArrayList<Violation>());
    }

    public MySqlWallVisitor(List<Violation> violations) {
        this.violations = violations;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public boolean visit(MySqlExecuteStatement x) {
        return false;
    }
}
