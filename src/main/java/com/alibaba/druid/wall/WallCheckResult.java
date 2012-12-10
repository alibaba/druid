package com.alibaba.druid.wall;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;

public class WallCheckResult {

    private final List<Violation>    violations    = new ArrayList<Violation>(2);
    private final List<SQLStatement> statementList = new ArrayList<SQLStatement>(2);

    public WallCheckResult(){

    }

    public WallCheckResult(Violation violation){
        this.violations.add(violation);
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public List<SQLStatement> getStatementList() {
        return statementList;
    }

}
