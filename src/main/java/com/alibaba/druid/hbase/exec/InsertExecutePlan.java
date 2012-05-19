package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import com.alibaba.druid.hbase.HBaseConnection;
import com.alibaba.druid.hbase.HBasePreparedStatement;
import com.alibaba.druid.sql.ast.SQLExpr;

public class InsertExecutePlan extends SingleTableExecutePlan {

    private Map<String, SQLExpr> columns = new LinkedHashMap<String, SQLExpr>();
    private byte[]               family  = Bytes.toBytes("d");

    @Override
    public void execute(HBasePreparedStatement statement) throws SQLException {
        try {
            HBaseConnection connection = statement.getConnection();
            HTableInterface htable = connection.getHTable(getTableName());

            Put put = new Put();
            for (int i = 0; i < columns.size(); ++i) {
//                String column = columns.get(i);
//                Object value = statement.getParameters().get(i);
//
//                byte[] qualifier = Bytes.toBytes(column);
//                byte[] bytes;
//
//                if (value instanceof String) {
//                    String strValue = (String) value;
//                    bytes = Bytes.toBytes(strValue);
//                } else {
//                    throw new SQLException("TODO"); // TODO
//                }
//
//                put.add(family, qualifier, bytes);
            }

            htable.put(put);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("executeQuery error", e);
        }
    }

    public Map<String, SQLExpr> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, SQLExpr> columns) {
        this.columns = columns;
    }

}
