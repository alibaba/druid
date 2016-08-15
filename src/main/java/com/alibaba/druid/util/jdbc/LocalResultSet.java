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
package com.alibaba.druid.util.jdbc;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LocalResultSet extends ResultSetBase {

    private int            rowIndex = -1;
    private List<Object[]> rows     = new ArrayList<Object[]>();

    public LocalResultSet(Statement statement){
        super(statement);
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
    
    public Object getObjectInternal(int columnIndex) {
        Object[] row = rows.get(rowIndex);
        return row[columnIndex - 1];
    }

    @Override
    public synchronized boolean previous() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        if (rowIndex > 0) {
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
