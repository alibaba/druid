package com.alibaba.druid.hdriver.impl.execute;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import com.alibaba.druid.hdriver.HResultSet;
import com.alibaba.druid.hdriver.impl.jdbc.HPreparedStatementImpl;
import com.alibaba.druid.hdriver.impl.jdbc.HResultSetMetaData;
import com.alibaba.druid.util.jdbc.ResultSetBase;
import com.alibaba.druid.util.jdbc.ResultSetMetaDataBase.ColumnMetaData;

public class ShowTablesPlan implements ExecutePlan {

    @Override
    public HResultSet executeQuery(HPreparedStatementImpl statement) throws SQLException {
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
    public boolean execute(HPreparedStatementImpl statement) throws SQLException {
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

            HResultSetMetaData metaData = new HResultSetMetaData();
            {
                ColumnMetaData column = new ColumnMetaData();
                column.setColumnName("name");
                column.setColumnType(Types.VARCHAR);
                column.setColumnTypeName("VARCHAR");
                metaData.getColumns().add(column);
            }
            {
                ColumnMetaData column = new ColumnMetaData();
                column.setColumnName("owner");
                column.setColumnType(Types.VARCHAR);
                column.setColumnTypeName("VARCHAR");
                metaData.getColumns().add(column);
            }
            {
                ColumnMetaData column = new ColumnMetaData();
                column.setColumnName("familys");
                column.setColumnType(Types.VARCHAR);
                column.setColumnTypeName("VARCHAR");
                metaData.getColumns().add(column);
            }
            {
                ColumnMetaData column = new ColumnMetaData();
                column.setColumnName("maxFileSize");
                column.setColumnType(Types.BIGINT);
                column.setColumnTypeName("long");
                metaData.getColumns().add(column);
            }
            {
                ColumnMetaData column = new ColumnMetaData();
                column.setColumnName("memStoreFlushSize");
                column.setColumnType(Types.BIGINT);
                column.setColumnTypeName("long");
                metaData.getColumns().add(column);
            }
            {
                ColumnMetaData column = new ColumnMetaData();
                column.setColumnName("regionSplitPolicyClassName");
                column.setColumnType(Types.VARCHAR);
                column.setColumnTypeName("VARCHAR");
                metaData.getColumns().add(column);
            }
            this.metaData = metaData;
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

        public String getString(String columnName) {
            return (String) getObject(columnName);
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

            if ("maxFileSize".equals(columnName)) {
                return table.getMaxFileSize();
            }
            
            if ("memStoreFlushSize".equals(columnName)) {
                return table.getMemStoreFlushSize();
            }
            
            if ("regionSplitPolicyClassName".equals(columnName)) {
                return table.getRegionSplitPolicyClassName();
            }
            
            return null;
        }

        @Override
        public int findColumn(String columnName) throws SQLException {
            HResultSetMetaData meta = (HResultSetMetaData) this.metaData;
            return meta.findColumn(columnName);
        }

        @Override
        public void updateObject(int columnIndex, Object x) throws SQLException {
        }
    }
}
