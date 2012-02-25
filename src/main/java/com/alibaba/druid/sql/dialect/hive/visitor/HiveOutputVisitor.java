package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement.PartitionedBy;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveShowTablesStatement;
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
        
        if (x.getPartitionedBy() != null) {
            println();
            x.getPartitionedBy().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(PartitionedBy x) {
        
    }

    @Override
    public boolean visit(PartitionedBy x) {
        print("PARTITIONED BY (");
        print(x.getName());
        print(" ");
        x.getType().accept(this);
        print(")");

        return false;
    }

    @Override
    public void endVisit(HiveShowTablesStatement x) {
        
    }

    @Override
    public boolean visit(HiveShowTablesStatement x) {
        print("SHOW TABLES");
        if (x.getPattern() != null) {
            print(" ");
            x.getPattern().accept(this);
        }
        return false;
    }

}
