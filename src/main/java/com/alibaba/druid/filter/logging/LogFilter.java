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
package com.alibaba.druid.filter.logging;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.SQLUtils.FormatOption;
import com.alibaba.druid.util.JdbcUtils;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public abstract class LogFilter extends FilterEventAdapter implements LogFilterMBean {
    protected String          dataSourceLoggerName                 = "druid.sql.DataSource";
    protected String          connectionLoggerName                 = "druid.sql.Connection";
    protected String          statementLoggerName                  = "druid.sql.Statement";
    protected String          resultSetLoggerName                  = "druid.sql.ResultSet";

    private boolean           connectionConnectBeforeLogEnable     = true;
    private boolean           connectionConnectAfterLogEnable      = true;
    private boolean           connectionCommitAfterLogEnable       = true;
    private boolean           connectionRollbackAfterLogEnable     = true;
    private boolean           connectionCloseAfterLogEnable        = true;

    private boolean           statementCreateAfterLogEnable        = true;
    private boolean           statementPrepareAfterLogEnable       = true;
    private boolean           statementPrepareCallAfterLogEnable   = true;

    private boolean           statementExecuteAfterLogEnable       = true;
    private boolean           statementExecuteQueryAfterLogEnable  = true;
    private boolean           statementExecuteUpdateAfterLogEnable = true;
    private boolean           statementExecuteBatchAfterLogEnable  = true;
    private boolean           statementExecutableSqlLogEnable      = false;

    private boolean           statementCloseAfterLogEnable         = true;

    private boolean           statementParameterClearLogEnable     = true;
    private boolean           statementParameterSetLogEnable       = true;

    private boolean           resultSetNextAfterLogEnable          = true;
    private boolean           resultSetOpenAfterLogEnable          = true;
    private boolean           resultSetCloseAfterLogEnable         = true;

    private boolean           dataSourceLogEnabled                 = true;
    private boolean           connectionLogEnabled                 = true;
    private boolean           connectionLogErrorEnabled            = true;
    private boolean           statementLogEnabled                  = true;
    private boolean           statementLogErrorEnabled             = true;
    private boolean           resultSetLogEnabled                  = true;
    private boolean           resultSetLogErrorEnabled             = true;

    private FormatOption      statementSqlFormatOption             = new FormatOption(false, true);
    private boolean           statementLogSqlPrettyFormat          = false;

    protected DataSourceProxy dataSource;

    public LogFilter(){
        configFromProperties(System.getProperties());
    }

    public void configFromProperties(Properties properties) {
        {
            String prop = properties.getProperty("druid.log.conn");
            if ("false".equals(prop)) {
                connectionLogEnabled = false;
            } else if ("true".equals(prop)) {
                connectionLogEnabled = true;
            }
        }
        {
            String prop = properties.getProperty("druid.log.stmt");
            if ("false".equals(prop)) {
                statementLogEnabled = false;
            } else if ("true".equals(prop)) {
                statementLogEnabled = true;
            }
        }
        {
            String prop = properties.getProperty("druid.log.rs");
            if ("false".equals(prop)) {
                resultSetLogEnabled = false;
            } else if ("true".equals(prop)) {
                resultSetLogEnabled = true;
            }
        }
        {
            String prop = properties.getProperty("druid.log.stmt.executableSql");
            if ("true".equals(prop)) {
                statementExecutableSqlLogEnable = true;
            } else if ("false".equals(prop)) {
                statementExecutableSqlLogEnable = false;
            }
        }
    }

    @Override
    public void init(DataSourceProxy dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isConnectionLogErrorEnabled() {
        return connectionLogErrorEnabled;
    }

    public boolean isResultSetCloseAfterLogEnabled() {
        return isResultSetLogEnabled() && resultSetCloseAfterLogEnable;
    }

    public void setResultSetCloseAfterLogEnabled(boolean resultSetCloseAfterLogEnable) {
        this.resultSetCloseAfterLogEnable = resultSetCloseAfterLogEnable;
    }

    public void setConnectionLogErrorEnabled(boolean connectionLogErrorEnabled) {
        this.connectionLogErrorEnabled = connectionLogErrorEnabled;
    }

    public boolean isResultSetLogErrorEnabled() {
        return resultSetLogErrorEnabled;
    }

    public void setResultSetLogErrorEnabled(boolean resultSetLogErrorEnabled) {
        this.resultSetLogErrorEnabled = resultSetLogErrorEnabled;
    }

    public boolean isConnectionConnectBeforeLogEnabled() {
        return isConnectionLogEnabled() && connectionConnectBeforeLogEnable;
    }

    public void setConnectionConnectBeforeLogEnabled(boolean beforeConnectionConnectLogEnable) {
        this.connectionConnectBeforeLogEnable = beforeConnectionConnectLogEnable;
    }

    public boolean isConnectionCloseAfterLogEnabled() {
        return isConnectionLogEnabled() && connectionCloseAfterLogEnable;
    }

    public boolean isConnectionRollbackAfterLogEnabled() {
        return isConnectionLogEnabled() && connectionRollbackAfterLogEnable;
    }

    public void setConnectionRollbackAfterLogEnabled(boolean connectionRollbackAfterLogEnable) {
        this.connectionRollbackAfterLogEnable = connectionRollbackAfterLogEnable;
    }

    public void setConnectionCloseAfterLogEnabled(boolean afterConnectionCloseLogEnable) {
        this.connectionCloseAfterLogEnable = afterConnectionCloseLogEnable;
    }

    public boolean isConnectionCommitAfterLogEnabled() {
        return isConnectionLogEnabled() && connectionCommitAfterLogEnable;
    }

    public void setConnectionCommitAfterLogEnabled(boolean afterConnectionCommitLogEnable) {
        this.connectionCommitAfterLogEnable = afterConnectionCommitLogEnable;
    }

    public boolean isConnectionConnectAfterLogEnabled() {
        return isConnectionLogEnabled() && connectionConnectAfterLogEnable;
    }

    public void setConnectionConnectAfterLogEnabled(boolean afterConnectionConnectLogEnable) {
        this.connectionConnectAfterLogEnable = afterConnectionConnectLogEnable;
    }

    public boolean isResultSetNextAfterLogEnabled() {
        return isResultSetLogEnabled() && resultSetNextAfterLogEnable;
    }

    public void setResultSetNextAfterLogEnabled(boolean afterResultSetNextLogEnable) {
        this.resultSetNextAfterLogEnable = afterResultSetNextLogEnable;
    }

    public boolean isResultSetOpenAfterLogEnabled() {
        return isResultSetLogEnabled() && resultSetOpenAfterLogEnable;
    }

    public void setResultSetOpenAfterLogEnabled(boolean afterResultSetOpenLogEnable) {
        this.resultSetOpenAfterLogEnable = afterResultSetOpenLogEnable;
    }

    public boolean isStatementCloseAfterLogEnabled() {
        return isStatementLogEnabled() && statementCloseAfterLogEnable;
    }

    public void setStatementCloseAfterLogEnabled(boolean afterStatementCloseLogEnable) {
        this.statementCloseAfterLogEnable = afterStatementCloseLogEnable;
    }

    public boolean isStatementCreateAfterLogEnabled() {
        return isStatementLogEnabled() && statementCreateAfterLogEnable;
    }

    public void setStatementCreateAfterLogEnabled(boolean afterStatementCreateLogEnable) {
        this.statementCreateAfterLogEnable = afterStatementCreateLogEnable;
    }

    public boolean isStatementExecuteBatchAfterLogEnabled() {
        return isStatementLogEnabled() && statementExecuteBatchAfterLogEnable;
    }

    public void setStatementExecuteBatchAfterLogEnabled(boolean afterStatementExecuteBatchLogEnable) {
        this.statementExecuteBatchAfterLogEnable = afterStatementExecuteBatchLogEnable;
    }

    public boolean isStatementExecuteAfterLogEnabled() {
        return isStatementLogEnabled() && statementExecuteAfterLogEnable;
    }

    public void setStatementExecuteAfterLogEnabled(boolean afterStatementExecuteLogEnable) {
        this.statementExecuteAfterLogEnable = afterStatementExecuteLogEnable;
    }

    public boolean isStatementExecuteQueryAfterLogEnabled() {
        return isStatementLogEnabled() && statementExecuteQueryAfterLogEnable;
    }

    public void setStatementExecuteQueryAfterLogEnabled(boolean afterStatementExecuteQueryLogEnable) {
        this.statementExecuteQueryAfterLogEnable = afterStatementExecuteQueryLogEnable;
    }

    public boolean isStatementExecuteUpdateAfterLogEnabled() {
        return isStatementLogEnabled() && statementExecuteUpdateAfterLogEnable;
    }

    public void setStatementExecuteUpdateAfterLogEnabled(boolean afterStatementExecuteUpdateLogEnable) {
        this.statementExecuteUpdateAfterLogEnable = afterStatementExecuteUpdateLogEnable;
    }

    public boolean isStatementExecutableSqlLogEnable() {
        return statementExecutableSqlLogEnable;
    }

    public void setStatementExecutableSqlLogEnable(boolean statementExecutableSqlLogEnable) {
        this.statementExecutableSqlLogEnable = statementExecutableSqlLogEnable;
    }

    public boolean isStatementPrepareCallAfterLogEnabled() {
        return isStatementLogEnabled() && statementPrepareCallAfterLogEnable;
    }

    public void setStatementPrepareCallAfterLogEnabled(boolean afterStatementPrepareCallLogEnable) {
        this.statementPrepareCallAfterLogEnable = afterStatementPrepareCallLogEnable;
    }

    public boolean isStatementPrepareAfterLogEnabled() {
        return isStatementLogEnabled() && statementPrepareAfterLogEnable;
    }

    public void setStatementPrepareAfterLogEnabled(boolean afterStatementPrepareLogEnable) {
        this.statementPrepareAfterLogEnable = afterStatementPrepareLogEnable;
    }

    public boolean isDataSourceLogEnabled() {
        return dataSourceLogEnabled;
    }

    public void setDataSourceLogEnabled(boolean dataSourceLogEnabled) {
        this.dataSourceLogEnabled = dataSourceLogEnabled;
    }

    public boolean isConnectionLogEnabled() {
        return connectionLogEnabled;
    }

    public void setConnectionLogEnabled(boolean connectionLogEnabled) {
        this.connectionLogEnabled = connectionLogEnabled;
    }

    public boolean isStatementLogEnabled() {
        return statementLogEnabled;
    }

    public void setStatementLogEnabled(boolean statementLogEnabled) {
        this.statementLogEnabled = statementLogEnabled;
    }

    public boolean isStatementLogErrorEnabled() {
        return statementLogErrorEnabled;
    }

    public void setStatementLogErrorEnabled(boolean statementLogErrorEnabled) {
        this.statementLogErrorEnabled = statementLogErrorEnabled;
    }

    public boolean isResultSetLogEnabled() {
        return resultSetLogEnabled;
    }

    public void setResultSetLogEnabled(boolean resultSetLogEnabled) {
        this.resultSetLogEnabled = resultSetLogEnabled;
    }

    public boolean isStatementParameterSetLogEnabled() {
        return isStatementLogEnabled() && statementParameterSetLogEnable;
    }

    public void setStatementParameterSetLogEnabled(boolean statementParameterSetLogEnable) {
        this.statementParameterSetLogEnable = statementParameterSetLogEnable;
    }

    public boolean isStatementParameterClearLogEnable() {
        return isStatementLogEnabled() && statementParameterClearLogEnable;
    }

    public void setStatementParameterClearLogEnable(boolean statementParameterClearLogEnable) {
        this.statementParameterClearLogEnable = statementParameterClearLogEnable;
    }

    public FormatOption getStatementSqlFormatOption() {
        return this.statementSqlFormatOption;
    }

    public void setStatementSqlFormatOption(FormatOption formatOption) {
        this.statementSqlFormatOption = formatOption;
    }

    public boolean isStatementSqlPrettyFormat() {
        return this.statementLogSqlPrettyFormat;
    }

    public void setStatementSqlPrettyFormat(boolean statementSqlPrettyFormat) {
        this.statementLogSqlPrettyFormat = statementSqlPrettyFormat;
    }

    protected abstract void connectionLog(String message);

    protected abstract void statementLog(String message);

    protected abstract void statementLogError(String message, Throwable error);

    protected abstract void resultSetLog(String message);

    protected abstract void resultSetLogError(String message, Throwable error);

    public void connection_connectAfter(ConnectionProxy connection) {
        if (connection == null) {
            return;
        }
        
        if (connectionConnectAfterLogEnable && isConnectionLogEnabled()) {
            connectionLog("{conn-" + connection.getId() + "} connected");
        }
    }

    @Override
    public Savepoint connection_setSavepoint(FilterChain chain, ConnectionProxy connection) throws SQLException {

        Savepoint savepoint = chain.connection_setSavepoint(connection);

        if (isConnectionLogEnabled()) {
            connectionLog("{conn " + connection.getId() + "} setSavepoint-" + savepointToString(savepoint));
        }

        return savepoint;
    }

    @Override
    public Savepoint connection_setSavepoint(FilterChain chain, ConnectionProxy connection, String name)
                                                                                                        throws SQLException {
        Savepoint savepoint = chain.connection_setSavepoint(connection, name);

        if (isConnectionLogEnabled()) {
            connectionLog("{conn " + connection.getId() + "} setSavepoint-" + name);
        }

        return savepoint;
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_rollback(chain, connection);

        if (connectionRollbackAfterLogEnable && isConnectionLogEnabled()) {
            connectionLog("{conn " + connection.getId() + "} rollback");
        }
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection, Savepoint savePoint)
                                                                                                       throws SQLException {
        super.connection_rollback(chain, connection, savePoint);

        if (connectionRollbackAfterLogEnable && isConnectionLogEnabled()) {
            connectionLog("{conn " + connection.getId() + "} rollback -> " + savepointToString(savePoint));
        }
    }

    @Override
    public void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_commit(chain, connection);

        if (connectionCommitAfterLogEnable && isConnectionLogEnabled()) {
            connectionLog("{conn-" + connection.getId() + "} commited");
        }
    }

    @Override
    public void connection_setAutoCommit(FilterChain chain, ConnectionProxy connection, boolean autoCommit)
                                                                                                           throws SQLException {

        connectionLog("{conn-" + connection.getId() + "} setAutoCommit " + autoCommit);
        chain.connection_setAutoCommit(connection, autoCommit);
    }

    @Override
    public void connection_close(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_close(chain, connection);

        if (connectionCloseAfterLogEnable && isConnectionLogEnabled()) {
            connectionLog("{conn-" + connection.getId() + "} closed");
        }
    }

    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        super.statement_close(chain, statement);

        if (statementCloseAfterLogEnable && isStatementLogEnabled()) {
            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + "} closed");
        }
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        statement.setLastExecuteStartNano();
        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        logExecutableSql(statement, sql);

        if (statementExecuteAfterLogEnable && isStatementLogEnabled()) {
            statement.setLastExecuteTimeNano();
            double nanos = statement.getLastExecuteTimeNano();
            double millis = nanos / (1000 * 1000);

            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + "} executed. "
                         + millis + " millis. " + sql);
        }
    }

    @Override
    protected void statementExecuteBatchBefore(StatementProxy statement) {
        statement.setLastExecuteStartNano();
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        String sql;
        if (statement instanceof PreparedStatementProxy) {
            sql = ((PreparedStatementProxy) statement).getSql();
        } else {
            sql = statement.getBatchSql();
        }

        logExecutableSql(statement, sql);

        if (statementExecuteBatchAfterLogEnable && isStatementLogEnabled()) {
            statement.setLastExecuteTimeNano();
            double nanos = statement.getLastExecuteTimeNano();
            double millis = nanos / (1000 * 1000);

            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
                         + "} batch executed. " + millis + " millis. " + sql);
        }
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        statement.setLastExecuteStartNano();
        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        logExecutableSql(statement, sql);

        if (statementExecuteQueryAfterLogEnable && isStatementLogEnabled()) {
            statement.setLastExecuteTimeNano();
            double nanos = statement.getLastExecuteTimeNano();
            double millis = nanos / (1000 * 1000);

            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + ", rs-"
                         + resultSet.getId() + "} query executed. " + millis + " millis. " + sql);
        }
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        statement.setLastExecuteStartNano();
        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        logExecutableSql(statement, sql);

        if (statementExecuteUpdateAfterLogEnable && isStatementLogEnabled()) {
            statement.setLastExecuteTimeNano();
            double nanos = statement.getLastExecuteTimeNano();
            double millis = nanos / (1000 * 1000);

            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
                         + "} update executed. effort " + updateCount + ". " + millis + " millis. " + sql);
        }
    }

    private void logExecutableSql(StatementProxy statement, String sql) {
        if (!isStatementExecutableSqlLogEnable()) {
            return;
        }

        int parametersSize = statement.getParametersSize();
        if (parametersSize == 0) {
            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + "} executed. "
                         + sql);
            return;
        }

        List<Object> parameters = new ArrayList<Object>(parametersSize);
        for (int i = 0; i < parametersSize; ++i) {
            JdbcParameter jdbcParam = statement.getParameter(i);
            parameters.add(jdbcParam != null
                    ? jdbcParam.getValue()
                    : null);
        }

        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        String formattedSql = SQLUtils.format(sql, dbType, parameters, this.statementSqlFormatOption);
        statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + "} executed. "
                     + formattedSql);
    }

    @Override
    public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_close(resultSet);

        StringBuffer buf = new StringBuffer();
        buf.append("{conn-");
        buf.append(resultSet.getStatementProxy().getConnectionProxy().getId());
        buf.append(", ");
        buf.append(stmtId(resultSet));
        buf.append(", rs-");
        buf.append(resultSet.getId());
        buf.append("} closed");

        if (isResultSetCloseAfterLogEnabled()) {
            resultSetLog(buf.toString());
        }
    }

    @Override
    public boolean resultSet_next(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        boolean moreRows = super.resultSet_next(chain, resultSet);

        if (moreRows) {
            if (resultSetNextAfterLogEnable && isResultSetLogEnabled()) {
                try {
                    StringBuffer buf = new StringBuffer();
                    buf.append("{conn-");
                    buf.append(resultSet.getStatementProxy().getConnectionProxy().getId());
                    buf.append(", ");
                    buf.append(stmtId(resultSet));
                    buf.append(", rs-");
                    buf.append(resultSet.getId());
                    buf.append("}");
                    buf.append(" Result: [");

                    ResultSetMetaData meta = resultSet.getMetaData();
                    for (int i = 0, size = meta.getColumnCount(); i < size; ++i) {
                        if (i != 0) {
                            buf.append(", ");
                        }
                        int columnIndex = i + 1;
                        int type = meta.getColumnType(columnIndex);

                        Object value;
                        if (type == Types.TIMESTAMP) {
                            value = resultSet.getTimestamp(columnIndex);
                        } else if (type == Types.BLOB) {
                            value = "<BLOB>";
                        } else if (type == Types.CLOB) {
                            value = "<CLOB>";
                        } else if (type == Types.NCLOB) {
                            value = "<NCLOB>";
                        } else if (type == Types.BINARY) {
                            value = "<BINARY>";
                        } else {
                            value = resultSet.getObject(columnIndex);
                        }
                        buf.append(value);
                    }

                    buf.append("]");

                    resultSetLog(buf.toString());
                } catch (SQLException ex) {
                    resultSetLogError("logging error", ex);
                }
            }
        }

        return moreRows;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                      throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterIndex);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
                                              java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterIndex, map);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                        throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement,
                                              String parameterName, java.util.Map<String, Class<?>> map)
                                                                                                        throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName, map);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    @Override
    protected void resultSetOpenAfter(ResultSetProxy resultSet) {
        if (resultSetOpenAfterLogEnable && isResultSetLogEnabled()) {
            try {
                StringBuffer buf = new StringBuffer();
                buf.append("{conn-");
                buf.append(resultSet.getStatementProxy().getConnectionProxy().getId());
                buf.append(", ");
                buf.append(stmtId(resultSet));
                buf.append(", rs-");
                buf.append(resultSet.getId());
                buf.append("}");

                String resultId = buf.toString();
                resultSetLog(resultId + " open");

                buf.append(" Header: [");

                ResultSetMetaData meta = resultSet.getMetaData();
                for (int i = 0, size = meta.getColumnCount(); i < size; ++i) {
                    if (i != 0) {
                        buf.append(", ");
                    }
                    buf.append(meta.getColumnName(i + 1));
                }
                buf.append("]");

                resultSetLog(buf.toString());
            } catch (SQLException ex) {
                resultSetLogError("logging error", ex);
            }
        }
    }

    protected void statementCreateAfter(StatementProxy statement) {
        if (statementCreateAfterLogEnable && isStatementLogEnabled()) {
            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", stmt-" + statement.getId()
                         + "} created");
        }
    }

    protected void statementPrepareAfter(PreparedStatementProxy statement) {
        if (statementPrepareAfterLogEnable && isStatementLogEnabled()) {
            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", pstmt-" + statement.getId()
                         + "} created. " + statement.getSql());
        }
    }

    protected void statementPrepareCallAfter(CallableStatementProxy statement) {
        if (statementPrepareCallAfterLogEnable && isStatementLogEnabled()) {
            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", cstmt-" + statement.getId()
                         + "} created. " + statement.getSql());
        }
    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        if (this.isStatementLogErrorEnabled()) {
	    if (!isStatementExecutableSqlLogEnable()) {
        		statementLogError("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
                              + "} execute error. " + sql, error);
            }else{
            	int parametersSize = statement.getParametersSize();
            	if (parametersSize > 0) {
            		List<Object> parameters = new ArrayList<Object>(parametersSize);
            		for (int i = 0; i < parametersSize; ++i) {
            			JdbcParameter jdbcParam = statement.getParameter(i);
            			parameters.add(jdbcParam != null
            					? jdbcParam.getValue()
            							: null);
            		}
            		String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
            		String formattedSql = SQLUtils.format(sql, dbType, parameters, this.statementSqlFormatOption);
			        statementLogError("{conn-" + statement.getConnectionProxy().getId()
                                + ", " + stmtId(statement)
                                + "} execute error. " + formattedSql
                            , error);
            	} else{
            		statementLogError("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
                              + "} execute error. " + sql, error);
            	}
            }
        }
    }

    private String stmtId(ResultSetProxy resultSet) {
        return stmtId(resultSet.getStatementProxy());
    }

    private String stmtId(StatementProxy statement) {
        StringBuffer buf = new StringBuffer();
        if (statement instanceof CallableStatementProxy) {
            buf.append("cstmt-");
        } else if (statement instanceof PreparedStatementProxy) {
            buf.append("pstmt-");
        } else {
            buf.append("stmt-");
        }
        buf.append(statement.getId());

        return buf.toString();
    }

    protected void logParameter(PreparedStatementProxy statement) {
        if (isStatementParameterSetLogEnabled()) {
            {
                StringBuffer buf = new StringBuffer();
                buf.append("{conn-");
                buf.append(statement.getConnectionProxy().getId());
                buf.append(", ");
                buf.append(stmtId(statement));
                buf.append("}");
                buf.append(" Parameters : [");

                for (int i = 0, parametersSize = statement.getParametersSize(); i < parametersSize; ++i) {
                    JdbcParameter parameter = statement.getParameter(i);
                    if (i != 0) {
                        buf.append(", ");
                    }
                    if (parameter == null) {
                        continue;
                    }

                    int sqlType = parameter.getSqlType();
                    Object value = parameter.getValue();
                    switch (sqlType) {
                        case Types.NULL:
                            buf.append("NULL");
                            break;
                        default:
                            buf.append(String.valueOf(value));
                            break;
                    }
                }
                buf.append("]");
                statementLog(buf.toString());
            }
            {
                StringBuffer buf = new StringBuffer();
                buf.append("{conn-");
                buf.append(statement.getConnectionProxy().getId());
                buf.append(", ");
                buf.append(stmtId(statement));
                buf.append("}");
                buf.append(" Types : [");
                for (int i = 0, parametersSize = statement.getParametersSize(); i < parametersSize; ++i) {
                    JdbcParameter parameter = statement.getParameter(i);
                    if (i != 0) {
                        buf.append(", ");
                    }
                    if (parameter == null) {
                        continue;
                    }
                    int sqlType = parameter.getSqlType();
                    buf.append(JdbcUtils.getTypeName(sqlType));
                }
                buf.append("]");
                statementLog(buf.toString());
            }
        }
    }

    @Override
    public void dataSource_releaseConnection(FilterChain chain, DruidPooledConnection conn) throws SQLException {
        long connectionId = -1;

        if (conn.getConnectionHolder() != null) {
            ConnectionProxy connection = (ConnectionProxy) conn.getConnectionHolder().getConnection();
            connectionId = connection.getId();
        }

        chain.dataSource_recycle(conn);

        if (connectionCloseAfterLogEnable && isConnectionLogEnabled()) {
            connectionLog("{conn-" + connectionId + "} pool-recycle");
        }
    }

    @Override
    public DruidPooledConnection dataSource_getConnection(FilterChain chain, DruidDataSource dataSource,
                                                          long maxWaitMillis) throws SQLException {
        DruidPooledConnection conn = chain.dataSource_connect(dataSource, maxWaitMillis);

        ConnectionProxy connection = (ConnectionProxy) conn.getConnectionHolder().getConnection();

        if (connectionConnectAfterLogEnable && isConnectionLogEnabled()) {
            connectionLog("{conn-" + connection.getId() + "} pool-connect");
        }

        return conn;
    }

    @Override
    public void preparedStatement_clearParameters(FilterChain chain, PreparedStatementProxy statement)
                                                                                                      throws SQLException {

        if (isStatementParameterClearLogEnable()) {
            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", pstmt-" + statement.getId()
                         + "} clearParameters. ");
        }
        chain.preparedStatement_clearParameters(statement);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface == this.getClass() || iface == LogFilter.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) {
        if (iface == this.getClass() || iface == LogFilter.class) {
            return (T) this;
        }
        return null;
    }
    
    protected String savepointToString(Savepoint savePoint) {
    	String savePointString = null;
    	try{
    		savePointString = savePoint.getSavepointName();
    	} catch (SQLException e) {
    		try {
				savePointString = String.valueOf(savePoint.getSavepointId());
			} catch (SQLException e1) {
				savePointString = savePoint.toString();
			}
    	}
    	return savePointString;
    }
}
