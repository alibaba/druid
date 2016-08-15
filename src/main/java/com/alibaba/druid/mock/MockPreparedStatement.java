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
package com.alibaba.druid.mock;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.druid.util.jdbc.PreparedStatementBase;

public class MockPreparedStatement extends PreparedStatementBase implements MockStatementBase, PreparedStatement {

    private final String sql;

    public MockPreparedStatement(MockConnection conn, String sql){
        super(conn);
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public MockConnection getConnection() throws SQLException {
        return (MockConnection) super.getConnection();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        checkOpen();
        
        MockConnection conn = getConnection();

        if (conn != null && conn.getDriver() != null) {
            return conn.getDriver().executeQuery(this, sql);
        }

        if (conn != null) {
            conn.handleSleep();
            
            return conn.getDriver().createMockResultSet(this);
        }
        
        return new MockResultSet(this);
    }

    @Override
    public int executeUpdate() throws SQLException {
        checkOpen();

        if (getConnection() != null) {
            getConnection().handleSleep();
        }

        return 0;
    }

    @Override
    public boolean execute() throws SQLException {
        checkOpen();

        if (getConnection() != null) {
            getConnection().handleSleep();
        }
        
        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        checkOpen();
        
        if (resultSet == null) {
            resultSet = this.getConnection().getDriver().createResultSet(this);
        }

        return resultSet;
    }
}
