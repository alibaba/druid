package com.alibaba.druid.mock.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import com.alibaba.druid.common.jdbc.ResultSetMetaDataBase.ColumnMetaData;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockResultSetMetaData;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBooleanExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class MySqlMockExecuteHandlerImpl implements MockExecuteHandler {

    @Override
    public ResultSet executeQuery(MockStatement statement, String sql) throws SQLException {
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

    public ResultSet executeQuery(MockStatement statement, SQLSelectQueryBlock query) throws SQLException {
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

    public ResultSet showStatus(MockStatement statement) throws SQLException {
        MockResultSet rs = new MockResultSet(statement);
        MockResultSetMetaData metaData = rs.getMockMetaData();

        Object[] row = new Object[] { "on" };

        ColumnMetaData column = new ColumnMetaData();
        column.setColumnType(Types.NVARCHAR);
        metaData.getColumns().add(column);

        rs.getRows().add(row);

        return rs;
    }

    public ResultSet executeQueryFromDual(MockStatement statement, SQLSelectQueryBlock query) throws SQLException {
        MockResultSet rs = new MockResultSet(statement);
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
            } else if (expr instanceof MySqlBooleanExpr) {
                row[i] = ((MySqlBooleanExpr) expr).getValue();
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

            } else {
                throw new SQLException("TODO");
            }

            metaData.getColumns().add(column);
        }

        rs.getRows().add(row);

        return rs;
    }
}
