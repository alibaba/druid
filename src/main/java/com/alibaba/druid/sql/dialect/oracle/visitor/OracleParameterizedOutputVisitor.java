package com.alibaba.druid.sql.dialect.oracle.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;

public class OracleParameterizedOutputVisitor extends OracleOutputVisitor {

    private static final String ATTR_PARAMS_SKIP = "_params.skip_";

    public OracleParameterizedOutputVisitor(Appendable appender){
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

    public boolean visit(SQLNullExpr x) {
        print('?');
        return false;
    }

    public SQLBinaryOpExpr merge(SQLBinaryOpExpr x) {
        if (x.getLeft() instanceof SQLLiteralExpr && x.getRight() instanceof SQLLiteralExpr) {
            if (x.getOperator() == SQLBinaryOperator.Equality || x.getOperator() == SQLBinaryOperator.NotEqual) {
                x.getLeft().getAttributes().put(ATTR_PARAMS_SKIP, true);
                x.getRight().getAttributes().put(ATTR_PARAMS_SKIP, true);
            }
            return x;
        }

        if (x.getRight() instanceof SQLLiteralExpr) {
            x = new SQLBinaryOpExpr(x.getLeft(), x.getOperator(), new SQLVariantRefExpr("?"));
        }

        if (x.getLeft() instanceof SQLLiteralExpr) {
            x = new SQLBinaryOpExpr(new SQLVariantRefExpr("?"), x.getOperator(), x.getRight());
        }

        for (;;) {
            if (x.getRight() instanceof SQLBinaryOpExpr) {
                if (x.getLeft() instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr leftBinaryExpr = (SQLBinaryOpExpr) x.getLeft();
                    if (leftBinaryExpr.getRight().equals(x.getRight())) {
                        x = leftBinaryExpr;
                        continue;
                    }
                }
                x = new SQLBinaryOpExpr(x.getLeft(), x.getOperator(), merge((SQLBinaryOpExpr) x.getRight()));
            }

            break;
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

}
