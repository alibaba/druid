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
package com.alibaba.druid.bvt.proxy;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;
import org.apache.log4j.Priority;
import org.junit.Assert;

import com.alibaba.druid.filter.logging.Log4j2Filter;
import com.alibaba.druid.filter.logging.LogFilter;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;

import junit.framework.TestCase;

public class Log4j2FilterTest extends TestCase {

    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_logger() throws Exception {
        Log4j2Filter filter = new Log4j2Filter();

        filter.setDataSourceLoggerName("_datasource_name_");
        filter.setConnectionLoggerName("_connection_name_");
        filter.setStatementLoggerName("_statement_name_");
        filter.setResultSetLoggerName("_resultset_name_");

        Assert.assertEquals(filter.getDataSourceLoggerName(), "_datasource_name_");
        Assert.assertEquals(filter.getConnectionLoggerName(), "_connection_name_");
        Assert.assertEquals(filter.getStatementLoggerName(), "_statement_name_");
        Assert.assertEquals(filter.getResultSetLoggerName(), "_resultset_name_");

        filter.setDataSourceLogger(new FakeLogger("_datasoure_", true));
        filter.setConnectionLogger(new FakeLogger("_connection_", true));
        filter.setStatementLogger(new FakeLogger("_statement_", true));
        filter.setResultSetLogger(new FakeLogger("_resultset_", true));

        Assert.assertEquals(filter.getDataSourceLoggerName(), "_datasoure_");
        Assert.assertEquals(filter.getConnectionLoggerName(), "_connection_");
        Assert.assertEquals(filter.getStatementLoggerName(), "_statement_");
        Assert.assertEquals(filter.getResultSetLoggerName(), "_resultset_");

        setLogDisableAll(filter, true);

        Assert.assertTrue(filter.isDataSourceLogEnabled());

        Assert.assertTrue(filter.isConnectionLogErrorEnabled());
        Assert.assertTrue(filter.isConnectionConnectBeforeLogEnabled());
        Assert.assertTrue(filter.isConnectionConnectAfterLogEnabled());
        Assert.assertTrue(filter.isConnectionCloseAfterLogEnabled());
        Assert.assertTrue(filter.isConnectionCommitAfterLogEnabled());
        Assert.assertTrue(filter.isConnectionRollbackAfterLogEnabled());

        Assert.assertTrue(filter.isStatementLogEnabled());
        Assert.assertTrue(filter.isStatementLogErrorEnabled());
        Assert.assertTrue(filter.isStatementCreateAfterLogEnabled());
        Assert.assertTrue(filter.isStatementCloseAfterLogEnabled());
        Assert.assertTrue(filter.isStatementExecuteAfterLogEnabled());
        Assert.assertTrue(filter.isStatementExecuteBatchAfterLogEnabled());
        Assert.assertTrue(filter.isStatementExecuteQueryAfterLogEnabled());
        Assert.assertTrue(filter.isStatementExecuteUpdateAfterLogEnabled());
        Assert.assertTrue(filter.isStatementLogErrorEnabled());
        Assert.assertTrue(filter.isStatementParameterSetLogEnabled());
        Assert.assertTrue(filter.isStatementPrepareAfterLogEnabled());
        Assert.assertTrue(filter.isStatementPrepareCallAfterLogEnabled());

        Assert.assertTrue(filter.isResultSetLogEnabled());
        Assert.assertTrue(filter.isResultSetLogErrorEnabled());
        Assert.assertTrue(filter.isResultSetCloseAfterLogEnabled());
        Assert.assertTrue(filter.isResultSetNextAfterLogEnabled());
        Assert.assertTrue(filter.isResultSetOpenAfterLogEnabled());

        // ////

        filter.setDataSourceLogger(new FakeLogger("_datasoure_", false));
        filter.setConnectionLogger(new FakeLogger("_connection_", false));
        filter.setStatementLogger(new FakeLogger("_statement_", false));
        filter.setResultSetLogger(new FakeLogger("_resultset_", false));

        Assert.assertFalse(filter.isDataSourceLogEnabled());

        Assert.assertFalse(filter.isConnectionLogErrorEnabled());
        Assert.assertFalse(filter.isConnectionConnectBeforeLogEnabled());
        Assert.assertFalse(filter.isConnectionConnectAfterLogEnabled());
        Assert.assertFalse(filter.isConnectionCloseAfterLogEnabled());
        Assert.assertFalse(filter.isConnectionCommitAfterLogEnabled());
        Assert.assertFalse(filter.isConnectionRollbackAfterLogEnabled());

        Assert.assertFalse(filter.isStatementLogEnabled());
        Assert.assertFalse(filter.isStatementLogErrorEnabled());
        Assert.assertFalse(filter.isStatementCreateAfterLogEnabled());
        Assert.assertFalse(filter.isStatementCloseAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteBatchAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteQueryAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteUpdateAfterLogEnabled());
        Assert.assertFalse(filter.isStatementLogErrorEnabled());
        Assert.assertFalse(filter.isStatementParameterSetLogEnabled());
        Assert.assertFalse(filter.isStatementPrepareAfterLogEnabled());
        Assert.assertFalse(filter.isStatementPrepareCallAfterLogEnabled());

        Assert.assertFalse(filter.isResultSetLogEnabled());
        Assert.assertFalse(filter.isResultSetLogErrorEnabled());
        Assert.assertFalse(filter.isResultSetCloseAfterLogEnabled());
        Assert.assertFalse(filter.isResultSetNextAfterLogEnabled());
        Assert.assertFalse(filter.isResultSetOpenAfterLogEnabled());

        // //////////////////////////////////////

        setLogDisableAll(filter, false);

        // ////

        filter.setDataSourceLogger(new FakeLogger("_datasoure_", false));
        filter.setConnectionLogger(new FakeLogger("_connection_", false));
        filter.setStatementLogger(new FakeLogger("_statement_", false));
        filter.setResultSetLogger(new FakeLogger("_resultset_", false));

        Assert.assertFalse(filter.isDataSourceLogEnabled());

        Assert.assertFalse(filter.isConnectionLogErrorEnabled());
        Assert.assertFalse(filter.isConnectionConnectBeforeLogEnabled());
        Assert.assertFalse(filter.isConnectionConnectAfterLogEnabled());
        Assert.assertFalse(filter.isConnectionCloseAfterLogEnabled());
        Assert.assertFalse(filter.isConnectionCommitAfterLogEnabled());
        Assert.assertFalse(filter.isConnectionRollbackAfterLogEnabled());

        Assert.assertFalse(filter.isStatementLogEnabled());
        Assert.assertFalse(filter.isStatementLogErrorEnabled());
        Assert.assertFalse(filter.isStatementCreateAfterLogEnabled());
        Assert.assertFalse(filter.isStatementCloseAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteBatchAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteQueryAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteUpdateAfterLogEnabled());
        Assert.assertFalse(filter.isStatementLogErrorEnabled());
        Assert.assertFalse(filter.isStatementParameterSetLogEnabled());
        Assert.assertFalse(filter.isStatementPrepareAfterLogEnabled());
        Assert.assertFalse(filter.isStatementPrepareCallAfterLogEnabled());

        Assert.assertFalse(filter.isResultSetLogEnabled());
        Assert.assertFalse(filter.isResultSetLogErrorEnabled());
        Assert.assertFalse(filter.isResultSetCloseAfterLogEnabled());
        Assert.assertFalse(filter.isResultSetNextAfterLogEnabled());
        Assert.assertFalse(filter.isResultSetOpenAfterLogEnabled());

        // //

        // ////

        filter.setDataSourceLogger(new FakeLogger("_datasoure_", true));
        filter.setConnectionLogger(new FakeLogger("_connection_", true));
        filter.setStatementLogger(new FakeLogger("_statement_", true));
        filter.setResultSetLogger(new FakeLogger("_resultset_", true));

        Assert.assertFalse(filter.isDataSourceLogEnabled());

        Assert.assertFalse(filter.isConnectionLogErrorEnabled());
        Assert.assertFalse(filter.isConnectionConnectBeforeLogEnabled());
        Assert.assertFalse(filter.isConnectionConnectAfterLogEnabled());
        Assert.assertFalse(filter.isConnectionCloseAfterLogEnabled());
        Assert.assertFalse(filter.isConnectionCommitAfterLogEnabled());
        Assert.assertFalse(filter.isConnectionRollbackAfterLogEnabled());

        Assert.assertFalse(filter.isStatementLogEnabled());
        Assert.assertFalse(filter.isStatementLogErrorEnabled());
        Assert.assertFalse(filter.isStatementCreateAfterLogEnabled());
        Assert.assertFalse(filter.isStatementCloseAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteBatchAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteQueryAfterLogEnabled());
        Assert.assertFalse(filter.isStatementExecuteUpdateAfterLogEnabled());
        Assert.assertFalse(filter.isStatementLogErrorEnabled());
        Assert.assertFalse(filter.isStatementParameterSetLogEnabled());
        Assert.assertFalse(filter.isStatementPrepareAfterLogEnabled());
        Assert.assertFalse(filter.isStatementPrepareCallAfterLogEnabled());

        Assert.assertFalse(filter.isResultSetLogEnabled());
        Assert.assertFalse(filter.isResultSetLogErrorEnabled());
        Assert.assertFalse(filter.isResultSetCloseAfterLogEnabled());
        Assert.assertFalse(filter.isResultSetNextAfterLogEnabled());
        Assert.assertFalse(filter.isResultSetOpenAfterLogEnabled());
    }

