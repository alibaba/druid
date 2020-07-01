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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class CommonsLogFilter extends LogFilter {

    private Log    dataSourceLogger     = LogFactory.getLog(dataSourceLoggerName);
    private Log    connectionLogger     = LogFactory.getLog(connectionLoggerName);
    private Log    statementLogger      = LogFactory.getLog(statementLoggerName);
    private Log    resultSetLogger      = LogFactory.getLog(resultSetLoggerName);

    public void setDataSourceLogger(Log dataSourceLogger) {
        this.dataSourceLogger = dataSourceLogger;
        if (dataSourceLogger instanceof Log4JLogger) {
            this.dataSourceLoggerName = ((Log4JLogger) dataSourceLogger).getLogger().getName();
        }
    }

    @Override
    public String getDataSourceLoggerName() {
        return dataSourceLoggerName;
    }

    @Override
    public void setDataSourceLoggerName(String dataSourceLoggerName) {
        this.dataSourceLoggerName = dataSourceLoggerName;
        dataSourceLogger = LogFactory.getLog(dataSourceLoggerName);
    }

    @Override
    public String getConnectionLoggerName() {
        return connectionLoggerName;
    }

    @Override
    public void setConnectionLoggerName(String connectionLoggerName) {
        this.connectionLoggerName = connectionLoggerName;
        connectionLogger = LogFactory.getLog(connectionLoggerName);
    }

    public void setConnectionLogger(Log connectionLogger) {
        this.connectionLogger = connectionLogger;
        if (connectionLogger instanceof Log4JLogger) {
            this.connectionLoggerName = ((Log4JLogger) connectionLogger).getLogger().getName();
        }
    }

    @Override
    public String getStatementLoggerName() {
        return statementLoggerName;
    }

    @Override
    public void setStatementLoggerName(String statementLoggerName) {
        this.statementLoggerName = statementLoggerName;
        statementLogger = LogFactory.getLog(statementLoggerName);
    }

    public void setStatementLogger(Log statementLogger) {
        this.statementLogger = statementLogger;
        if (statementLogger instanceof Log4JLogger) {
            this.statementLoggerName = ((Log4JLogger) statementLogger).getLogger().getName();
        }
    }

    @Override
    public String getResultSetLoggerName() {
        return resultSetLoggerName;
    }

    @Override
    public void setResultSetLoggerName(String resultSetLoggerName) {
        this.resultSetLoggerName = resultSetLoggerName;
        resultSetLogger = LogFactory.getLog(resultSetLoggerName);
    }

    public void setResultSetLogger(Log resultSetLogger) {
        this.resultSetLogger = statementLogger;
        if (resultSetLogger instanceof Log4JLogger) {
            this.resultSetLoggerName = ((Log4JLogger) resultSetLogger).getLogger().getName();
        }
    }

    @Override
    public boolean isDataSourceLogEnabled() {
        return dataSourceLogger.isDebugEnabled() && super.isDataSourceLogEnabled();
    }

    public boolean isConnectionLogErrorEnabled() {
        return connectionLogger.isErrorEnabled() && super.isConnectionLogErrorEnabled();
    }

    @Override
    public boolean isConnectionLogEnabled() {
        return connectionLogger.isDebugEnabled() && super.isConnectionLogEnabled();
    }

    @Override
    public boolean isStatementLogEnabled() {
        return statementLogger.isDebugEnabled() && super.isStatementLogEnabled();
    }

    @Override
    public boolean isResultSetLogEnabled() {
        return resultSetLogger.isDebugEnabled() && super.isResultSetLogEnabled();
    }

    @Override
    public boolean isResultSetLogErrorEnabled() {
        return resultSetLogger.isErrorEnabled() && super.isResultSetLogErrorEnabled();
    }

    @Override
    public boolean isStatementLogErrorEnabled() {
        return statementLogger.isErrorEnabled() && super.isStatementLogErrorEnabled();
    }

    @Override
    protected void connectionLog(String message) {
        connectionLogger.debug(message);
    }

    @Override
    protected void statementLog(String message) {
        statementLogger.debug(message);
    }

    @Override
    protected void resultSetLog(String message) {
        resultSetLogger.debug(message);
    }

    @Override
    protected void resultSetLogError(String message, Throwable error) {
        resultSetLogger.error(message, error);
    }

    @Override
    protected void statementLogError(String message, Throwable error) {
        statementLogger.error(message, error);
    }
}
