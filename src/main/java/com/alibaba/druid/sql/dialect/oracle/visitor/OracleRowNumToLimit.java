package com.alibaba.druid.sql.dialect.oracle.visitor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

public class OracleRowNumToLimit extends OracleASTVisitorAdapter {
    private Context context;
    private boolean removeSelectListRownum = true;

    @Override
    public boolean visit(SQLSelect x) {
        if (x.getWithSubQuery() != null) {
            x.getWithSubQuery().accept(this);
        }

        if (x.getQuery() != null) {
            x.getQuery().accept(this);
        }

        SQLSelectQueryBlock queryBlock = x.getQueryBlock();
        if (queryBlock != null && queryBlock.getLimit() != null) {
            SQLExpr rowCount = queryBlock.getLimit().getRowCount();
            if (rowCount instanceof SQLIntegerExpr && SQLIntegerExpr.isZero((SQLIntegerExpr) rowCount)) {
                x.setOrderBy(null);
            }
        }

        return false;
    }

    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        context = new Context(context);
        context.queryBlock = x;

        SQLExpr where = x.getWhere();
        if (where != null) {
            where.accept(this);
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            from.accept(this);
        }

        removeSelectListRowNum(x);

        List<SQLSelectItem> selectList = x.getSelectList();
        for (SQLSelectItem selectItem : selectList) {
            selectItem.accept(this);
        }

        SQLExpr startWith = x.getStartWith();
        if (startWith != null) {
            startWith.accept(this);
        }

        boolean allColumn = false;
        if (selectList.size() == 1) {
            SQLExpr expr = selectList.get(0).getExpr();
            if (expr instanceof SQLAllColumnExpr) {
                allColumn = true;
            } else if (expr instanceof SQLPropertyExpr && ((SQLPropertyExpr) expr).getName().equals("*")) {
                allColumn = true;
            }
        }
        if ((!allColumn)
                && x.getFrom() instanceof SQLSubqueryTableSource
                && ((SQLSubqueryTableSource) x.getFrom()).getSelect().getQuery() instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock subQuery = ((SQLSubqueryTableSource) x.getFrom()).getSelect().getQueryBlock();
            List<SQLSelectItem> subSelectList = subQuery.getSelectList();
            if (subSelectList.size() >= selectList.size()) {
                boolean match = true;
                for (int i = 0; i < selectList.size(); i++) {
                    if (!selectList.get(i).equals(subSelectList.get(i))) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    allColumn = true;
                }
            }
        }

        if (x.getParent() instanceof SQLSelect
                && x.getWhere() == null
                && x.getOrderBy() == null
                && allColumn
                && x.getLimit() != null
                && x.getFrom() instanceof SQLSubqueryTableSource
                && ((SQLSubqueryTableSource) x.getFrom()).getSelect().getQuery() instanceof SQLSelectQueryBlock) {
            SQLSelect select = (SQLSelect) x.getParent();
            SQLSelectQueryBlock subQuery = ((SQLSubqueryTableSource) x.getFrom()).getSelect().getQueryBlock();
            subQuery.mergeLimit(x.getLimit());

            x.setLimit(null);
            select.setQuery(subQuery);
            context.queryBlock = subQuery;

            context.fixLimit();

            subQuery.accept(this);
        }

        if (x.getParent() instanceof SQLUnionQuery
                && x.getWhere() == null
                && x.getOrderBy() == null
                && allColumn
                && x.getLimit() != null
                && x.getFrom() instanceof SQLSubqueryTableSource
                && ((SQLSubqueryTableSource) x.getFrom()).getSelect().getQuery() instanceof SQLSelectQueryBlock) {
            SQLUnionQuery union = (SQLUnionQuery) x.getParent();
            SQLSelectQueryBlock subQuery = ((SQLSubqueryTableSource) x.getFrom()).getSelect().getQueryBlock();
            subQuery.mergeLimit(x.getLimit());

            x.setLimit(null);
            if (union.getLeft() == x) {
                union.setLeft(subQuery);
            } else {
                union.setRight(subQuery);
            }
            context.queryBlock = subQuery;

            context.fixLimit();

            subQuery.accept(this);
        }

        context = context.parent;
        return false;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        if (x.getLeft() != null) {
            x.getLeft().accept(this);
        }

        if (x.getRight() != null) {
            x.getRight().accept(this);
        }