    private void setLogDisableAll(LogFilter logFilter, boolean enable) {
        logFilter.setDataSourceLogEnabled(enable);

        logFilter.setConnectionLogEnabled(enable);
        logFilter.setConnectionLogErrorEnabled(enable);
        logFilter.setConnectionRollbackAfterLogEnabled(enable);
        logFilter.setConnectionConnectBeforeLogEnabled(enable);
        logFilter.setConnectionConnectAfterLogEnabled(enable);
        logFilter.setConnectionCommitAfterLogEnabled(enable);
        logFilter.setConnectionCloseAfterLogEnabled(enable);

        logFilter.setStatementLogEnabled(enable);
        logFilter.setStatementLogErrorEnabled(enable);
        logFilter.setStatementCreateAfterLogEnabled(enable);
        logFilter.setStatementExecuteAfterLogEnabled(enable);
        logFilter.setStatementExecuteBatchAfterLogEnabled(enable);
        logFilter.setStatementExecuteQueryAfterLogEnabled(enable);
        logFilter.setStatementExecuteUpdateAfterLogEnabled(enable);
        logFilter.setStatementPrepareCallAfterLogEnabled(enable);
        logFilter.setStatementPrepareAfterLogEnabled(enable);
        logFilter.setStatementCloseAfterLogEnabled(enable);

        logFilter.setResultSetLogEnabled(enable);
        logFilter.setResultSetOpenAfterLogEnabled(enable);
        logFilter.setResultSetNextAfterLogEnabled(enable);
        logFilter.setResultSetLogErrorEnabled(enable);
        logFilter.setResultSetCloseAfterLogEnabled(enable);
    }

