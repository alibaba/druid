package com.alibaba.druid.sql.dialect.clickhouse.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLRowDataType;
import com.alibaba.druid.sql.ast.SQLStructDataType;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class ClickhouseOutputVisitor extends SQLASTOutputVisitor implements ClickhouseVisitor {
    public ClickhouseOutputVisitor(Appendable appender) {
        super(appender);
    }

    public ClickhouseOutputVisitor(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public ClickhouseOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLWithSubqueryClause.Entry x) {
        if (x.getExpr() != null) {
            x.getExpr().accept(this);
        } else if (x.getSubQuery() != null) {
            print('(');
            println();
            SQLSelect query = x.getSubQuery();
            if (query != null) {
                query.accept(this);
            } else {
                x.getReturningStatement().accept(this);
            }
            println();
            print(')');
        }
        print(' ');
        print0(ucase ? "AS " : "as ");
        print0(x.getAlias());

        return false;
    }

    public boolean visit(SQLStructDataType x) {
        print0(ucase ? "NESTED (" : "nested (");
        incrementIndent();
        println();
        printlnAndAccept(x.getFields(), ",");
        decrementIndent();
        println();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLStructDataType.Field x) {
        SQLName name = x.getName();
        if (name != null) {
            name.accept(this);
        }
        SQLDataType dataType = x.getDataType();

        if (dataType != null) {
            print(' ');
            dataType.accept(this);
        }

        return false;
    }
}
