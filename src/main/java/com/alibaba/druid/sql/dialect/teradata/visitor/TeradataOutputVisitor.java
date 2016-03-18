package com.alibaba.druid.sql.dialect.teradata.visitor;

import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class TeradataOutputVisitor extends SQLASTOutputVisitor implements TeradataASTVisitor {

    public TeradataOutputVisitor(Appendable appender){
        super(appender);
    }

}
