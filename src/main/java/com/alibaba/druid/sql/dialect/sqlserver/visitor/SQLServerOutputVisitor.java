package com.alibaba.druid.sql.dialect.sqlserver.visitor;

import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.Top;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class SQLServerOutputVisitor extends SQLASTOutputVisitor implements SQLServerASTVisitor {

    public SQLServerOutputVisitor(Appendable appender){
        super(appender);
    }

    public boolean visit(SQLServerSelectQueryBlock select) {
        print("SELECT ");

        if (SQLSetQuantifier.ALL == select.getDistionOption()) {
            print("ALL ");
        } else if (SQLSetQuantifier.DISTINCT == select.getDistionOption()) {
            print("DISTINCT ");
        } else if (SQLSetQuantifier.UNIQUE == select.getDistionOption()) {
            print("UNIQUE ");
        }

        if (select.getTop() != null) {
            select.getTop().accept(this);
        }

        printSelectList(select.getSelectList());

        if (select.getFrom() != null) {
            println();
            print("FROM ");
            select.getFrom().accept(this);
        }

        if (select.getWhere() != null) {
            println();
            print("WHERE ");
            select.getWhere().accept(this);
        }

        if (select.getGroupBy() != null) {
            print(" ");
            select.getGroupBy().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(SQLServerSelectQueryBlock x) {

    }

    @Override
    public boolean visit(Top x) {
        print("TOP ");
        x.getExpr().accept(this);
        print(" ");
        return false;
    }

    @Override
    public void endVisit(Top x) {

    }

}
