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
package com.alibaba.druid.mock.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockResultSetMetaData;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.jdbc.ResultSetMetaDataBase.ColumnMetaData;

public class MySqlMockExecuteHandlerImpl implements MockExecuteHandler {

    @Override
    public ResultSet executeQuery(MockStatementBase statement, String sql) throws SQLException {
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList(); //

        if (stmtList.size() > 1) {
            throw new SQLException("not support multi-statment. " + sql);
        }

        if (stmtList.size() == 0) {
            throw new SQLException("executeQueryError : " + sql);
        }

        SQLStatement stmt = stmtList.get(0);

        if (stmt instanceof CobarShowStatus) {
            return showStatus(statement);
        }

        if (!(stmt instanceof SQLSelectStatement)) {
            throw new SQLException("executeQueryError : " + sql);
        }

        SQLSelect select = ((SQLSelectStatement) stmt).getSelect();
        SQLSelectQuery query = select.getQuery();

        if (query instanceof SQLSelectQueryBlock) {
            return executeQuery(statement, (SQLSelectQueryBlock) query);
        }

        throw new SQLException("TODO");
    }

    public ResultSet executeQuery(MockStatementBase statement, SQLSelectQueryBlock query) throws SQLException {
        SQLTableSource from = query.getFrom();

        if (from instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) from).getExpr();

            if (expr instanceof SQLIdentifierExpr) {
                String ident = ((SQLIdentifierExpr) expr).getName();
                if ("dual".equalsIgnoreCase(ident)) {
                    return executeQueryFromDual(statement, query);
                }
            }
            throw new SQLException("TODO : " + query);
        } else if (from == null) {
            return executeQueryFromDual(statement, query);
        } else {
            throw new SQLException("TODO");
        }
    }

    public ResultSet showStatus(MockStatementBase statement) throws SQLException {
        MockResultSet rs = new MockResultSet(statement);
        MockResultSetMetaData metaData = rs.getMockMetaData();

        Object[] row = new Object[] { "on" };

        ColumnMetaData column = new ColumnMetaData();
        column.setColumnType(Types.NVARCHAR);
        metaData.getColumns().add(column);

        rs.getRows().add(row);

        return rs;
    }

    public ResultSet executeQueryFromDual(MockStatementBase statement, SQLSelectQueryBlock query) throws SQLException {
        MockResultSet rs = statement.getConnection().getDriver().createMockResultSet(statement);
        MockResultSetMetaData metaData = rs.getMockMetaData();

        Object[] row = new Object[query.getSelectList().size()];

        for (int i = 0, size = query.getSelectList().size(); i < size; ++i) {
            ColumnMetaData column = new ColumnMetaData();
            SQLSelectItem item = query.getSelectList().get(i);
            SQLExpr expr = item.getExpr();

            if (expr instanceof SQLIntegerExpr) {
                row[i] = ((SQLNumericLiteralExpr) expr).getNumber();
                column.setColumnType(Types.INTEGER);
            } else if (expr instanceof SQLNumberExpr) {
                row[i] = ((SQLNumericLiteralExpr) expr).getNumber();
                column.setColumnType(Types.DECIMAL);
            } else if (expr instanceof SQLCharExpr) {
                row[i] = ((SQLCharExpr) expr).getText();
                column.setColumnType(Types.VARCHAR);
            } else if (expr instanceof SQLNCharExpr) {
                row[i] = ((SQLNCharExpr) expr).getText();
                column.setColumnType(Types.NVARCHAR);
            } else if (expr instanceof SQLBooleanExpr) {
                row[i] = ((SQLBooleanExpr) expr).getValue();
                column.setColumnType(Types.NVARCHAR);
            } else if (expr instanceof SQLNullExpr) {
                row[i] = null;
            } else if (expr instanceof SQLMethodInvokeExpr) {
                SQLMethodInvokeExpr methodInvokeExpr = (SQLMethodInvokeExpr) expr;

                if ("NOW".equalsIgnoreCase(methodInvokeExpr.getMethodName())) {
                    row[i] = new Timestamp(System.currentTimeMillis());
                } else {
                    throw new SQLException("TODO");
                }
            } else if (expr instanceof SQLVariantRefExpr) {
                SQLVariantRefExpr varExpr = (SQLVariantRefExpr) expr;
                int varIndex = varExpr.getIndex();
                if (statement instanceof MockPreparedStatement) {
                    MockPreparedStatement mockPstmt = (MockPreparedStatement) statement;
                    row[i] = mockPstmt.getParameters().get(varIndex);
                } else {
                    row[i] = null;
                }
            } else {
                row[i] = null;
            }

            metaData.getColumns().add(column);
        }

        rs.getRows().add(row);

        return rs;
    }
}
