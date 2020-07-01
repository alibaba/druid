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

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.junit.Assert;

import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.logging.LogFilter;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;

public class Log4jFilterTest extends TestCase {
    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }
    
    public void test_logger() throws Exception {
        Log4jFilter filter = new Log4jFilter();

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

    public static class FakeLogger extends Logger {

        private boolean enable;

        public FakeLogger(String name, boolean enable){
            super(name);
            this.enable = enable;
        }

        public boolean isDebugEnabled() {
            return this.enable;
        }

        public boolean isEnabledFor(Priority level) {
            return this.enable;
        }
    }
}
