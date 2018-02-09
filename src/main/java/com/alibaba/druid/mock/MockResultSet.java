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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.util.jdbc.ResultSetBase;

public class MockResultSet extends ResultSetBase implements ResultSet {

    private int            rowIndex = -1;
    private List<Object[]> rows;

    public MockResultSet(Statement statement){
        this(statement, new ArrayList<Object[]>());
    }

    public MockResultSet(Statement statement, List<Object[]> rows){
        super(statement);
        this.rows = rows;
        super.metaData = new MockResultSetMetaData();
    }

    public List<Object[]> getRows() {
        return rows;
    }

    @Override
    public synchronized boolean next() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        if (rowIndex < rows.size() - 1) {
            rowIndex++;
            return true;
        }
        return false;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }

        return (MockResultSetMetaData) metaData;
    }

    public MockResultSetMetaData getMockMetaData() throws SQLException {
        return (MockResultSetMetaData) metaData;
    }

    public Object getObjectInternal(int columnIndex) {
        Object[] row = rows.get(rowIndex);
        Object obj = row[columnIndex - 1];
        return obj;
    }

    @Override
    public synchronized boolean previous() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        if (rowIndex >= 0) {
            rowIndex--;
            return true;
        }
        return false;
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        Object[] row = rows.get(rowIndex);
        row[columnIndex - 1] = x;
    }

}
