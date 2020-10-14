package com.alibaba.druid.sql.transform;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExprGroup;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.repository.SchemaRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SQLUnifiedVisitor extends MySqlASTVisitorAdapter {

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        SchemaRepository repository = new SchemaRepository(DbType.mysql);
        repository.resolve(x);

        List<SQLSelectItem> selectList = x.getSelectList();
        for (SQLSelectItem item : selectList) {
            if (item.getExpr() instanceof SQLPropertyExpr) {
                SQLPropertyExpr expr = (SQLPropertyExpr) item.getExpr();

                SQLTableSource resolvedTableSource = expr.getResolvedTableSource();

                if (resolvedTableSource != null) {
                    String alias = resolvedTableSource.getAlias();
                    if (alias != null) {
                        expr.setOwner(alias);
                    } else {
                        expr.setOwner(resolvedTableSource.computeAlias());
                    }
                }
            }
            item.setAlias(null);
        }

        Collections.sort(selectList, new Comparator<SQLSelectItem>() {
            @Override
            public int compare(SQLSelectItem o1, SQLSelectItem o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });

        if (x.getFrom() != null) {
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            x.getWhere().accept(this);
        }

        if(x.getGroupBy() != null) {
            x.getGroupBy().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        SQLBinaryOperator operator = x.getOperator();
        if (operator == SQLBinaryOperator.BooleanOr || operator == SQLBinaryOperator.BooleanAnd) {
            SQLExpr left = x.getLeft();
            left.accept(this);

            SQLExpr right = x.getRight();
            right.accept(this);

            int compareResult = left.toString().compareToIgnoreCase(right.toString());
            if (compareResult > 0) {
                x.setLeft(right);
                x.setRight(left);
            }
        } else {
            SQLExpr left = x.getLeft();
            left.accept(this);

            SQLExpr right = x.getRight();
            right.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLPropertyExpr x) {
        SQLExpr owner = x.getOwner();

        SQLTableSource resolvedTableSource = x.getResolvedTableSource();

        if (resolvedTableSource != null) {
            String alias = resolvedTableSource.getAlias();
            if (alias != null) {
                x.setOwner(alias);
            } else {
                x.setOwner(resolvedTableSource.computeAlias());
            }
        }
        return false;
    }

    @Override
    public boolean visit(SQLBinaryOpExprGroup x) {
        Collections.sort(x.getItems(), new Comparator<SQLExpr>() {
            @Override
            public int compare(SQLExpr o1, SQLExpr o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
        return false;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        SQLSelectQuery left = x.getLeft();
        SQLSelectQuery right = x.getRight();

        left.accept(this);
        right.accept(this);

        int compareResult = left.toString().compareToIgnoreCase(right.toString());
        if (compareResult > 0) {
            x.setLeft(right);
            x.setRight(left);
        }
        return false;
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        String tablename = ((SQLName) x.getExpr()).getSimpleName();
        x.setAlias(tablename);
        return false;
    }

    @Override
    public boolean visit(SQLSelectGroupByClause x) {
        Collections.sort(x.getItems(), new Comparator<SQLExpr>() {
            @Override
            public int compare(SQLExpr o1, SQLExpr o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });

        SQLExpr having = x.getHaving();
        if (having != null) {
            having.accept(this);
        }

        return false;
    }
}
