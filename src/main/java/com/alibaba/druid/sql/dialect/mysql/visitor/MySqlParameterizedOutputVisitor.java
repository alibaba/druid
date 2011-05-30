package com.alibaba.druid.sql.dialect.mysql.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;

public class MySqlParameterizedOutputVisitor extends MySqlOutputVisitor {

    private static final String ATTR_PARAMS_SKIP = "_params.skip_";

    public MySqlParameterizedOutputVisitor(Appendable appender){
        super(appender);
    }

    public void println() {
        print(' ');
    }

    public boolean visit(SQLInListExpr x) {
        x.getExpr().accept(this);

        if (x.isNot()) {
            print(" NOT IN (?)");
        } else {
            print(" IN (?)");
        }

        return false;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        x = merge(x);

        return super.visit(x);
    }

    public SQLBinaryOpExpr merge(SQLBinaryOpExpr x) {
        if (x.getLeft() instanceof SQLLiteralExpr && x.getRight() instanceof SQLLiteralExpr) {
            x.getLeft().getAttributes().put(ATTR_PARAMS_SKIP, true);
            x.getRight().getAttributes().put(ATTR_PARAMS_SKIP, true);
            return x;
        }

        if (x.getRight() instanceof SQLLiteralExpr) {
            x = new SQLBinaryOpExpr(x.getLeft(), x.getOperator(), new SQLVariantRefExpr("?"));
        }

        if (x.getLeft() instanceof SQLLiteralExpr) {
            x = new SQLBinaryOpExpr(new SQLVariantRefExpr("?"), x.getOperator(), x.getRight());
        }

        if (x.getRight() instanceof SQLBinaryOpExpr) {
            x = new SQLBinaryOpExpr(x.getLeft(), x.getOperator(), merge((SQLBinaryOpExpr) x.getRight()));
        }

        if (x.getLeft() instanceof SQLBinaryOpExpr) {
            x = new SQLBinaryOpExpr(merge((SQLBinaryOpExpr) x.getLeft()), x.getOperator(), x.getRight());
        }

        // ID = ? OR ID = ? => ID = ?
        if (x.getOperator() == SQLBinaryOperator.BooleanOr) {
            if ((x.getLeft() instanceof SQLBinaryOpExpr) && (x.getRight() instanceof SQLBinaryOpExpr)) {
                SQLBinaryOpExpr left = (SQLBinaryOpExpr) x.getLeft();
                SQLBinaryOpExpr right = (SQLBinaryOpExpr) x.getRight();

                if (mergeEqual(left, right)) {
                    return left;
                }

                if (isLiteralExpr(left.getLeft()) && left.getOperator() == SQLBinaryOperator.BooleanOr) {
                    if (mergeEqual(left.getRight(), right)) {
                        return left;
                    }
                }
            }
        }

        return x;
    }

    private boolean mergeEqual(SQLExpr a, SQLExpr b) {
        if (!(a instanceof SQLBinaryOpExpr)) {
            return false;
        }
        if (!(b instanceof SQLBinaryOpExpr)) {
            return false;
        }

        SQLBinaryOpExpr binaryA = (SQLBinaryOpExpr) a;
        SQLBinaryOpExpr binaryB = (SQLBinaryOpExpr) b;

        if (binaryA.getOperator() != SQLBinaryOperator.Equality) {
            return false;
        }

        if (binaryB.getOperator() != SQLBinaryOperator.Equality) {
            return false;
        }

        if (!(binaryA.getRight() instanceof SQLLiteralExpr || binaryA.getRight() instanceof SQLVariantRefExpr)) {
            return false;
        }

        if (!(binaryB.getRight() instanceof SQLLiteralExpr || binaryB.getRight() instanceof SQLVariantRefExpr)) {
            return false;
        }

        return binaryA.getLeft().toString().equals(binaryB.getLeft().toString());
    }

    private boolean isLiteralExpr(SQLExpr expr) {
        if (expr instanceof SQLLiteralExpr) {
            return true;
        }

        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binary = (SQLBinaryOpExpr) expr;
            return isLiteralExpr(binary.getLeft()) && isLiteralExpr(binary.getRight());
        }

        return false;
    }

    public boolean visit(SQLIntegerExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }

    public boolean visit(SQLNumberExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }

    public boolean visit(SQLCharExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }

    public boolean visit(SQLNCharExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ATTR_PARAMS_SKIP))) {
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
