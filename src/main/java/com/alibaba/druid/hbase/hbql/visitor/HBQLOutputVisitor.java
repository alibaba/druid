package com.alibaba.druid.hbase.hbql.visitor;

import com.alibaba.druid.hbase.hbql.ast.HBQLShowStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;


public class HBQLOutputVisitor extends SQLASTOutputVisitor implements HBQLVisitor {

    public HBQLOutputVisitor(Appendable appender){
        super(appender);
    }

    @Override
    public void endVisit(HBQLShowStatement x) {
        
    }

    @Override
    public boolean visit(HBQLShowStatement x) {
        print("SHOW TABLES");
        return false;
    }

}
