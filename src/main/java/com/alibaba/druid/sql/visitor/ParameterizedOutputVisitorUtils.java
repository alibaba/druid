/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerParameterizedOutputVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;

public class ParameterizedOutputVisitorUtils {

    public static final String ATTR_PARAMS_SKIP = "druid.parameterized.skip";

    public static String parameterize(String sql, String dbType) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> statementList = parser.parseStatementList();
        if (statementList.size() == 0) {
            return sql;
        }

        StringBuilder out = new StringBuilder();
        ParameterizedVisitor visitor = createParameterizedOutputVisitor(out, dbType);

        for (int i = 0; i < statementList.size(); i++) {
            if (i > 0) {
                out.append(";\n");
            }
            SQLStatement stmt = statementList.get(i);
            stmt.accept(visitor);
        }

        if (visitor.getReplaceCount() == 0 && !parser.getLexer().hasComment()) {
            return sql;
        }

        return out.toString();
    }

    public static ParameterizedVisitor createParameterizedOutputVisitor(Appendable out, String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            return new OracleParameterizedOutputVisitor(out);
        }

        if (JdbcUtils.MYSQL.equals(dbType)) {
            return new MySqlParameterizedOutputVisitor(out);
        }

        if (JdbcUtils.MARIADB.equals(dbType)) {
            return new MySqlParameterizedOutputVisitor(out);
        }

        if (JdbcUtils.H2.equals(dbType)) {
            return new MySqlParameterizedOutputVisitor(out);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return new PGParameterizedOutputVisitor(out);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerParameterizedOutputVisitor(out);
        }

        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2ParameterizedOutputVisitor(out);
        }

        return new ParameterizedOutputVisitor(out);
    }

    public static boolean visit(ParameterizedVisitor v, SQLInListExpr x) {
        List<SQLExpr> targetList = x.getTargetList();

        boolean changed = true;
        if (targetList.size() == 1 && targetList.get(0) instanceof SQLVariantRefExpr) {
            changed = false;
        }

        x.getExpr().accept(v);

        if (x.isNot()) {
            v.print(" NOT IN (?)");
        } else {
            v.print(" IN (?)");
        }

        if (changed) {
            v.incrementReplaceCunt();
        }

        return false;
    }

    public static boolean visit(ParameterizedVisitor v, SQLIntegerExpr x) {
        if (!checkParameterize(x)) {
            return SQLASTOutputVisitorUtils.visit(v, x);
        }

        v.print('?');
        v.incrementReplaceCunt();
        return false;
    }

    public static boolean visit(ParameterizedVisitor v, SQLNumberExpr x) {
        if (!checkParameterize(x)) {
            return SQLASTOutputVisitorUtils.visit(v, x);
        }

        v.print('?');
        v.incrementReplaceCunt();
        return false;
    }

    public static boolean visit(ParameterizedVisitor v, SQLCharExpr x) {
        v.print('?');
        v.incrementReplaceCunt();
        return false;
    }

    public static boolean checkParameterize(SQLObject x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return false;
        }

        SQLObject parent = x.getParent();

        if (parent instanceof SQLDataType //
            || parent instanceof SQLColumnDefinition //
            || parent instanceof SQLServerTop //
            //|| parent instanceof SQLAssignItem //
            || parent instanceof SQLSelectOrderByItem //
        ) {
            return false;
        }

        return true;
    }

    public static boolean visit(ParameterizedVisitor v, SQLNCharExpr x) {
        v.print('?');
        v.incrementReplaceCunt();
        return false;
    }

    public static boolean visit(ParameterizedVisitor v, SQLNullExpr x) {
        SQLObject parent = x.getParent();
        if (parent instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) parent;
            if (binaryOpExpr.getOperator() == SQLBinaryOperator.IsNot
                || binaryOpExpr.getOperator() == SQLBinaryOperator.Is) {
                v.print("NULL");
                return false;
            }
        }

        v.print('?');
        v.incrementReplaceCunt();
        return false;
    }

    public static boolean visit(ParameterizedVisitor v, SQLVariantRefExpr x) {
        v.print('?');
        v.incrementReplaceCunt();
        return false;
    }
    
    public static boolean visit(ParameterizedVisitor v, SQLHexExpr x) {
        v.print('?');
        v.incrementReplaceCunt();
        return false;
    }

    public static SQLBinaryOpExpr merge(ParameterizedVisitor v, SQLBinaryOpExpr x) {
        SQLExpr left = x.getLeft();
        SQLExpr right = x.getRight();
        SQLObject parent = x.getParent();

        if (left instanceof SQLLiteralExpr && right instanceof SQLLiteralExpr) {
            if (x.getOperator() == SQLBinaryOperator.Equality //
                || x.getOperator() == SQLBinaryOperator.NotEqual) {
                if((left instanceof SQLIntegerExpr) && (right instanceof SQLIntegerExpr) ) {
                    if (((SQLIntegerExpr) left).getNumber().intValue() < 100) {
                        left.putAttribute(ATTR_PARAMS_SKIP, true);
                    }
                    if (((SQLIntegerExpr) right).getNumber().intValue() < 100) {
                        right.putAttribute(ATTR_PARAMS_SKIP, true);
                    }
                } else {
                    left.putAttribute(ATTR_PARAMS_SKIP, true);
                    right.putAttribute(ATTR_PARAMS_SKIP, true);
                }
            }
            return x;
        }

        for (;;) {
            if (x.getRight() instanceof SQLBinaryOpExpr) {
                if (x.getLeft() instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr leftBinaryExpr = (SQLBinaryOpExpr) x.getLeft();
                    if (leftBinaryExpr.getRight().equals(x.getRight())) {
                        x = leftBinaryExpr;
                        v.incrementReplaceCunt();
                        continue;
                    }
                }
                SQLExpr mergedRight = merge(v, (SQLBinaryOpExpr) x.getRight());
                if (mergedRight != x.getRight()) {
                    x = new SQLBinaryOpExpr(x.getLeft(), x.getOperator(), mergedRight);
                    v.incrementReplaceCunt();
                }
                x.setParent(parent);
            }

            break;
        }

        if (x.getLeft() instanceof SQLBinaryOpExpr) {
            SQLExpr mergedLeft = merge(v, (SQLBinaryOpExpr) x.getLeft());
            if (mergedLeft != x.getLeft()) {
                x = new SQLBinaryOpExpr(mergedLeft, x.getOperator(), x.getRight());
                v.incrementReplaceCunt();
            }
            x.setParent(parent);
        }

        // ID = ? OR ID = ? => ID = ?
        if (x.getOperator() == SQLBinaryOperator.BooleanOr) {
            if ((left instanceof SQLBinaryOpExpr) && (right instanceof SQLBinaryOpExpr)) {
                SQLBinaryOpExpr leftBinary = (SQLBinaryOpExpr) x.getLeft();
                SQLBinaryOpExpr rightBinary = (SQLBinaryOpExpr) x.getRight();

                if (mergeEqual(leftBinary, rightBinary)) {
                    v.incrementReplaceCunt();
                    return leftBinary;
                }

                if (isLiteralExpr(leftBinary.getLeft()) //
                    && leftBinary.getOperator() == SQLBinaryOperator.BooleanOr) {
                    if (mergeEqual(leftBinary.getRight(), right)) {
                        v.incrementReplaceCunt();
                        return leftBinary;
                    }
                }
            }
        }

        return x;
    }

    private static boolean mergeEqual(SQLExpr a, SQLExpr b) {
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

    private static boolean isLiteralExpr(SQLExpr expr) {
        if (expr instanceof SQLLiteralExpr) {
            return true;
        }

        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binary = (SQLBinaryOpExpr) expr;
            return isLiteralExpr(binary.getLeft()) && isLiteralExpr(binary.getRight());
        }

        return false;
    }
}
