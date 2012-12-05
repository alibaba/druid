package com.alibaba.druid.support.ibatis;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;

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

    public void setUserConnection(Connection connnection) throws SQLException {
        session.setUserConnection(connnection);
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
