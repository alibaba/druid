package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class HiveOutputVisitor extends SQLASTOutputVisitor implements HiveASTVisitor {

    public HiveOutputVisitor(Appendable appender){
        super(appender);
    }

    @Override
    public void endVisit(HiveCreateTableStatement x) {
        
    }

    @Override
    public boolean visit(HiveCreateTableStatement x) {
        visit((SQLCreateTableStatement) x);
        return false;
    }

}
