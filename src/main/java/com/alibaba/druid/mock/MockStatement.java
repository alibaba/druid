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
package com.alibaba.druid.mock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.util.jdbc.StatementBase;

public class MockStatement extends StatementBase implements MockStatementBase, Statement {

    public final static String ERROR_SQL = "THROW ERROR";

    protected MockConnection   mockConnection;

    public MockStatement(Connection connection){
        super(connection);

        if (connection instanceof MockConnection) {
            mockConnection = (MockConnection) connection;
        }
    }

    protected void checkOpen() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        if (this.mockConnection != null) {
            mockConnection.checkState();
        }
    }

    public MockConnection getConnection() {
        return mockConnection;
    }

    public void setFakeConnection(MockConnection fakeConnection) {
        this.mockConnection = fakeConnection;
        this.setConnection(fakeConnection);
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        checkOpen();

        if (mockConnection != null && mockConnection.getDriver() != null) {
            return mockConnection.getDriver().executeQuery(this, sql);
        }

        return new MockResultSet(this);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        checkOpen();

        if (mockConnection != null) {
            mockConnection.handleSleep();
        }

        return 0;
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        checkOpen();

        if (ERROR_SQL.equals(sql)) {
            throw new SQLException();
        }

        if (mockConnection != null) {
            mockConnection.setLastSql(sql);
            mockConnection.handleSleep();
        }

        return false;
    }

}
