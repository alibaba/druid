package com.alibaba.druid.pool.ha;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxyImpl;


public class MultiDataSourcePreparedStatement extends PreparedStatementProxyImpl {
    private MultiDataSourceConnection multiConnection;

    public MultiDataSourcePreparedStatement(MultiDataSourceConnection connection, PreparedStatement statement, String sql, long id){
        super(connection, statement, sql, id);
        
        multiConnection = connection;
    }
    
    public MultiDataSourceConnection getConnection() {
        return multiConnection;
    }
    
    @Override
    public boolean execute() throws SQLException {
        FilterChain chain = createChain();
        
        if (chain.getFilterSize() == 0) {
            firstResultSet = this.statement.execute();
        } else {
            firstResultSet = chain.preparedStatement_execute(this);    
        }
        
        return firstResultSet;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        FilterChain chain = createChain();
        
        if (chain.getFilterSize() == 0) {
            return this.statement.executeQuery();
        }
        
        return chain.preparedStatement_executeQuery(this);
    }

    @Override
    public int executeUpdate() throws SQLException {
        FilterChain chain = createChain();
        
        if (chain.getFilterSize() == 0) {
            return this.statement.executeUpdate();
        }
        
        return chain.preparedStatement_executeUpdate(this);
    }
}
