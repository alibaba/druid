/*
 * Copyright 2011 Alibaba Group. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.alibaba.druid.filter.logging;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public abstract class LogFilter extends FilterEventAdapter implements LogFilterMBean {

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

    private boolean           statementCloseAfterLogEnable         = true;

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

    protected final String    attributeNameParameter               = "log.parameter";
    protected final String    attributeNameLastSql                 = "log.lastSql";

    protected DataSourceProxy dataSource;

    public LogFilter(){
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

    protected abstract void connectionLog(String message);

    protected abstract void statementLog(String message);

    protected abstract void statementLogError(String message, Throwable error);

    protected abstract void resultSetLog(String message);

    protected abstract void resultSetLogError(String message, Throwable error);

    public void connection_connectAfter(ConnectionProxy connection) {
        if (connectionConnectAfterLogEnable && isConnectionLogEnabled()) {
            connectionLog("{conn-" + connection.getId() + "} connected");
        }
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_rollback(chain, connection);

        if (connectionRollbackAfterLogEnable && isConnectionLogEnabled()) {
            connectionLog("connect rollback. id " + connection.getId());
        }
    }

    @Override
    public void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_commit(chain, connection);

        if (connectionCommitAfterLogEnable && isConnectionLogEnabled()) {
            connectionLog("connect commited. id " + connection.getId());
        }
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
        setLastSql(statement, sql);

        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    private void setLastSql(StatementProxy statement, String sql) {
        statement.getAttributes().put(attributeNameLastSql, sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        if (statementExecuteAfterLogEnable && isStatementLogEnabled()) {
            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + "} executed. "
                         + sql);
        }
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        if (statementExecuteBatchAfterLogEnable && isStatementLogEnabled()) {
            String sql;
            if (statement instanceof PreparedStatementProxy) {
                sql = ((PreparedStatementProxy) statement).getSql();
            } else {
                sql = statement.getBatchSql();
            }

            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
                         + "} batch executed. " + sql);
        }
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        setLastSql(statement, sql);

        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        if (statementExecuteQueryAfterLogEnable && isStatementLogEnabled()) {
            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + ", rs-"
                         + resultSet.getId() + "} query executed. " + sql);
        }
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        setLastSql(statement, sql);

        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        if (statementExecuteUpdateAfterLogEnable && isStatementLogEnabled()) {
            statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
                         + "} update executed. effort " + updateCount + ". " + sql);
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
            statementLogError("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
                              + "} execute error. " + sql, error);
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

    protected void preparedStatement_setParameterBefore(PreparedStatementProxy statement, int parameterIndex,
                                                        int sqlType, Object... values) {
        JdbcParameter parameter = new JdbcParameter(sqlType, values);

        getParameters(statement).put(parameterIndex, parameter);
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, JdbcParameter> getParameters(PreparedStatementProxy statement) {
        Map<Integer, JdbcParameter> parameters = (Map<Integer, JdbcParameter>) statement.getAttributes().get(attributeNameParameter);

        if (parameters == null) {
            statement.getAttributes().put(attributeNameParameter, new HashMap<Integer, JdbcParameter>());
            parameters = (Map<Integer, JdbcParameter>) statement.getAttributes().get(attributeNameParameter);
        }

        return parameters;
    }

    protected void logParameter(PreparedStatementProxy statement) {
        if (statementParameterSetLogEnable && isStatementLogEnabled()) {
            {
                StringBuffer buf = new StringBuffer();
                buf.append("{conn-");
                buf.append(statement.getConnectionProxy().getId());
                buf.append(", ");
                buf.append(stmtId(statement));
                buf.append("}");
                buf.append(" Parameters : [");
                int parameterIndex = 0;
                for (JdbcParameter parameter : getParameters(statement).values()) {
                    if (parameterIndex != 0) {
                        buf.append(", ");
                    }
                    int sqlType = parameter.getSqlType();
                    Object[] values = parameter.getValues();
                    switch (sqlType) {
                        case Types.NULL:
                            buf.append("NULL");
                            break;
                        default:
                            buf.append(String.valueOf(values[0]));
                            break;
                    }
                    parameterIndex++;
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
                int parameterIndex = 0;
                for (JdbcParameter parameter : getParameters(statement).values()) {
                    if (parameterIndex != 0) {
                        buf.append(", ");
                    }
                    int sqlType = parameter.getSqlType();
                    buf.append(JdbcUtils.getTypeName(sqlType));
                    parameterIndex++;
                }
                buf.append("]");
                statementLog(buf.toString());
            }
        }
    }

}
