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

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public interface LogFilterMBean {

    String getDataSourceLoggerName();

    void setDataSourceLoggerName(String loggerName);

    boolean isDataSourceLogEnabled();

    void setDataSourceLogEnabled(boolean dataSourceLogEnabled);

    // //////////////

    String getConnectionLoggerName();

    void setConnectionLoggerName(String loggerName);

    boolean isConnectionLogEnabled();

    void setConnectionLogEnabled(boolean connectionLogEnabled);

    boolean isConnectionLogErrorEnabled();

    void setConnectionLogErrorEnabled(boolean connectionLogErrorEnabled);

    boolean isConnectionConnectBeforeLogEnabled();

    void setConnectionConnectBeforeLogEnabled(boolean beforeConnectionConnectLogEnable);

    boolean isConnectionConnectAfterLogEnabled();

    void setConnectionConnectAfterLogEnabled(boolean afterConnectionConnectLogEnable);

    boolean isConnectionCloseAfterLogEnabled();

    void setConnectionCloseAfterLogEnabled(boolean afterConnectionCloseLogEnable);

    boolean isConnectionCommitAfterLogEnabled();

    void setConnectionCommitAfterLogEnabled(boolean afterConnectionCommitLogEnable);

    // ////////////

    String getStatementLoggerName();

    void setStatementLoggerName(String loggerName);

    boolean isStatementLogEnabled();

    void setStatementLogEnabled(boolean statementLogEnabled);

    boolean isStatementCloseAfterLogEnabled();

    void setStatementCloseAfterLogEnabled(boolean afterStatementCloseLogEnable);

    boolean isStatementCreateAfterLogEnabled();

    void setStatementCreateAfterLogEnabled(boolean afterStatementCreateLogEnable);

    boolean isStatementExecuteBatchAfterLogEnabled();

    void setStatementExecuteBatchAfterLogEnabled(boolean afterStatementExecuteBatchLogEnable);

    boolean isStatementExecuteAfterLogEnabled();

    void setStatementExecuteAfterLogEnabled(boolean afterStatementExecuteLogEnable);

    boolean isStatementExecuteQueryAfterLogEnabled();

    void setStatementExecuteQueryAfterLogEnabled(boolean afterStatementExecuteQueryLogEnable);

    boolean isStatementExecuteUpdateAfterLogEnabled();

    void setStatementExecuteUpdateAfterLogEnabled(boolean afterStatementExecuteUpdateLogEnable);

    boolean isStatementPrepareCallAfterLogEnabled();

    void setStatementPrepareCallAfterLogEnabled(boolean afterStatementPrepareCallLogEnable);

    boolean isStatementPrepareAfterLogEnabled();

    void setStatementPrepareAfterLogEnabled(boolean afterStatementPrepareLogEnable);

    boolean isStatementLogErrorEnabled();

    void setStatementLogErrorEnabled(boolean statementLogErrorEnabled);

    boolean isStatementParameterSetLogEnabled();

    void setStatementParameterSetLogEnabled(boolean statementParameterSetLogEnable);

    // //////////////

    String getResultSetLoggerName();

    void setResultSetLoggerName(String loggerName);

    boolean isResultSetLogEnabled();

    void setResultSetLogEnabled(boolean resultSetLogEnabled);

    boolean isResultSetNextAfterLogEnabled();

    void setResultSetNextAfterLogEnabled(boolean afterResultSetNextLogEnable);

    boolean isResultSetOpenAfterLogEnabled();

    void setResultSetOpenAfterLogEnabled(boolean afterResultSetOpenLogEnable);

    boolean isResultSetLogErrorEnabled();

    void setResultSetLogErrorEnabled(boolean resultSetLogErrorEnabled);

    boolean isResultSetCloseAfterLogEnabled();

    void setResultSetCloseAfterLogEnabled(boolean resultSetCloseAfterLogEnable);
}
