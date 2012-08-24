/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
