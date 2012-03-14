package com.alibaba.druid.filter.wall.spi;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.filter.wall.Violation;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVIsitorAdapter;

public class OracleWallVisitor extends OracleASTVIsitorAdapter implements WallVisitor {

    private final List<Violation> violations;

    public OracleWallVisitor(){
        this(new ArrayList<Violation>());
    }

    public OracleWallVisitor(List<Violation> violations){
        this.violations = violations;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        WallVisitorUtils.check(this, x);

        return true;
    }
}
