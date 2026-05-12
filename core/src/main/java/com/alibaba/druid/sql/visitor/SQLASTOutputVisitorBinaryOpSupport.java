/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExprGroup;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLExprUtils;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.util.ArrayList;
import java.util.List;

class SQLASTOutputVisitorBinaryOpSupport {
    private final SQLASTOutputVisitor visitor;

    SQLASTOutputVisitorBinaryOpSupport(SQLASTOutputVisitor visitor) {
        this.visitor = visitor;
    }

    boolean visitBetweenExpr(SQLBetweenExpr x) {
        final SQLExpr testExpr = x.getTestExpr();
        final SQLExpr beginExpr = x.getBeginExpr();
        final SQLExpr endExpr = x.getEndExpr();
        if (x.isParenthesized()) {
            visitor.print('(');
        }
        if (testExpr != null) {
            visitor.printExpr(testExpr, visitor.parameterized);
        }

        if (x.isNot()) {
            visitor.print0(visitor.ucase ? " NOT BETWEEN " : " not between ");
        } else {
            visitor.print0(visitor.ucase ? " BETWEEN " : " between ");
        }

        int lines = visitor.lines;
        if (beginExpr instanceof SQLBinaryOpExpr) {
            visitor.incrementIndent();
            visitor.printExpr(beginExpr, visitor.parameterized);
            visitor.decrementIndent();
        } else {
            visitor.printExpr(beginExpr, visitor.parameterized);
        }

        if (lines != visitor.lines) {
            visitor.println();
            visitor.print0(visitor.ucase ? "AND " : "and ");
        } else {
            visitor.print0(visitor.ucase ? " AND " : " and ");
        }

        if (endExpr instanceof SQLBinaryOpExpr) {
            visitor.incrementIndent();
            visitor.printExpr(endExpr, visitor.parameterized);
            visitor.decrementIndent();
        } else {
            visitor.printExpr(endExpr, visitor.parameterized);
        }

        if (x.getHint() != null) {
            x.getHint().accept(visitor);
        }
        if (x.isParenthesized()) {
            visitor.print(')');
        }
        return false;
    }

    boolean visitBinaryOpExprGroup(SQLBinaryOpExprGroup x) {
        SQLObject parent = x.getParent();
        SQLBinaryOperator operator = x.getOperator();

        boolean isRoot = parent instanceof SQLSelectQueryBlock || parent instanceof SQLBinaryOpExprGroup;

        List<SQLExpr> items = x.getItems();
        if (items.isEmpty()) {
            visitor.print("true");
            return false;
        }

        if (isRoot) {
            visitor.indentCount++;
        }

        if (visitor.parameterized) {
            SQLExpr firstLeft = null;
            SQLBinaryOperator firstOp = null;
            List<Object> parameters = new ArrayList<Object>(items.size());

            List<SQLBinaryOpExpr> literalItems = null;

            if ((operator != SQLBinaryOperator.BooleanOr || !visitor.isEnabled(VisitorFeature.OutputParameterizedQuesUnMergeOr))
                    && (operator != SQLBinaryOperator.BooleanAnd || !visitor.isEnabled(VisitorFeature.OutputParameterizedQuesUnMergeAnd))) {
                for (int i = 0; i < items.size(); i++) {
                    SQLExpr item = items.get(i);
                    if (item instanceof SQLBinaryOpExpr) {
                        SQLBinaryOpExpr binaryItem = (SQLBinaryOpExpr) item;
                        SQLExpr left = binaryItem.getLeft();
                        SQLExpr right = binaryItem.getRight();

                        if (right instanceof SQLLiteralExpr && !(right instanceof SQLNullExpr)) {
                            if (left instanceof SQLLiteralExpr) {
                                if (literalItems == null) {
                                    literalItems = new ArrayList<SQLBinaryOpExpr>();
                                }
                                literalItems.add(binaryItem);
                                continue;
                            }

                            if (visitor.parameters != null) {
                                ExportParameterVisitorUtils.exportParameter(parameters, right);
                            }
                        } else if (right instanceof SQLVariantRefExpr) {
                            // skip
                        } else {
                            firstLeft = null;
                            break;
                        }

                        if (firstLeft == null) {
                            firstLeft = binaryItem.getLeft();
                            firstOp = binaryItem.getOperator();
                        } else {
                            if (firstOp != binaryItem.getOperator() || !SQLExprUtils.equals(firstLeft, left)) {
                                firstLeft = null;
                                break;
                            }
                        }
                    } else {
                        firstLeft = null;
                        break;
                    }
                }
            }

            if (firstLeft != null) {
                if (literalItems != null) {
                    for (SQLBinaryOpExpr literalItem : literalItems) {
                        visitor.visit(literalItem);
                        visitor.println();
                        visitor.printOperator(operator);
                        visitor.print(' ');
                    }
                }
                visitor.printExpr(firstLeft, visitor.parameterized);
                visitor.print(' ');
                visitor.printOperator(firstOp);
                visitor.print0(" ?");

                if (visitor.parameters != null && parameters.size() > 0) {
                    visitor.parameters.addAll(parameters);
                }

                visitor.incrementReplaceCunt();
                if (isRoot) {
                    visitor.indentCount--;
                }
                return false;
            }
        }

        for (int i = 0; i < items.size(); i++) {
            SQLExpr item = items.get(i);

            if (i != 0) {
                visitor.println();
                visitor.printOperator(operator);
                visitor.print(' ');
            }

            if (item.hasBeforeComment()) {
                visitor.printlnComments(item.getBeforeCommentsDirect());
            }

            if (item instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) item;
                SQLBinaryOperator itemOp = binaryOpExpr.getOperator();

                boolean isLogic = itemOp.isLogical();
                if (isLogic) {
                    visitor.indentCount++;
                }

                visitor.visit(binaryOpExpr);

                if (isLogic) {
                    visitor.indentCount--;
                }
            } else if (item instanceof SQLBinaryOpExprGroup) {
                visitor.print('(');
                visitor.visit((SQLBinaryOpExprGroup) item);
                visitor.print(')');
            } else {
                visitor.printExpr(item, visitor.parameterized);
            }
        }
        List<String> afterComments = x.getAfterCommentsDirect();
        if (!visitor.parameterized) {
            if (afterComments != null && !afterComments.isEmpty() && visitor.isPrettyFormat()) {
                visitor.print(' ');
            }
            visitor.printlnComment(afterComments);
        }
        if (isRoot) {
            visitor.indentCount--;
        }
        return false;
    }

    boolean visitBinaryOpExpr(SQLBinaryOpExpr x) {
        if (x.isParenthesized()) {
            visitor.print('(');
        }
        boolean rs = visitor.visitInternal(x);
        if (x.isParenthesized()) {
            visitor.print(')');
        }
        List<String> afterComments = x.getAfterCommentsDirect();
        if (!visitor.parameterized) {
            if (afterComments != null && !afterComments.isEmpty() && visitor.isPrettyFormat()) {
                visitor.print(' ');
            }
            visitor.printlnComment(afterComments);
        }
        return rs;
    }
}
