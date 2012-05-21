package com.alibaba.druid.hdriver.impl.execute;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;

import com.alibaba.druid.hdriver.impl.HBaseConnectionImpl;
import com.alibaba.druid.hdriver.impl.HPreparedStatementImpl;
import com.alibaba.druid.hdriver.impl.mapping.HMapping;
import com.alibaba.druid.hdriver.impl.mapping.HMappingDefaultImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;

public class InsertExecutePlan extends SingleTableExecutePlan {

    private Map<String, SQLExpr> columns = new LinkedHashMap<String, SQLExpr>();

    @Override
    public boolean execute(HPreparedStatementImpl statement) throws SQLException {
        try {
            HMapping mapping = this.getMapping();
            if (mapping == null) {
                mapping = new HMappingDefaultImpl();
            }
            
            HBaseConnectionImpl connection = statement.getConnection();
            String dbType = connection.getConnectProperties().getProperty("dbType");

            Put put = null;
            for (Map.Entry<String, SQLExpr> entry : columns.entrySet()) {
                String column = entry.getKey();
                SQLExpr valueExpr = entry.getValue();

                Object value = SQLEvalVisitorUtils.eval(dbType, valueExpr, statement.getParameters());

                if (value == null) {
                    continue;
                }
                
                byte[] bytes = mapping.toBytes(column, value);

                if (mapping.isRow(column)) {
                    put = new Put(bytes);
                } else {
                    byte[] family = mapping.getFamily(column);
                    byte[] qualifier = mapping.getQualifier(column);
                    put.add(family, qualifier, bytes);
                }
            }

            HTableInterface htable = connection.getHTable(getTableName());
            htable.put(put);

            return false;
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
