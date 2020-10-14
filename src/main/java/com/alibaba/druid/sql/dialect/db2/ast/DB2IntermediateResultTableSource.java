package com.alibaba.druid.sql.dialect.db2.ast;

import com.alibaba.druid.sql.ast.statement.SQLTableSourceImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class DB2IntermediateResultTableSource extends SQLTableSourceImpl {
    @Override
    protected void accept0(SQLASTVisitor v) {

    }

    public static enum  Type {
        OldTable,
        NewTable,
        FinalTable
    }
}
