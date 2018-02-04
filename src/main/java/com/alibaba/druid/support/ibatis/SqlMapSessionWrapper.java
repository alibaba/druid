/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("deprecation")
public class SqlMapSessionWrapper extends SqlMapExecutorWrapper implements SqlMapSession {
	private SqlMapSession session;
	
    public SqlMapSessionWrapper(ExtendedSqlMapClient client, SqlMapSession session){
        super(client, session);
        this.session = session;
    }
    
	public void startTransaction() throws SQLException {
        session.startTransaction();
    }

    public void startTransaction(int transactionIsolation) throws SQLException {
        session.startTransaction(transactionIsolation);
    }

    public void commitTransaction() throws SQLException {
        session.commitTransaction();
    }

    public void endTransaction() throws SQLException {
        session.endTransaction();
    }

    public void setUserConnection(Connection connection) throws SQLException {
        session.setUserConnection(connection);
    }

    @Deprecated
    public Connection getUserConnection() throws SQLException {
        return session.getUserConnection();
    }

    public Connection getCurrentConnection() throws SQLException {
        return session.getCurrentConnection();
    }

    public DataSource getDataSource() {
        return session.getDataSource();
    }

    public void close() {
        session.close();
    }
}
