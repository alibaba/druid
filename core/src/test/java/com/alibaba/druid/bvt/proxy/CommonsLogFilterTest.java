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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.alibaba.druid.filter.logging.CommonsLogFilter;
import com.alibaba.druid.filter.logging.LogFilter;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;

public class CommonsLogFilterTest extends TestCase {
    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_logger() throws Exception {
        CommonsLogFilter filter = new CommonsLogFilter();

        filter.setDataSourceLoggerName("_datasource_name_");
        filter.setConnectionLoggerName("_connection_name_");
        filter.setStatementLoggerName("_statement_name_");
        filter.setResultSetLoggerName("_resultset_name_");

        assertEquals(filter.getDataSourceLoggerName(), "_datasource_name_");
        assertEquals(filter.getConnectionLoggerName(), "_connection_name_");
        assertEquals(filter.getStatementLoggerName(), "_statement_name_");
        assertEquals(filter.getResultSetLoggerName(), "_resultset_name_");

        filter.setDataSourceLogger(new FakeLogger("_datasoure_", true));
        filter.setConnectionLogger(new FakeLogger("_connection_", true));
        filter.setStatementLogger(new FakeLogger("_statement_", true));
        filter.setResultSetLogger(new FakeLogger("_resultset_", true));

        assertEquals(filter.getDataSourceLoggerName(), "_datasoure_");
        assertEquals(filter.getConnectionLoggerName(), "_connection_");
        assertEquals(filter.getStatementLoggerName(), "_statement_");
        assertEquals(filter.getResultSetLoggerName(), "_resultset_");

        setLogEnableAll(filter, true);

        assertTrue(filter.isDataSourceLogEnabled());

        assertTrue(filter.isConnectionLogEnabled());
        assertTrue(filter.isConnectionLogErrorEnabled());
        assertTrue(filter.isConnectionConnectBeforeLogEnabled());
        assertTrue(filter.isConnectionConnectAfterLogEnabled());
        assertTrue(filter.isConnectionCloseAfterLogEnabled());
        assertTrue(filter.isConnectionCommitAfterLogEnabled());
        assertTrue(filter.isConnectionRollbackAfterLogEnabled());

        assertTrue(filter.isStatementLogEnabled());
        assertTrue(filter.isStatementLogErrorEnabled());
        assertTrue(filter.isStatementCreateAfterLogEnabled());
        assertTrue(filter.isStatementCloseAfterLogEnabled());
        assertTrue(filter.isStatementExecuteAfterLogEnabled());
        assertTrue(filter.isStatementExecuteBatchAfterLogEnabled());
        assertTrue(filter.isStatementExecuteQueryAfterLogEnabled());
        assertTrue(filter.isStatementExecuteUpdateAfterLogEnabled());
        assertTrue(filter.isStatementLogErrorEnabled());
        assertTrue(filter.isStatementParameterSetLogEnabled());
        assertTrue(filter.isStatementPrepareAfterLogEnabled());
        assertTrue(filter.isStatementPrepareCallAfterLogEnabled());

        assertTrue(filter.isResultSetLogEnabled());
        assertTrue(filter.isResultSetLogErrorEnabled());
        assertTrue(filter.isResultSetCloseAfterLogEnabled());
        assertTrue(filter.isResultSetNextAfterLogEnabled());
        assertTrue(filter.isResultSetOpenAfterLogEnabled());

        // ////

        filter.setDataSourceLogger(new FakeLogger("_datasoure_", false));
        filter.setConnectionLogger(new FakeLogger("_connection_", false));
        filter.setStatementLogger(new FakeLogger("_statement_", false));
        filter.setResultSetLogger(new FakeLogger("_resultset_", false));

        assertFalse(filter.isDataSourceLogEnabled());

        assertFalse(filter.isConnectionLogEnabled());
        assertFalse(filter.isConnectionLogErrorEnabled());
        assertFalse(filter.isConnectionConnectBeforeLogEnabled());
        assertFalse(filter.isConnectionConnectAfterLogEnabled());
        assertFalse(filter.isConnectionCloseAfterLogEnabled());
        assertFalse(filter.isConnectionCommitAfterLogEnabled());
        assertFalse(filter.isConnectionRollbackAfterLogEnabled());

        assertFalse(filter.isStatementLogEnabled());
        assertFalse(filter.isStatementLogErrorEnabled());
        assertFalse(filter.isStatementCreateAfterLogEnabled());
        assertFalse(filter.isStatementCloseAfterLogEnabled());
        assertFalse(filter.isStatementExecuteAfterLogEnabled());
        assertFalse(filter.isStatementExecuteBatchAfterLogEnabled());
        assertFalse(filter.isStatementExecuteQueryAfterLogEnabled());
        assertFalse(filter.isStatementExecuteUpdateAfterLogEnabled());
        assertFalse(filter.isStatementLogErrorEnabled());
        assertFalse(filter.isStatementParameterSetLogEnabled());
        assertFalse(filter.isStatementPrepareAfterLogEnabled());
        assertFalse(filter.isStatementPrepareCallAfterLogEnabled());

        assertFalse(filter.isResultSetLogEnabled());
        assertFalse(filter.isResultSetLogErrorEnabled());
        assertFalse(filter.isResultSetCloseAfterLogEnabled());
        assertFalse(filter.isResultSetNextAfterLogEnabled());
        assertFalse(filter.isResultSetOpenAfterLogEnabled());

        // ////////////////////////////////////////
        // ////////////////////////////////////////
        // ////////////////////////////////////////

        setLogEnableAll(filter, false);

        // ////

        filter.setDataSourceLogger(null);
        filter.setConnectionLogger(null);
        filter.setStatementLogger(null);
        filter.setResultSetLogger(null);

        filter.setDataSourceLogger(new FakeLogger("_datasoure_", false));
        filter.setConnectionLogger(new FakeLogger("_connection_", false));
        filter.setStatementLogger(new FakeLogger("_statement_", false));
        filter.setResultSetLogger(new FakeLogger("_resultset_", false));

        filter.setStatementLogErrorEnabled(true);
        assertFalse(filter.isStatementLogErrorEnabled());
        filter.setStatementLogErrorEnabled(false);
        assertFalse(filter.isStatementLogErrorEnabled());

        assertFalse(filter.isDataSourceLogEnabled());

        assertFalse(filter.isConnectionLogEnabled());
        assertFalse(filter.isConnectionLogErrorEnabled());
        assertFalse(filter.isConnectionConnectBeforeLogEnabled());
        assertFalse(filter.isConnectionConnectAfterLogEnabled());
        assertFalse(filter.isConnectionCloseAfterLogEnabled());
        assertFalse(filter.isConnectionCommitAfterLogEnabled());
        assertFalse(filter.isConnectionRollbackAfterLogEnabled());

        assertFalse(filter.isStatementLogEnabled());
        assertFalse(filter.isStatementLogErrorEnabled());
        assertFalse(filter.isStatementCreateAfterLogEnabled());
        assertFalse(filter.isStatementCloseAfterLogEnabled());
        assertFalse(filter.isStatementExecuteAfterLogEnabled());
        assertFalse(filter.isStatementExecuteBatchAfterLogEnabled());
        assertFalse(filter.isStatementExecuteQueryAfterLogEnabled());
        assertFalse(filter.isStatementExecuteUpdateAfterLogEnabled());
        assertFalse(filter.isStatementLogErrorEnabled());
        assertFalse(filter.isStatementParameterSetLogEnabled());
        assertFalse(filter.isStatementPrepareAfterLogEnabled());
        assertFalse(filter.isStatementPrepareCallAfterLogEnabled());

        assertFalse(filter.isResultSetLogEnabled());
        assertFalse(filter.isResultSetLogErrorEnabled());
        assertFalse(filter.isResultSetCloseAfterLogEnabled());
        assertFalse(filter.isResultSetNextAfterLogEnabled());
        assertFalse(filter.isResultSetOpenAfterLogEnabled());

        // //

        // ////

        filter.setDataSourceLogger(new FakeLogger("_datasoure_", true));
        filter.setConnectionLogger(new FakeLogger("_connection_", true));
        filter.setStatementLogger(new FakeLogger("_statement_", true));
        filter.setResultSetLogger(new FakeLogger("_resultset_", true));

        assertFalse(filter.isConnectionLogEnabled());
        assertFalse(filter.isStatementLogEnabled());
        assertFalse(filter.isResultSetLogEnabled());

        assertFalse(filter.isStatementLogErrorEnabled());
        filter.setStatementLogErrorEnabled(true);
        assertTrue(filter.isStatementLogErrorEnabled());
        filter.setStatementLogErrorEnabled(false);

        filter.setConnectionLogEnabled(true);
        filter.setStatementLogEnabled(true);
        filter.setResultSetLogEnabled(true);

        assertFalse(filter.isDataSourceLogEnabled());

        assertFalse(filter.isConnectionLogErrorEnabled());
        assertFalse(filter.isConnectionConnectBeforeLogEnabled());
        assertFalse(filter.isConnectionConnectAfterLogEnabled());
        assertFalse(filter.isConnectionCloseAfterLogEnabled());
        assertFalse(filter.isConnectionCommitAfterLogEnabled());
        assertFalse(filter.isConnectionRollbackAfterLogEnabled());

        assertFalse(filter.isStatementLogErrorEnabled());
        assertFalse(filter.isStatementCreateAfterLogEnabled());
        assertFalse(filter.isStatementCloseAfterLogEnabled());
        assertFalse(filter.isStatementExecuteAfterLogEnabled());
        assertFalse(filter.isStatementExecuteBatchAfterLogEnabled());
        assertFalse(filter.isStatementExecuteQueryAfterLogEnabled());
        assertFalse(filter.isStatementExecuteUpdateAfterLogEnabled());
        assertFalse(filter.isStatementLogErrorEnabled());
        assertFalse(filter.isStatementParameterSetLogEnabled());
        assertFalse(filter.isStatementPrepareAfterLogEnabled());
        assertFalse(filter.isStatementPrepareCallAfterLogEnabled());

        assertFalse(filter.isResultSetLogErrorEnabled());
        assertFalse(filter.isResultSetCloseAfterLogEnabled());
        assertFalse(filter.isResultSetNextAfterLogEnabled());
        assertFalse(filter.isResultSetOpenAfterLogEnabled());

    }

    @SuppressWarnings("serial")
    public static class FakeLogger extends Log4JLogger {
        private boolean enable;

        public FakeLogger(String name, boolean enable) {
            super(name);
            this.enable = enable;
        }

        public boolean isDebugEnabled() {
            return this.enable;
        }

        public boolean isErrorEnabled() {
            return this.enable;
        }

        public boolean isEnabledFor(Priority level) {
            return this.enable;
        }
    }

    private void setLogEnableAll(LogFilter logFilter, boolean enable) {
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
        logFilter.setStatementParameterSetLogEnabled(enable);

        logFilter.setResultSetLogEnabled(enable);
        logFilter.setResultSetOpenAfterLogEnabled(enable);
        logFilter.setResultSetNextAfterLogEnabled(enable);
        logFilter.setResultSetLogErrorEnabled(enable);
        logFilter.setResultSetCloseAfterLogEnabled(enable);
    }
}
