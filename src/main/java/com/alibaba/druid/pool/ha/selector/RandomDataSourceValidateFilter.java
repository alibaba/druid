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
package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

/**
 * A Druid Filter that records the last success execute time.
 *
 * @author DigitalSonic
 */
public class RandomDataSourceValidateFilter extends FilterEventAdapter {
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        recordTime(statement);
    }

    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        recordTime(statement);
    }

    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        recordTime(statement);
    }

    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        recordTime(statement);
    }

    private void recordTime(StatementProxy statement) {
        ConnectionProxy conn = statement.getConnectionProxy();
        if (conn != null) {
            DataSourceProxy dataSource = conn.getDirectDataSource();
            RandomDataSourceValidateThread.logSuccessTime(dataSource);
        }
    }
}
