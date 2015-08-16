/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.ibatis;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.mapping.result.ResultObjectFactory;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("deprecation")
public class SqlMapClientWrapper extends SqlMapExecutorWrapper implements SqlMapClient, ExtendedSqlMapClient {

    protected final ExtendedSqlMapClient client;

    public SqlMapClientWrapper(ExtendedSqlMapClient client){
        super(client, client);
        this.client = client;
         
    }
    
    public ExtendedSqlMapClient getClient() {
        return this.client;
    }

    public void startTransaction() throws SQLException {
        client.startTransaction();
    }

    public void startTransaction(int transactionIsolation) throws SQLException {
        client.startTransaction(transactionIsolation);
    }

    public void commitTransaction() throws SQLException {
        client.commitTransaction();
    }

    public void endTransaction() throws SQLException {
        client.endTransaction();
    }

    public void setUserConnection(Connection connection) throws SQLException {
        client.setUserConnection(connection);
    }

    public Connection getUserConnection() throws SQLException {
        return client.getUserConnection();
    }

    public Connection getCurrentConnection() throws SQLException {
        return client.getCurrentConnection();
    }

    public DataSource getDataSource() {
        return client.getDataSource();
    }

    public SqlMapSession openSession() {
        SqlMapSession session = client.openSession();
        IbatisUtils.setClientImpl(session, clientImplWrapper);
        return new SqlMapSessionWrapper(client, session);
    }

    public SqlMapSession openSession(Connection conn) {
        SqlMapSession session = client.openSession(conn);
        IbatisUtils.setClientImpl(session, clientImplWrapper);
        return new SqlMapSessionWrapper(client, session);
    }

    public SqlMapSession getSession() {
        SqlMapSession session = client.getSession();
        IbatisUtils.setClientImpl(session, clientImplWrapper);
        return new SqlMapSessionWrapper(client, session);
    }

    public void flushDataCache() {
        client.flushDataCache();
    }

    public void flushDataCache(String cacheId) {
        client.flushDataCache(cacheId);
    }

    public MappedStatement getMappedStatement(String id) {
        return client.getMappedStatement(id);
    }

    public boolean isLazyLoadingEnabled() {
        return client.isLazyLoadingEnabled();
    }

    public boolean isEnhancementEnabled() {
        return client.isEnhancementEnabled();
    }

    public SqlExecutor getSqlExecutor() {
        return client.getSqlExecutor();
    }

    public SqlMapExecutorDelegate getDelegate() {
        return client.getDelegate();
    }

    public ResultObjectFactory getResultObjectFactory() {
        return client.getResultObjectFactory();
    }
}
