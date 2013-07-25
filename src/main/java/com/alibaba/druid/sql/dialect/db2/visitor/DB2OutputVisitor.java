package com.alibaba.druid.sql.dialect.db2.visitor;

import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;


public class DB2OutputVisitor extends SQLASTOutputVisitor implements DB2ASTVisitor {
    public DB2OutputVisitor(Appendable appender){
        super(appender);
    }
}
