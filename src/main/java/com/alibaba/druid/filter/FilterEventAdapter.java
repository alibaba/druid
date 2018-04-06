/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.filter;

import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public abstract class FilterEventAdapter extends FilterAdapter {

    public FilterEventAdapter(){
    }

    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        connection_connectBefore(chain, info);

        ConnectionProxy connection = super.connection_connect(chain, info);

        connection_connectAfter(connection);

        return connection;
    }

    public void connection_connectBefore(FilterChain chain, Properties info) {

    }

    public void connection_connectAfter(ConnectionProxy connection) {

    }

    @Override
    public StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection) throws SQLException {
        StatementProxy statement = super.connection_createStatement(chain, connection);

        statementCreateAfter(statement);

        return statement;
    }

    @Override
    public StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection, int resultSetType,
                                                     int resultSetConcurrency) throws SQLException {
        StatementProxy statement = super.connection_createStatement(chain, connection, resultSetType,
                                                                    resultSetConcurrency);

        statementCreateAfter(statement);

        return statement;
    }

    @Override
    public StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection, int resultSetType,
                                                     int resultSetConcurrency, int resultSetHoldability)
                                                                                                        throws SQLException {
        StatementProxy statement = super.connection_createStatement(chain, connection, resultSetType,
                                                                    resultSetConcurrency, resultSetHoldability);

        statementCreateAfter(statement);

        return statement;
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                   throws SQLException {
        CallableStatementProxy statement = super.connection_prepareCall(chain, connection, sql);

        statementPrepareCallAfter(statement);

        return statement;
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency)
                                                                                                     throws SQLException {
        CallableStatementProxy statement = super.connection_prepareCall(chain, connection, sql, resultSetType,
                                                                        resultSetConcurrency);

        statementPrepareCallAfter(statement);

        return statement;
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency,
                                                         int resultSetHoldability) throws SQLException {
        CallableStatementProxy statement = super.connection_prepareCall(chain, connection, sql, resultSetType,
                                                                        resultSetConcurrency, resultSetHoldability);

        statementPrepareCallAfter(statement);

        return statement;
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                        throws SQLException {
        PreparedStatementProxy statement = super.connection_prepareStatement(chain, connection, sql);

        statementPrepareAfter(statement);

        return statement;
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int autoGeneratedKeys) throws SQLException {
        PreparedStatementProxy statement = super.connection_prepareStatement(chain, connection, sql, autoGeneratedKeys);

        statementPrepareAfter(statement);

        return statement;
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency)
                                                                                                                      throws SQLException {
        PreparedStatementProxy statement = super.connection_prepareStatement(chain, connection, sql, resultSetType,
                                                                             resultSetConcurrency);

        statementPrepareAfter(statement);

        return statement;
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency,
                                                              int resultSetHoldability) throws SQLException {
        PreparedStatementProxy statement = super.connection_prepareStatement(chain, connection, sql, resultSetType,
                                                                             resultSetConcurrency, resultSetHoldability);

        statementPrepareAfter(statement);

        return statement;
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int[] columnIndexes) throws SQLException {
        PreparedStatementProxy statement = super.connection_prepareStatement(chain, connection, sql, columnIndexes);

        statementPrepareAfter(statement);

        return statement;
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, String[] columnNames) throws SQLException {
        PreparedStatementProxy statement = super.connection_prepareStatement(chain, connection, sql, columnNames);

        statementPrepareAfter(statement);

        return statement;
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        statementExecuteBefore(statement, sql);

        try {
            boolean firstResult = super.statement_execute(chain, statement, sql);

            statementExecuteAfter(statement, sql, firstResult);

            return firstResult;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                    throws SQLException {
        statementExecuteBefore(statement, sql);

        try {
            boolean firstResult = super.statement_execute(chain, statement, sql, autoGeneratedKeys);

            this.statementExecuteAfter(statement, sql, firstResult);

            return firstResult;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                  throws SQLException {
        statementExecuteBefore(statement, sql);

        try {
            boolean firstResult = super.statement_execute(chain, statement, sql, columnIndexes);

            this.statementExecuteAfter(statement, sql, firstResult);

            return firstResult;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                   throws SQLException {
        statementExecuteBefore(statement, sql);

        try {
            boolean firstResult = super.statement_execute(chain, statement, sql, columnNames);

            this.statementExecuteAfter(statement, sql, firstResult);

            return firstResult;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        }
    }

    @Override
    public int[] statement_executeBatch(FilterChain chain, StatementProxy statement) throws SQLException {
        statementExecuteBatchBefore(statement);

        try {
            int[] result = super.statement_executeBatch(chain, statement);

            statementExecuteBatchAfter(statement, result);

            return result;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, statement.getBatchSql(), error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, statement.getBatchSql(), error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, statement.getBatchSql(), error);
            throw error;
        }
    }

    @Override
    public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql)
                                                                                                         throws SQLException {
        statementExecuteQueryBefore(statement, sql);

        try {
            ResultSetProxy resultSet = super.statement_executeQuery(chain, statement, sql);

            if (resultSet != null) {
                statementExecuteQueryAfter(statement, sql, resultSet);
                resultSetOpenAfter(resultSet);
            }

            return resultSet;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        statementExecuteUpdateBefore(statement, sql);

        try {
            int updateCount = super.statement_executeUpdate(chain, statement, sql);

            statementExecuteUpdateAfter(statement, sql, updateCount);

            return updateCount;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                      throws SQLException {
        statementExecuteUpdateBefore(statement, sql);

        try {
            int updateCount = super.statement_executeUpdate(chain, statement, sql, autoGeneratedKeys);

            statementExecuteUpdateAfter(statement, sql, updateCount);

            return updateCount;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                    throws SQLException {
        statementExecuteUpdateBefore(statement, sql);

        try {
            int updateCount = super.statement_executeUpdate(chain, statement, sql, columnIndexes);

            statementExecuteUpdateAfter(statement, sql, updateCount);

            return updateCount;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                     throws SQLException {
        statementExecuteUpdateBefore(statement, sql);

        try {
            int updateCount = super.statement_executeUpdate(chain, statement, sql, columnNames);

            statementExecuteUpdateAfter(statement, sql, updateCount);

            return updateCount;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, sql, error);
            throw error;
        }
    }

    @Override
    public ResultSetProxy statement_getGeneratedKeys(FilterChain chain, StatementProxy statement) throws SQLException {
        ResultSetProxy resultSet = super.statement_getGeneratedKeys(chain, statement);

        if (resultSet != null) {
            resultSetOpenAfter(resultSet);
        }

        return resultSet;
    }

    @Override
    public ResultSetProxy statement_getResultSet(FilterChain chain, StatementProxy statement) throws SQLException {
        ResultSetProxy resultSet = super.statement_getResultSet(chain, statement);

        if (resultSet != null) {
            resultSetOpenAfter(resultSet);
        }

        return resultSet;
    }

    @Override
    public boolean preparedStatement_execute(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        try {
            statementExecuteBefore(statement, statement.getSql());

            boolean firstResult = chain.preparedStatement_execute(statement);

            this.statementExecuteAfter(statement, statement.getSql(), firstResult);

            return firstResult;

        } catch (SQLException error) {
            statement_executeErrorAfter(statement, statement.getSql(), error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, statement.getSql(), error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, statement.getSql(), error);
            throw error;
        }

    }

    @Override
    public ResultSetProxy preparedStatement_executeQuery(FilterChain chain, PreparedStatementProxy statement)
                                                                                                             throws SQLException {
        try {
            statementExecuteQueryBefore(statement, statement.getSql());

            ResultSetProxy resultSet = chain.preparedStatement_executeQuery(statement);

            if (resultSet != null) {
                statementExecuteQueryAfter(statement, statement.getSql(), resultSet);

                resultSetOpenAfter(resultSet);
            }

            return resultSet;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, statement.getSql(), error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, statement.getSql(), error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, statement.getSql(), error);
            throw error;
        }
    }

    @Override
    public int preparedStatement_executeUpdate(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        try {
            statementExecuteUpdateBefore(statement, statement.getSql());

            int updateCount = super.preparedStatement_executeUpdate(chain, statement);

            statementExecuteUpdateAfter(statement, statement.getSql(), updateCount);

            return updateCount;
        } catch (SQLException error) {
            statement_executeErrorAfter(statement, statement.getSql(), error);
            throw error;
        } catch (RuntimeException error) {
            statement_executeErrorAfter(statement, statement.getSql(), error);
            throw error;
        } catch (Error error) {
            statement_executeErrorAfter(statement, statement.getSql(), error);
            throw error;
        }
    }

    protected void statementCreateAfter(StatementProxy statement) {

    }

    protected void statementPrepareAfter(PreparedStatementProxy statement) {

    }

    protected void statementPrepareCallAfter(CallableStatementProxy statement) {

    }

    protected void resultSetOpenAfter(ResultSetProxy resultSet) {

    }

    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {

    }

    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {

    }

    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {

    }

    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {

    }

    protected void statementExecuteBefore(StatementProxy statement, String sql) {

    }

    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {

    }

    protected void statementExecuteBatchBefore(StatementProxy statement) {

    }

    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {

    }

    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {

    }
}
