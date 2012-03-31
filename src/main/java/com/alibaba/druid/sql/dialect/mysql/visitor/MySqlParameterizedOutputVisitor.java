package com.alibaba.druid.sql.dialect.mysql.visitor;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;

public class MySqlParameterizedOutputVisitor extends MySqlOutputVisitor {

    public MySqlParameterizedOutputVisitor() {
        this(new StringBuilder());
    }

    public MySqlParameterizedOutputVisitor(Appendable appender){
        super(appender);
    }

    public boolean visit(SQLInListExpr x) {
        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLBinaryOpExpr x) {
        x = ParameterizedOutputVisitorUtils.merge(x);

        return super.visit(x);
    }

    public boolean visit(SQLNullExpr x) {
        print('?');
        return false;
    }

    public boolean visit(SQLIntegerExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }

    public boolean visit(SQLNumberExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }

    public boolean visit(SQLCharExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }

    public boolean visit(SQLNCharExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        print("INSERT ");

        if (x.isLowPriority()) {
            print("LOW_PRIORITY ");
        }

        if (x.isDelayed()) {
            print("DELAYED ");
        }

        if (x.isHighPriority()) {
            print("HIGH_PRIORITY ");
        }

        if (x.isIgnore()) {
            print("IGNORE ");
        }

        print("INTO ");

        x.getTableName().accept(this);

        if (x.getColumns().size() > 0) {
            print(" (");
            for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
                if (i != 0) {
                    print(", ");
                }
                x.getColumns().get(i).accept(this);
            }
            print(")");
        }

        if (x.getValuesList().size() != 0) {
            print(" VALUES ");
            int size = x.getValuesList().size();
            if (size == 0) {
                print("()");
            } else {
                for (int i = 0; i < 1; ++i) {
                    if (i != 0) {
                        print(", ");
                    }
                    x.getValuesList().get(i).accept(this);
                }
            }
        }
        if (x.getQuery() != null) {
            print(" ");
            x.getQuery().accept(this);
        }

        if (x.getDuplicateKeyUpdate().size() != 0) {
            print(" ON DUPLICATE KEY UPDATE ");
            printAndAccept(x.getDuplicateKeyUpdate(), ", ");
        }

        return false;
    }
}