    public static class FakeLogger implements Logger {

        private boolean enable;
        private String name;

        public FakeLogger(String name, boolean enable){
            this.name = name;
            this.enable = enable;
        }

        public boolean isDebugEnabled() {
            return this.enable;
        }

        public boolean isEnabledFor(Priority level) {
            return this.enable;
        }

        @Override
        public void catching(Level level, Throwable t) {

        }

        @Override
        public void catching(Throwable t) {

        }

        @Override
        public void debug(Marker marker, Message msg) {

        }

        @Override
        public void debug(Marker marker, Message msg, Throwable t) {

        }

        @Override
        public void debug(Marker marker, MessageSupplier msgSupplier) {

        }

        @Override
        public void debug(Marker marker, MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void debug(Marker marker, Object message) {

        }

        @Override
        public void debug(Marker marker, Object message, Throwable t) {

        }

        @Override
        public void debug(Marker marker, String message) {

        }

        @Override
        public void debug(Marker marker, String message, Object... params) {

        }

        @Override
        public void debug(Marker marker, String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void debug(Marker marker, String message, Throwable t) {

        }

        @Override
        public void debug(Marker marker, Supplier<?> msgSupplier) {

        }

        @Override
        public void debug(Marker marker, Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void debug(Message msg) {

        }

        @Override
        public void debug(Message msg, Throwable t) {

        }

        @Override
        public void debug(MessageSupplier msgSupplier) {

        }

        @Override
        public void debug(MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void debug(Object message) {

        }

        @Override
        public void debug(Object message, Throwable t) {

        }

        @Override
        public void debug(String message) {

        }

        @Override
        public void debug(String message, Object... params) {

        }

        @Override
        public void debug(String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void debug(String message, Throwable t) {

        }

        @Override
        public void debug(Supplier<?> msgSupplier) {

        }

        @Override
        public void debug(Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void entry() {

        }

        @Override
        public void entry(Object... params) {

        }

        @Override
        public void error(Marker marker, Message msg) {

        }

        @Override
        public void error(Marker marker, Message msg, Throwable t) {

        }

        @Override
        public void error(Marker marker, MessageSupplier msgSupplier) {

        }

        @Override
        public void error(Marker marker, MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void error(Marker marker, Object message) {

        }

        @Override
        public void error(Marker marker, Object message, Throwable t) {

        }

        @Override
        public void error(Marker marker, String message) {

        }

        @Override
        public void error(Marker marker, String message, Object... params) {

        }

        @Override
        public void error(Marker marker, String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void error(Marker marker, String message, Throwable t) {

        }

        @Override
        public void error(Marker marker, Supplier<?> msgSupplier) {

        }

        @Override
        public void error(Marker marker, Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void error(Message msg) {

        }

        @Override
        public void error(Message msg, Throwable t) {

        }

        @Override
        public void error(MessageSupplier msgSupplier) {

        }

        @Override
        public void error(MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void error(Object message) {

        }

        @Override
        public void error(Object message, Throwable t) {

        }

        @Override
        public void error(String message) {

        }

        @Override
        public void error(String message, Object... params) {

        }

        @Override
        public void error(String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void error(String message, Throwable t) {

        }

        @Override
        public void error(Supplier<?> msgSupplier) {

        }

        @Override
        public void error(Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void exit() {

        }

        @Override
        public <R> R exit(R result) {

            return null;
        }

        @Override
        public void fatal(Marker marker, Message msg) {

        }

        @Override
        public void fatal(Marker marker, Message msg, Throwable t) {

        }

        @Override
        public void fatal(Marker marker, MessageSupplier msgSupplier) {

        }

        @Override
        public void fatal(Marker marker, MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void fatal(Marker marker, Object message) {

        }

        @Override
        public void fatal(Marker marker, Object message, Throwable t) {

        }

        @Override
        public void fatal(Marker marker, String message) {

        }

        @Override
        public void fatal(Marker marker, String message, Object... params) {

        }

        @Override
        public void fatal(Marker marker, String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void fatal(Marker marker, String message, Throwable t) {

        }

        @Override
        public void fatal(Marker marker, Supplier<?> msgSupplier) {

        }

        @Override
        public void fatal(Marker marker, Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void fatal(Message msg) {

        }

        @Override
        public void fatal(Message msg, Throwable t) {

        }

        @Override
        public void fatal(MessageSupplier msgSupplier) {

        }

        @Override
        public void fatal(MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void fatal(Object message) {

        }

        @Override
        public void fatal(Object message, Throwable t) {

        }

        @Override
        public void fatal(String message) {

        }

        @Override
        public void fatal(String message, Object... params) {

        }

        @Override
        public void fatal(String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void fatal(String message, Throwable t) {

        }

        @Override
        public void fatal(Supplier<?> msgSupplier) {

        }

        @Override
        public void fatal(Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public Level getLevel() {

            return null;
        }

        @Override
        public MessageFactory getMessageFactory() {

            return null;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void info(Marker marker, Message msg) {

        }

        @Override
        public void info(Marker marker, Message msg, Throwable t) {

        }

        @Override
        public void info(Marker marker, MessageSupplier msgSupplier) {

        }

        @Override
        public void info(Marker marker, MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void info(Marker marker, Object message) {

        }

        @Override
        public void info(Marker marker, Object message, Throwable t) {

        }

        @Override
        public void info(Marker marker, String message) {

        }

        @Override
        public void info(Marker marker, String message, Object... params) {

        }

        @Override
        public void info(Marker marker, String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void info(Marker marker, String message, Throwable t) {

        }

        @Override
        public void info(Marker marker, Supplier<?> msgSupplier) {

        }

        @Override
        public void info(Marker marker, Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void info(Message msg) {

        }

        @Override
        public void info(Message msg, Throwable t) {

        }

        @Override
        public void info(MessageSupplier msgSupplier) {

        }

        @Override
        public void info(MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void info(Object message) {

        }

        @Override
        public void info(Object message, Throwable t) {

        }

        @Override
        public void info(String message) {

        }

        @Override
        public void info(String message, Object... params) {

        }

        @Override
        public void info(String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void info(String message, Throwable t) {

        }

        @Override
        public void info(Supplier<?> msgSupplier) {

        }

        @Override
        public void info(Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public boolean isDebugEnabled(Marker marker) {

            return this.enable;
        }

        @Override
        public boolean isEnabled(Level level) {

            return this.enable;
        }

        @Override
        public boolean isEnabled(Level level, Marker marker) {

            return this.enable;
        }

        @Override
        public boolean isErrorEnabled() {
            return this.enable;
        }

        @Override
        public boolean isErrorEnabled(Marker marker) {

            return this.enable;
        }

        @Override
        public boolean isFatalEnabled() {

            return this.enable;
        }

        @Override
        public boolean isFatalEnabled(Marker marker) {

            return this.enable;
        }

        @Override
        public boolean isInfoEnabled() {

            return this.enable;
        }

        @Override
        public boolean isInfoEnabled(Marker marker) {

            return this.enable;
        }

        @Override
        public boolean isTraceEnabled() {

            return this.enable;
        }

        @Override
        public boolean isTraceEnabled(Marker marker) {

            return this.enable;
        }

        @Override
        public boolean isWarnEnabled() {

            return this.enable;
        }

        @Override
        public boolean isWarnEnabled(Marker marker) {

            return this.enable;
        }

        @Override
        public void log(Level level, Marker marker, Message msg) {

        }

        @Override
        public void log(Level level, Marker marker, Message msg, Throwable t) {

        }

        @Override
        public void log(Level level, Marker marker, MessageSupplier msgSupplier) {

        }

        @Override
        public void log(Level level, Marker marker, MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void log(Level level, Marker marker, Object message) {

        }

        @Override
        public void log(Level level, Marker marker, Object message, Throwable t) {

        }

        @Override
        public void log(Level level, Marker marker, String message) {

        }

        @Override
        public void log(Level level, Marker marker, String message, Object... params) {

        }

        @Override
        public void log(Level level, Marker marker, String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void log(Level level, Marker marker, String message, Throwable t) {

        }

        @Override
        public void log(Level level, Marker marker, Supplier<?> msgSupplier) {

        }

        @Override
        public void log(Level level, Marker marker, Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void log(Level level, Message msg) {

        }

        @Override
        public void log(Level level, Message msg, Throwable t) {

        }

        @Override
        public void log(Level level, MessageSupplier msgSupplier) {

        }

        @Override
        public void log(Level level, MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void log(Level level, Object message) {

        }

        @Override
        public void log(Level level, Object message, Throwable t) {

        }

        @Override
        public void log(Level level, String message) {

        }

        @Override
        public void log(Level level, String message, Object... params) {

        }

        @Override
        public void log(Level level, String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void log(Level level, String message, Throwable t) {

        }

        @Override
        public void log(Level level, Supplier<?> msgSupplier) {

        }

        @Override
        public void log(Level level, Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void printf(Level level, Marker marker, String format, Object... params) {

        }

        @Override
        public void printf(Level level, String format, Object... params) {

        }

        @Override
        public <T extends Throwable> T throwing(Level level, T t) {

            return null;
        }

        @Override
        public <T extends Throwable> T throwing(T t) {

            return null;
        }

        @Override
        public void trace(Marker marker, Message msg) {

        }

        @Override
        public void trace(Marker marker, Message msg, Throwable t) {

        }

        @Override
        public void trace(Marker marker, MessageSupplier msgSupplier) {

        }

        @Override
        public void trace(Marker marker, MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void trace(Marker marker, Object message) {

        }

        @Override
        public void trace(Marker marker, Object message, Throwable t) {

        }

        @Override
        public void trace(Marker marker, String message) {

        }

        @Override
        public void trace(Marker marker, String message, Object... params) {

        }

        @Override
        public void trace(Marker marker, String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void trace(Marker marker, String message, Throwable t) {

        }

        @Override
        public void trace(Marker marker, Supplier<?> msgSupplier) {

        }

        @Override
        public void trace(Marker marker, Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void trace(Message msg) {

        }

        @Override
        public void trace(Message msg, Throwable t) {

        }

        @Override
        public void trace(MessageSupplier msgSupplier) {

        }

        @Override
        public void trace(MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void trace(Object message) {

        }

        @Override
        public void trace(Object message, Throwable t) {

        }

        @Override
        public void trace(String message) {

        }

        @Override
        public void trace(String message, Object... params) {

        }

        @Override
        public void trace(String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void trace(String message, Throwable t) {

        }

        @Override
        public void trace(Supplier<?> msgSupplier) {

        }

        @Override
        public void trace(Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void warn(Marker marker, Message msg) {

        }

        @Override
        public void warn(Marker marker, Message msg, Throwable t) {

        }

        @Override
        public void warn(Marker marker, MessageSupplier msgSupplier) {

        }

        @Override
        public void warn(Marker marker, MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void warn(Marker marker, Object message) {

        }

        @Override
        public void warn(Marker marker, Object message, Throwable t) {

        }

        @Override
        public void warn(Marker marker, String message) {

        }

        @Override
        public void warn(Marker marker, String message, Object... params) {

        }

        @Override
        public void warn(Marker marker, String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void warn(Marker marker, String message, Throwable t) {

        }

        @Override
        public void warn(Marker marker, Supplier<?> msgSupplier) {

        }

        @Override
        public void warn(Marker marker, Supplier<?> msgSupplier, Throwable t) {

        }

        @Override
        public void warn(Message msg) {

        }

        @Override
        public void warn(Message msg, Throwable t) {

        }

        @Override
        public void warn(MessageSupplier msgSupplier) {

        }

        @Override
        public void warn(MessageSupplier msgSupplier, Throwable t) {

        }

        @Override
        public void warn(Object message) {

        }

        @Override
        public void warn(Object message, Throwable t) {

        }

        @Override
        public void warn(String message) {

        }

        @Override
        public void warn(String message, Object... params) {

        }

        @Override
        public void warn(String message, Supplier<?>... paramSuppliers) {

        }

        @Override
        public void warn(String message, Throwable t) {

        }

        @Override
        public void warn(Supplier<?> msgSupplier) {

        }

        @Override
        public void warn(Supplier<?> msgSupplier, Throwable t) {

        }
    }
}
