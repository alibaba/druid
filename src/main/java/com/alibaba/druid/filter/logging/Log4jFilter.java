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
package com.alibaba.druid.filter.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class Log4jFilter extends LogFilter implements Log4jFilterMBean {

    private Logger dataSourceLogger = Logger.getLogger(dataSourceLoggerName);
    private Logger connectionLogger = Logger.getLogger(connectionLoggerName);
    private Logger statementLogger  = Logger.getLogger(statementLoggerName);
    private Logger resultSetLogger  = Logger.getLogger(resultSetLoggerName);

    @Override
    public String getDataSourceLoggerName() {
        return dataSourceLoggerName;
    }

    @Override
    public void setDataSourceLoggerName(String dataSourceLoggerName) {
        this.dataSourceLoggerName = dataSourceLoggerName;
        dataSourceLogger = Logger.getLogger(dataSourceLoggerName);
    }

    public void setDataSourceLogger(Logger dataSourceLogger) {
        this.dataSourceLogger = dataSourceLogger;
        this.dataSourceLoggerName = dataSourceLogger.getName();
    }

    @Override
    public String getConnectionLoggerName() {
        return connectionLoggerName;
    }

    @Override
    public void setConnectionLoggerName(String connectionLoggerName) {
        this.connectionLoggerName = connectionLoggerName;
        connectionLogger = Logger.getLogger(connectionLoggerName);
    }

    public void setConnectionLogger(Logger connectionLogger) {
        this.connectionLogger = connectionLogger;
        this.connectionLoggerName = connectionLogger.getName();
    }

    @Override
    public String getStatementLoggerName() {
        return statementLoggerName;
    }

    @Override
    public void setStatementLoggerName(String statementLoggerName) {
        this.statementLoggerName = statementLoggerName;
        statementLogger = Logger.getLogger(statementLoggerName);
    }

    public void setStatementLogger(Logger statementLogger) {
        this.statementLogger = statementLogger;
        this.statementLoggerName = statementLogger.getName();
    }

    @Override
    public String getResultSetLoggerName() {
        return resultSetLoggerName;
    }

    @Override
    public void setResultSetLoggerName(String resultSetLoggerName) {
        this.resultSetLoggerName = resultSetLoggerName;
        resultSetLogger = Logger.getLogger(resultSetLoggerName);
    }

    public void setResultSetLogger(Logger resultSetLogger) {
        this.resultSetLogger = resultSetLogger;
        this.resultSetLoggerName = resultSetLogger.getName();
    }

    @Override
    public boolean isConnectionLogErrorEnabled() {
        return connectionLogger.isEnabledFor(Level.ERROR) && super.isConnectionLogErrorEnabled();
    }

    @Override
    public boolean isDataSourceLogEnabled() {
        return dataSourceLogger.isDebugEnabled() && super.isDataSourceLogEnabled();
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
        return resultSetLogger.isEnabledFor(Level.ERROR) && super.isResultSetLogErrorEnabled();
    }

    @Override
    public boolean isStatementLogErrorEnabled() {
        return statementLogger.isEnabledFor(Level.ERROR) && super.isStatementLogErrorEnabled();
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