        if (x.getLeft() instanceof SQLSelectQueryBlock && x.getRight() instanceof SQLSelectQueryBlock) {
            if (x.getOperator() == SQLUnionOperator.MINUS) {
                boolean eqNonLimit;
                {
                    SQLSelectQueryBlock left = (SQLSelectQueryBlock) x.getLeft().clone();
                    SQLSelectQueryBlock right = (SQLSelectQueryBlock) x.getRight().clone();

                    left.setLimit(null);
                    right.setLimit(null);

                    eqNonLimit = left.toString().equals(right.toString());
                }

                if (eqNonLimit) {
                    SQLSelectQueryBlock merged = (SQLSelectQueryBlock) x.getLeft().clone();
                    SQLSelectQueryBlock right = (SQLSelectQueryBlock) x.getRight();

                    SQLLimit leftLimit = merged.getLimit();
                    SQLLimit rightLimit = right.getLimit();

                    if ((leftLimit == null && rightLimit == null)
                            || (leftLimit != null && leftLimit.equals(rightLimit))) {
                        merged.setLimit(new SQLLimit(0));
                    } else if (leftLimit == null) {
                        SQLExpr rightOffset = rightLimit.getOffset();
                        if (rightOffset != null && !SQLIntegerExpr.isZero(rightOffset)) {
                            return false; // can not merge
                        }
                        SQLLimit limit = new SQLLimit();
                        limit.setOffset(rightLimit.getRowCount());
                        merged.setLimit(limit);
                    } else {
                        SQLExpr rightOffset = rightLimit.getOffset();
                        if (rightOffset != null && !SQLIntegerExpr.isZero(rightOffset)) {
                            return false; // can not merge
                        }

                        SQLExpr leftOffset = leftLimit.getOffset();
                        if (leftOffset != null && !SQLIntegerExpr.isZero(leftOffset)) {
                            return false; // todo
                        }

                        SQLExpr rightRowCount = rightLimit.getRowCount();
                        SQLExpr leftRowCount = leftLimit.getRowCount();

                        SQLLimit limit = new SQLLimit();
                        limit.setOffset(rightRowCount);
                        limit.setRowCount(substract(leftRowCount, rightRowCount));

                        if (SQLIntegerExpr.isZero(limit.getRowCount())) {
                            limit.setRowCount(0);
                            limit.setOffset(null);
                            if (merged.getOrderBy() != null) {
                                merged.setOrderBy(null);
                            }
                        }

                        merged.setLimit(limit);
                    }

                    SQLObject parent = x.getParent();
                    if (parent instanceof SQLSelect) {
                        SQLSelect select = (SQLSelect) parent;
                        select.setQuery(merged);
                    } else if (parent instanceof SQLUnionQuery) {
                        SQLUnionQuery union = (SQLUnionQuery) parent;
                        if (union.getLeft() == x) {
                            union.setLeft(merged);
                        } else {
                            union.setRight(merged);
                        }
                    }
                }
            } else  if (x.getOperator() == SQLUnionOperator.INTERSECT) {
                boolean eqNonLimit;
                {
                    SQLSelectQueryBlock left = (SQLSelectQueryBlock) x.getLeft().clone();
                    SQLSelectQueryBlock right = (SQLSelectQueryBlock) x.getRight().clone();

                    left.setLimit(null);
                    right.setLimit(null);

                    eqNonLimit = left.toString().equals(right.toString());
                }

                if (eqNonLimit) {
                    SQLSelectQueryBlock merged = (SQLSelectQueryBlock) x.getLeft().clone();
                    SQLSelectQueryBlock right = (SQLSelectQueryBlock) x.getRight();

                    SQLLimit leftLimit = merged.getLimit();
                    SQLLimit rightLimit = right.getLimit();

                    if (rightLimit == null
                            || (rightLimit.equals(leftLimit))) {
                        // skip
                    } else if (leftLimit == null) {
                        merged.setLimit(rightLimit.clone());
                    } else {
                        SQLLimit limit = new SQLLimit();

                        SQLExpr rightOffset = rightLimit.getOffset();
                        SQLExpr leftOffset = leftLimit.getOffset();

                        if (leftOffset == null) {
                            limit.setOffset(rightOffset);
                        } else if (rightOffset == null) {
                            limit.setOffset(leftOffset);
                        } else if (rightOffset.equals(leftOffset)) {
                            limit.setOffset(leftOffset);
                        } else {
                            if ((!(leftOffset instanceof SQLIntegerExpr)) || !(rightOffset instanceof SQLIntegerExpr)) {
                                return false; // can not merged
                            }

                            limit.setOffset(SQLIntegerExpr.greatst((SQLIntegerExpr) leftOffset, (SQLIntegerExpr) rightOffset));
                        }


                        SQLExpr rightRowCount = rightLimit.getRowCount();
                        SQLExpr leftRowCount = leftLimit.getRowCount();

                        SQLExpr leftEnd = leftOffset == null ? leftRowCount : substract(leftRowCount, leftOffset);
                        SQLExpr rightEnd = rightOffset == null ? rightRowCount : substract(rightRowCount, rightOffset);

                        if ((leftEnd != null && !(leftEnd instanceof SQLIntegerExpr)) || (rightEnd != null && !(rightEnd instanceof SQLIntegerExpr))) {
                            return false; // can not merged
                        }

                        SQLIntegerExpr end = SQLIntegerExpr.least((SQLIntegerExpr) leftEnd, (SQLIntegerExpr) rightEnd);

                        if (limit.getOffset() == null) {
                            limit.setRowCount(end);
                        } else {
                            limit.setRowCount(substract(end, limit.getOffset()));
                        }

                        merged.setLimit(limit);
                    }

                    SQLObject parent = x.getParent();
                    if (parent instanceof SQLSelect) {
                        SQLSelect select = (SQLSelect) parent;
                        select.setQuery(merged);
                    } else if (parent instanceof SQLUnionQuery) {
                        SQLUnionQuery union = (SQLUnionQuery) parent;
                        if (union.getLeft() == x) {
                            union.setLeft(merged);
                        } else {
                            union.setRight(merged);
                        }
                    }
                }
            }
        }

