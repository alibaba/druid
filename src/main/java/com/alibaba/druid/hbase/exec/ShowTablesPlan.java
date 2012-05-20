package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;
import java.sql.Statement;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import com.alibaba.druid.common.jdbc.ResultSetBase;
import com.alibaba.druid.hbase.HPreparedStatement;
import com.alibaba.druid.hbase.HResultSet;

public class ShowTablesPlan implements ExecutePlan {

    @Override
    public HResultSet executeQuery(HPreparedStatement statement) throws SQLException {
        try {
            HBaseAdmin admin = statement.getConnection().getEngine().getHBaseAdmin();

            HTableDescriptor[] tables = admin.listTables();

            return new ShowTableResultSet(statement, tables);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("executeQuery error", e);
        }
    }

    @Override
    public boolean execute(HPreparedStatement statement) throws SQLException {
        HResultSet resultSet = executeQuery(statement);
        statement.setResultSet(resultSet);
        return true;
    }

    public static class ShowTableResultSet extends ResultSetBase implements HResultSet {

        private final HTableDescriptor[] tables;
        private int                      rowIndex = -1;

        public ShowTableResultSet(Statement statement, HTableDescriptor[] tables){
            super(statement);
            this.tables = tables;
        }

        @Override
        public synchronized boolean next() throws SQLException {
            if (closed) {
                throw new SQLException();
            }

            if (rowIndex < tables.length - 1) {
                rowIndex++;
                return true;
            }
            return false;
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

        public Object getObject(String columnName) {
            HTableDescriptor table = tables[rowIndex];
            
            if ("name".equals(columnName)) {
                return table.getNameAsString();
            }
            
            if ("owner".equals(columnName)) {
                return table.getOwnerString();
            }

            if ("familys".equals(columnName)) {
                StringBuffer buf = new StringBuffer();
                for (HColumnDescriptor column : table.getFamilies()) {
                    if (buf.length() != 0) {
                        buf.append(',');
                    }
                    buf.append(column.getNameAsString());
                }
                return buf.toString();
            }
            
            return null;
        }

        @Override
        public void updateObject(int columnIndex, Object x) throws SQLException {
        }
    }
}
