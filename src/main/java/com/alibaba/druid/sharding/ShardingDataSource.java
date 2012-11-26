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
package com.alibaba.druid.sharding;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DataSourceAdapter;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.sharding.config.RouteConfig;
import com.alibaba.druid.sharding.sql.MySqlShardingVisitor;
import com.alibaba.druid.sharding.sql.ShardingVisitor;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.JdbcDataSourceStat;

public class ShardingDataSource extends DataSourceAdapter implements DataSource, DataSourceProxy {

    private final AtomicLong connectionIdSeed  = new AtomicLong();
    private final AtomicLong statementIdSeed   = new AtomicLong();
    private final AtomicLong resultSetIdSeed   = new AtomicLong();
    private final AtomicLong transactionIdSeed = new AtomicLong();
    private final Properties properties        = new Properties();
    private String           dbType;
    private List<Filter>     filters           = new CopyOnWriteArrayList<Filter>();

    private RouteConfig      routeConfig       = new RouteConfig();

    public Connection getConnectionByPartition(String partition) {
        throw new UnsupportedOperationException();
    }
    
    public Connection getConnectionByDb(String partition) {
        throw new UnsupportedOperationException();
    }

    public DruidPooledConnection getConnectionBySql(String partition) {
        throw new UnsupportedOperationException();
    }

    protected void afterConnectionClosed(ShardingConnection conn) {

    }
    
    public ShardingVisitor createShardingVisitor() {
        return createShardingVisitor(new ArrayList<Object>());
    }
    
    public SQLStatement parseStatement(String sql) throws SQLException {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        if (stmtList.size() > 0) {
            throw new SQLException("not support multi-statement");
        }
        
        return stmtList.get(0);
    }
    
    public ShardingVisitor createShardingVisitor(List<Object> parameters) {
        return new MySqlShardingVisitor(routeConfig, parameters);
    }

    public RouteConfig getRouteConfig() {
        return routeConfig;
    }

    public void setRouteConfig(RouteConfig routeConfig) {
        this.routeConfig = routeConfig;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public long createStatementId() {
        return statementIdSeed.incrementAndGet();
    }

    public long createConnectionId() {
        return connectionIdSeed.getAndIncrement();
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public JdbcDataSourceStat getDataSourceStat() {
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDbType() {
        return dbType;
    }

    @Override
    public Driver getRawDriver() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getRawJdbcUrl() {
        return null;
    }

    @Override
    public List<Filter> getProxyFilters() {
        return filters;
    }

    @Override
    public long createResultSetId() {
        return resultSetIdSeed.incrementAndGet();
    }

    @Override
    public long createTransactionId() {
        return transactionIdSeed.incrementAndGet();
    }

    @Override
    public Properties getConnectProperties() {
        return properties;
    }

}
