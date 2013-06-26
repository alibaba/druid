/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGParameterizedOutputVisitor;
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
        
        SQLStatement stmt = statementList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = createParameterizedOutputVisitor(out, dbType);
        stmt.accept(visitor);

        return out.toString();
    }

    public static SQLASTOutputVisitor createParameterizedOutputVisitor(Appendable out, String dbType) {
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

        return new ParameterizedOutputVisitor(out);
    }

    public static boolean visit(SQLASTOutputVisitor v, SQLInListExpr x) {
        x.getExpr().accept(v);

        if (x.isNot()) {
            v.print(" NOT IN (?)");
        } else {
            v.print(" IN (?)");
        }

        return false;
    }

    public static SQLBinaryOpExpr merge(SQLBinaryOpExpr x) {
        SQLObject parent = x.getParent();
        if (x.getLeft() instanceof SQLLiteralExpr && x.getRight() instanceof SQLLiteralExpr) {
            if (x.getOperator() == SQLBinaryOperator.Equality || x.getOperator() == SQLBinaryOperator.NotEqual) {
                x.getLeft().getAttributes().put(ATTR_PARAMS_SKIP, true);
                x.getRight().getAttributes().put(ATTR_PARAMS_SKIP, true);
            }
            return x;
        }

        if (x.getRight() instanceof SQLLiteralExpr) {
            x = new SQLBinaryOpExpr(x.getLeft(), x.getOperator(), new SQLVariantRefExpr("?"));
            x.setParent(parent);
        }

        if (x.getLeft() instanceof SQLLiteralExpr) {
            x = new SQLBinaryOpExpr(new SQLVariantRefExpr("?"), x.getOperator(), x.getRight());
            x.setParent(parent);
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
                x.setParent(parent);
            }

            break;
        }

        if (x.getLeft() instanceof SQLBinaryOpExpr) {
            x = new SQLBinaryOpExpr(merge((SQLBinaryOpExpr) x.getLeft()), x.getOperator(), x.getRight());
            x.setParent(parent);
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