        return false;
    }

    private void removeSelectListRowNum(SQLSelectQueryBlock x) {
        SQLTableSource from = x.getFrom();
        SQLLimit limit = x.getLimit();
        if (limit == null
                && from instanceof SQLSubqueryTableSource
                && ((SQLSubqueryTableSource) from).getSelect().getQuery() instanceof SQLSelectQueryBlock) {
            limit = ((SQLSubqueryTableSource) from).getSelect().getQueryBlock().getLimit();
        }

        if (!removeSelectListRownum) {
            return;

        }
        List<SQLSelectItem> selectList = x.getSelectList();
        for (int i = selectList.size() - 1; i >= 0; i--) {
            SQLSelectItem selectItem = selectList.get(i);
            SQLExpr expr = selectItem.getExpr();
            if (isRowNum(expr)
                    && limit != null) {
                selectList.remove(i);
            }
        }
    }

    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        SQLExpr left = x.getLeft();
        SQLExpr right = x.getRight();
        SQLBinaryOperator op = x.getOperator();

        if (context == null || context.queryBlock == null) {
            return false;
        }

        boolean isRowNum = isRowNum(left);
        if (isRowNum) {
            if (op == SQLBinaryOperator.LessThan) {
                if (SQLUtils.replaceInParent(x, null)) {
                    context.setLimit(decrement(right));
                    // 如果存在 offset, 重新计算 rowCount
                    context.fixLimit();
                }
                return false;
            } else if (op == SQLBinaryOperator.LessThanOrEqual) {
                if (SQLUtils.replaceInParent(x, null)) {
                    context.setLimit(right);
                    // 如果存在 offset, 重新计算 rowCount
                    context.fixLimit();
                }
                return false;
            } else if (op == SQLBinaryOperator.Equality) {
                if (SQLUtils.replaceInParent(x, null)) {
                    context.setLimit(right);
                    // 如果存在 offset, 重新计算 rowCount
                    context.fixLimit();
                }
                return false;
            } else if (op == SQLBinaryOperator.GreaterThanOrEqual) {

                if (SQLUtils.replaceInParent(x, null)) {
                    context.setOffset(decrement(right));
                    // 如果存在 offset, 重新计算 rowCount
                    context.fixLimit();
                }
                return false;
            } else if (op == SQLBinaryOperator.GreaterThan) {
                if (SQLUtils.replaceInParent(x, null)) {
                    context.setOffset(right);
                    // 如果存在 offset, 重新计算 rowCount
                    context.fixLimit();
                }
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean visit(SQLBetweenExpr x) {
        if (!isRowNum(x.getTestExpr())) {
            return true;
        }

        if (SQLUtils.replaceInParent(x, null)) {
            SQLExpr offset = decrement(x.getBeginExpr());
            context.setOffset(offset);
            if (offset instanceof SQLIntegerExpr) {
                int val = ((SQLIntegerExpr) offset).getNumber().intValue();
                if (val < 0) {
                    offset = new SQLIntegerExpr(0);
                }
            }
            context.setLimit(substract(x.getEndExpr(), offset));
            SQLLimit limit = context.queryBlock.getLimit();
            if (limit != null) {
                limit.putAttribute("oracle.isFixLimit", Boolean.TRUE);
            }

        }

        return false;
    }

    public boolean isRowNum(SQLExpr x) {
        if (x instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) x;
            long nameHashCode64 = identifierExpr.nameHashCode64();
            if (nameHashCode64 == FnvHash.Constants.ROWNUM) {
                return true;
            }

            if (context != null
                    && context.queryBlock != null
                    && context.queryBlock.getFrom() instanceof SQLSubqueryTableSource
                    && ((SQLSubqueryTableSource) context.queryBlock.getFrom()).getSelect().getQuery() instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock subQueryBlock = ((SQLSubqueryTableSource) context.queryBlock.getFrom()).getSelect().getQueryBlock();
                SQLSelectItem selectItem = subQueryBlock.findSelectItem(nameHashCode64);
                this.context = new Context(this.context);
                this.context.queryBlock = subQueryBlock;
                try {
                    if (selectItem != null && isRowNum(selectItem.getExpr())) {
                        return true;
                    }
                } finally {
                    this.context = this.context.parent;
                }
            }
        }

        return false;
    }

    public static class Context {
        public final Context parent;

        public Context(Context parent) {
            this.parent = parent;
        }

        public SQLSelectQueryBlock queryBlock;

        void setLimit(SQLExpr x) {
            if (x instanceof SQLIntegerExpr) {
                int val = ((SQLIntegerExpr) x).getNumber().intValue();
                if (val < 0) {
                    x = new SQLIntegerExpr(0);
                }
            }

            SQLLimit limit = queryBlock.getLimit();
            if (limit == null) {
                limit = new SQLLimit();
                queryBlock.setLimit(limit);
            }
            limit.setRowCount(x);
        }

        void fixLimit() {
            SQLLimit limit = queryBlock.getLimit();
            if (limit == null) {
                return;
            }

            if (limit.getAttribute("oracle.isFixLimit") == Boolean.TRUE) {
                return;
            }

            if (limit.getRowCount() != null && limit.getOffset() != null) {
                if (limit.getRowCount() instanceof SQLIntegerExpr && limit.getOffset() instanceof SQLIntegerExpr) {
                    SQLIntegerExpr rowCountExpr = SQLIntegerExpr.substract((SQLIntegerExpr) limit.getRowCount(), (SQLIntegerExpr) limit.getOffset());
                    limit.setRowCount(rowCountExpr);
                } else {
                    limit.setRowCount(substract(limit.getRowCount(), limit.getOffset()));
                }
                limit.putAttribute("oracle.isFixLimit", Boolean.TRUE);

            }
        }

        void setOffset(SQLExpr x) {
            if (x instanceof SQLIntegerExpr) {
                int val = ((SQLIntegerExpr) x).getNumber().intValue();
                if (val < 0) {
                    x = new SQLIntegerExpr(0);
                }
            }

            SQLLimit limit = queryBlock.getLimit();
            if (limit == null) {
                limit = new SQLLimit();
                queryBlock.setLimit(limit);
            }
            limit.setOffset(x);
        }
    }

    public static SQLExpr decrement(SQLExpr x) {
        if (x instanceof SQLIntegerExpr) {
            int val = ((SQLIntegerExpr) x).getNumber().intValue() - 1;
            return new SQLIntegerExpr(val);
        }

        return new SQLBinaryOpExpr(x.clone(), SQLBinaryOperator.Subtract, new SQLIntegerExpr(1));
    }

    public static SQLExpr substract(SQLExpr left, SQLExpr right) {
        if (left == null && right == null) {
            return null;
        }

        if (left == null) {
            return null;
        }

        if (left instanceof SQLIntegerExpr && right instanceof SQLIntegerExpr) {
            int rightVal = Math.max(0, ((SQLIntegerExpr) right).getNumber().intValue());
            int leftVal = ((SQLIntegerExpr) left).getNumber().intValue();
            int val = leftVal - rightVal;
            if (val < 0) {
                val = 0;
            }
            return new SQLIntegerExpr(val);
        } else {
            return new SQLBinaryOpExpr(left, SQLBinaryOperator.Subtract, right);
        }

    }

    public static SQLExpr increment(SQLExpr x) {
        if (x instanceof SQLIntegerExpr) {
            int val = ((SQLIntegerExpr) x).getNumber().intValue() + 1;
            return new SQLIntegerExpr(val);
        }

        return new SQLBinaryOpExpr(x.clone(), SQLBinaryOperator.Add, new SQLIntegerExpr(1));
    }


}
