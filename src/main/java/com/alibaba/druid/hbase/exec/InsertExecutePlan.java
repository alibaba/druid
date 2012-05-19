package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;

import org.apache.hadoop.hbase.client.HTableInterface;

import com.alibaba.druid.hbase.HBaseConnection;
import com.alibaba.druid.hbase.HBasePreparedStatement;

public class InsertExecutePlan extends SingleTableExecutePlan {

    @Override
    public void execute(HBasePreparedStatement statement) throws SQLException {
        try {
            HBaseConnection connection = statement.getConnection();
            HTableInterface htable = connection.getHTable(getTableName());

            throw new UnsupportedOperationException();
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("executeQuery error", e);
        }
    }

}
