package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import com.alibaba.druid.hbase.HBaseConnection;
import com.alibaba.druid.hbase.HBasePreparedStatement;
import com.alibaba.druid.hbase.HBaseResultSet;
import com.alibaba.druid.sql.ast.SQLExpr;

public class SingleTableQueryExecutePlan extends SingleTableExecutePlan {

    private List<String>  columeNames = new ArrayList<String>();
    private List<SQLExpr> conditions  = new ArrayList<SQLExpr>();

    public List<SQLExpr> getConditions() {
        return conditions;
    }

    public void setConditions(List<SQLExpr> conditions) {
        this.conditions = conditions;
    }

    public SingleTableQueryExecutePlan(){

    }

    public List<String> getColumeNames() {
        return columeNames;
    }

    @Override
    public HBaseResultSet executeQuery(HBasePreparedStatement statement) throws SQLException {
        try {
            HBaseConnection connection = statement.getConnection();
            HTableInterface htable = connection.getHTable(getTableName());

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
