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
        Object obj = row[columnIndex - 1];
        return obj;
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
