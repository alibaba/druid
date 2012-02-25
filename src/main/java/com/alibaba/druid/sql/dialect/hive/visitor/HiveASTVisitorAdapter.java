package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveShowTablesStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class HiveASTVisitorAdapter extends SQLASTVisitorAdapter implements HiveASTVisitor {

    @Override
    public void endVisit(HiveCreateTableStatement x) {

    }

    @Override
    public boolean visit(HiveCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(HiveCreateTableStatement.PartitionedBy x) {

    }

    @Override
    public boolean visit(HiveCreateTableStatement.PartitionedBy x) {
        return true;
    }

    @Override
    public void endVisit(HiveShowTablesStatement x) {

    }

    @Override
    public boolean visit(HiveShowTablesStatement x) {
        return true;
    }

}
