package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import com.alibaba.druid.hbase.HBaseConnection;
import com.alibaba.druid.hbase.HBasePreparedStatement;
import com.alibaba.druid.hbase.HBaseResultSet;

public class SingleTableQueryExecutePlan implements ExecutePlan {

    private String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public HBaseResultSet executeScan(HBasePreparedStatement statement) throws SQLException {
        try {
            HBaseConnection connection = statement.getConnection();
            HTableInterface htable = connection.getHTable(tableName);
            
            Scan scan = new Scan();
            ResultScanner scanner = htable.getScanner(scan);
            
            return new HBaseResultSet(statement, htable, scanner);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("executeQuery error", e);
        }
    }

}
