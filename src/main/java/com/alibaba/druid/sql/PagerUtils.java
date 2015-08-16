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
package com.alibaba.druid.sql;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.PGLimit;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

public class PagerUtils {

    public static String count(String sql, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLStatement stmt = stmtList.get(0);

        if (!(stmt instanceof SQLSelectStatement)) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        return count(selectStmt.getSelect(), dbType);
    }

    public static String limit(String sql, String dbType, int offset, int count) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLStatement stmt = stmtList.get(0);

        if (!(stmt instanceof SQLSelectStatement)) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        return limit(selectStmt.getSelect(), dbType, offset, count);
    }

    public static String limit(SQLSelect select, String dbType, int offset, int count) {
        SQLSelectQuery query = select.getQuery();

        if (JdbcConstants.ORACLE.equals(dbType)) {
            return limitOracle(select, dbType, offset, count);
        }

        if (JdbcConstants.DB2.equals(dbType)) {
            return limitDB2(select, dbType, offset, count);
        }

        if (JdbcConstants.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return limitSQLServer(select, dbType, offset, count);
        }

        if (query instanceof SQLSelectQueryBlock) {
            return limitQueryBlock(select, dbType, offset, count);
        }

        throw new UnsupportedOperationException();
    }

    private static String limitQueryBlock(SQLSelect select, String dbType, int offset, int count) {
        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) select.getQuery();
        if (JdbcConstants.MYSQL.equals(dbType) || //
            JdbcConstants.MARIADB.equals(dbType) || //
            JdbcConstants.H2.equals(dbType)) {
            return limitMySqlQueryBlock((MySqlSelectQueryBlock) queryBlock, dbType, offset, count);
        }

        if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            return limitPostgreSQLQueryBlock((PGSelectQueryBlock) queryBlock, dbType, offset, count);
        }
        throw new UnsupportedOperationException();
    }

    private static String limitPostgreSQLQueryBlock(PGSelectQueryBlock queryBlock, String dbType, int offset, int count) {
        if (queryBlock.getLimit() != null) {
            throw new IllegalArgumentException("limit already exists.");
        }

        PGLimit limit = new PGLimit();
        if (offset > 0) {
            limit.setOffset(new SQLIntegerExpr(offset));
        }
        limit.setRowCount(new SQLIntegerExpr(count));
        queryBlock.setLimit(limit);

        return SQLUtils.toSQLString(queryBlock, dbType);
    }

    private static String limitDB2(SQLSelect select, String dbType, int offset, int count) {
        SQLSelectQuery query = select.getQuery();

        SQLBinaryOpExpr gt = new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                                                 SQLBinaryOperator.GreaterThan, //
                                                 new SQLNumberExpr(offset), //
                                                 JdbcConstants.DB2);
        SQLBinaryOpExpr lteq = new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                                                   SQLBinaryOperator.LessThanOrEqual, //
                                                   new SQLNumberExpr(count + offset), //
                                                   JdbcConstants.DB2);
        SQLBinaryOpExpr pageCondition = new SQLBinaryOpExpr(gt, SQLBinaryOperator.BooleanAnd, lteq, JdbcConstants.DB2);

        if (query instanceof SQLSelectQueryBlock) {
            DB2SelectQueryBlock queryBlock = (DB2SelectQueryBlock) query;
            if (offset <= 0) {
                queryBlock.setFirst(new SQLNumberExpr(count));
                return SQLUtils.toSQLString(select, dbType);
            }

            SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
            SQLOrderBy orderBy = select.getOrderBy();
            aggregateExpr.setOver(new SQLOver(orderBy));
            select.setOrderBy(null);

            queryBlock.getSelectList().add(new SQLSelectItem(aggregateExpr, "ROWNUM"));

            DB2SelectQueryBlock countQueryBlock = new DB2SelectQueryBlock();
            countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));

            countQueryBlock.setFrom(new SQLSubqueryTableSource(select, "XX"));

            countQueryBlock.setWhere(pageCondition);

            return SQLUtils.toSQLString(countQueryBlock, dbType);
        }

        DB2SelectQueryBlock countQueryBlock = new DB2SelectQueryBlock();
        countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLPropertyExpr(new SQLIdentifierExpr("XX"), "*")));
        SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
        SQLOrderBy orderBy = select.getOrderBy();
        aggregateExpr.setOver(new SQLOver(orderBy));
        select.setOrderBy(null);
        countQueryBlock.getSelectList().add(new SQLSelectItem(aggregateExpr, "ROWNUM"));

        countQueryBlock.setFrom(new SQLSubqueryTableSource(select, "XX"));

        if (offset <= 0) {
            return SQLUtils.toSQLString(countQueryBlock, dbType);
        }

        DB2SelectQueryBlock offsetQueryBlock = new DB2SelectQueryBlock();
        offsetQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));
        offsetQueryBlock.setFrom(new SQLSubqueryTableSource(new SQLSelect(countQueryBlock), "XXX"));
        offsetQueryBlock.setWhere(pageCondition);

        return SQLUtils.toSQLString(offsetQueryBlock, dbType);
    }

    private static String limitSQLServer(SQLSelect select, String dbType, int offset, int count) {
        SQLSelectQuery query = select.getQuery();

        SQLBinaryOpExpr gt = new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                                                 SQLBinaryOperator.GreaterThan, //
                                                 new SQLNumberExpr(offset), //
                                                 JdbcConstants.SQL_SERVER);
        SQLBinaryOpExpr lteq = new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                                                   SQLBinaryOperator.LessThanOrEqual, //
                                                   new SQLNumberExpr(count + offset), //
                                                   JdbcConstants.SQL_SERVER);
        SQLBinaryOpExpr pageCondition = new SQLBinaryOpExpr(gt, SQLBinaryOperator.BooleanAnd, lteq,
                                                            JdbcConstants.SQL_SERVER);

        if (query instanceof SQLSelectQueryBlock) {
            SQLServerSelectQueryBlock queryBlock = (SQLServerSelectQueryBlock) query;
            if (offset <= 0) {
                queryBlock.setTop(new SQLServerTop(new SQLNumberExpr(count)));
                return SQLUtils.toSQLString(select, dbType);
            }

            SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
            SQLOrderBy orderBy = select.getOrderBy();
            aggregateExpr.setOver(new SQLOver(orderBy));
            select.setOrderBy(null);

            queryBlock.getSelectList().add(new SQLSelectItem(aggregateExpr, "ROWNUM"));

            SQLServerSelectQueryBlock countQueryBlock = new SQLServerSelectQueryBlock();
            countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));

            countQueryBlock.setFrom(new SQLSubqueryTableSource(select, "XX"));

            countQueryBlock.setWhere(pageCondition);

            return SQLUtils.toSQLString(countQueryBlock, dbType);
        }

        SQLServerSelectQueryBlock countQueryBlock = new SQLServerSelectQueryBlock();
        countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLPropertyExpr(new SQLIdentifierExpr("XX"), "*")));

        countQueryBlock.setFrom(new SQLSubqueryTableSource(select, "XX"));

        if (offset <= 0) {
            countQueryBlock.setTop(new SQLServerTop(new SQLNumberExpr(count)));
            return SQLUtils.toSQLString(countQueryBlock, dbType);
        }

        SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
        SQLOrderBy orderBy = select.getOrderBy();
        aggregateExpr.setOver(new SQLOver(orderBy));
        select.setOrderBy(null);
        countQueryBlock.getSelectList().add(new SQLSelectItem(aggregateExpr, "ROWNUM"));

        SQLServerSelectQueryBlock offsetQueryBlock = new SQLServerSelectQueryBlock();
        offsetQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));
        offsetQueryBlock.setFrom(new SQLSubqueryTableSource(new SQLSelect(countQueryBlock), "XXX"));
        offsetQueryBlock.setWhere(pageCondition);

        return SQLUtils.toSQLString(offsetQueryBlock, dbType);
    }

    private static String limitOracle(SQLSelect select, String dbType, int offset, int count) {
        SQLSelectQuery query = select.getQuery();

        if (query instanceof SQLSelectQueryBlock) {
            OracleSelectQueryBlock queryBlock = (OracleSelectQueryBlock) query;
            if (queryBlock.getGroupBy() == null && select.getOrderBy() == null && offset <= 0) {
                SQLExpr condition = new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                                                        SQLBinaryOperator.LessThanOrEqual, //
                                                        new SQLNumberExpr(count), //
                                                        JdbcConstants.ORACLE);
                if (queryBlock.getWhere() == null) {
                    queryBlock.setWhere(condition);
                } else {
                    queryBlock.setWhere(new SQLBinaryOpExpr(queryBlock.getWhere(), //
                                                            SQLBinaryOperator.BooleanAnd, //
                                                            condition, //
                                                            JdbcConstants.ORACLE));
                }

                return SQLUtils.toSQLString(select, dbType);
            }
        }

        OracleSelectQueryBlock countQueryBlock = new OracleSelectQueryBlock();
        countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLPropertyExpr(new SQLIdentifierExpr("XX"), "*")));
        countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLIdentifierExpr("ROWNUM"), "RN"));

        countQueryBlock.setFrom(new SQLSubqueryTableSource(select, "XX"));
        countQueryBlock.setWhere(new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                                                     SQLBinaryOperator.LessThanOrEqual, //
                                                     new SQLNumberExpr(count + offset), //
                                                     JdbcConstants.ORACLE));
        if (offset <= 0) {
            return SQLUtils.toSQLString(countQueryBlock, dbType);
        }

        OracleSelectQueryBlock offsetQueryBlock = new OracleSelectQueryBlock();
        offsetQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));
        offsetQueryBlock.setFrom(new SQLSubqueryTableSource(new SQLSelect(countQueryBlock), "XXX"));
        offsetQueryBlock.setWhere(new SQLBinaryOpExpr(new SQLIdentifierExpr("RN"), //
                                                      SQLBinaryOperator.GreaterThan, //
                                                      new SQLNumberExpr(offset), //
                                                      JdbcConstants.ORACLE));

        return SQLUtils.toSQLString(offsetQueryBlock, dbType);
    }

    private static String limitMySqlQueryBlock(MySqlSelectQueryBlock queryBlock, String dbType, int offset, int count) {
        if (queryBlock.getLimit() != null) {
            throw new IllegalArgumentException("limit already exists.");
        }

        Limit limit = new Limit();
        if (offset > 0) {
            limit.setOffset(new SQLNumberExpr(offset));
        }
        limit.setRowCount(new SQLNumberExpr(count));
        queryBlock.setLimit(limit);

        return SQLUtils.toSQLString(queryBlock, dbType);
    }

    private static String count(SQLSelect select, String dbType) {
        if (select.getOrderBy() != null) {
            select.setOrderBy(null);
        }

        SQLSelectQuery query = select.getQuery();
        clearOrderBy(query);

        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectItem countItem = createCountItem(dbType);

            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;

            if (queryBlock.getGroupBy() != null && queryBlock.getGroupBy().getItems().size() > 0) {
                return createCountUseSubQuery(select, dbType);
            }

            queryBlock.getSelectList().clear();
            queryBlock.getSelectList().add(countItem);
            return SQLUtils.toSQLString(select, dbType);
        } else if (query instanceof SQLUnionQuery) {
            return createCountUseSubQuery(select, dbType);
        }

        throw new IllegalStateException();
    }

    private static String createCountUseSubQuery(SQLSelect select, String dbType) {
        SQLSelectQueryBlock countSelectQuery = createQueryBlock(dbType);

        SQLSelectItem countItem = createCountItem(dbType);
        countSelectQuery.getSelectList().add(countItem);

        SQLSubqueryTableSource fromSubquery = new SQLSubqueryTableSource(select);
        fromSubquery.setAlias("ALIAS_COUNT");
        countSelectQuery.setFrom(fromSubquery);

        SQLSelect countSelect = new SQLSelect(countSelectQuery);
        SQLSelectStatement countStmt = new SQLSelectStatement(countSelect);

        return SQLUtils.toSQLString(countStmt, dbType);
    }

    private static SQLSelectQueryBlock createQueryBlock(String dbType) {
        if (JdbcConstants.MYSQL.equals(dbType)) {
            return new MySqlSelectQueryBlock();
        }

        if (JdbcConstants.MARIADB.equals(dbType)) {
            return new MySqlSelectQueryBlock();
        }

        if (JdbcConstants.H2.equals(dbType)) {
            return new MySqlSelectQueryBlock();
        }

        if (JdbcConstants.ORACLE.equals(dbType)) {
            return new OracleSelectQueryBlock();
        }

        if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            return new PGSelectQueryBlock();
        }

        if (JdbcConstants.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerSelectQueryBlock();
        }

        if (JdbcConstants.DB2.equals(dbType)) {
            return new DB2SelectQueryBlock();
        }

        return new SQLSelectQueryBlock();
    }

    private static SQLSelectItem createCountItem(String dbType) {
        SQLAggregateExpr countExpr = new SQLAggregateExpr("COUNT");

        countExpr.getArguments().add(new SQLAllColumnExpr());

        SQLSelectItem countItem = new SQLSelectItem(countExpr);
        return countItem;
    }

    private static void clearOrderBy(SQLSelectQuery query) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            if (queryBlock instanceof MySqlSelectQueryBlock) {
                MySqlSelectQueryBlock mysqlQueryBlock = (MySqlSelectQueryBlock) queryBlock;
                if (mysqlQueryBlock.getOrderBy() != null) {
                    mysqlQueryBlock.setOrderBy(null);
                }
            } else if (queryBlock instanceof PGSelectQueryBlock) {
                PGSelectQueryBlock pgQueryBlock = (PGSelectQueryBlock) queryBlock;
                if (pgQueryBlock.getOrderBy() != null) {
                    pgQueryBlock.setOrderBy(null);
                }
            }
            return;
        }

        if (query instanceof SQLUnionQuery) {
            SQLUnionQuery union = (SQLUnionQuery) query;
            if (union.getOrderBy() != null) {
                union.setOrderBy(null);
            }
            clearOrderBy(union.getLeft());
            clearOrderBy(union.getRight());
        }
    }
}
