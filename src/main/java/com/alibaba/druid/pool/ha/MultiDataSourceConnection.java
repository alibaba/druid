package com.alibaba.druid.pool.ha;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.druid.pool.WrapperAdapter;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxyImpl;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxyImpl;

public class MultiDataSourceConnection extends WrapperAdapter implements Connection, ConnectionProxy {

    private final MultiDataSource    haDataSource;

    private Connection            conn;

    private final int             id;

    private Boolean               autoCommit       = null;
    private Boolean               readOnly         = null;
    private String                catalog          = null;
    private Integer               transactionLeval = null;
    private Map<String, Class<?>> typeMap          = null;
    private Integer               holdability      = null;

    private Properties            clientInfo       = null;

    private Map<String, Object>   attributes       = null;

    private Date                  connectedTime    = null;

    public MultiDataSourceConnection(HADataSource haDataSource, int id){
        this.haDataSource = haDataSource;
        this.id = id;
    }
    
    public void checkConnection(String sql) throws SQLException {
        if (conn == null) {
            conn = haDataSource.getConnectionInternal(this, sql);
        }

        if (autoCommit != null) {
            conn.setAutoCommit(autoCommit);
        }

        if (readOnly != null) {
            conn.setReadOnly(readOnly);
        }

        if (catalog != null) {
            conn.setCatalog(catalog);
        }

        if (transactionLeval != null) {
            conn.setTransactionIsolation(transactionLeval);
        }

        if (typeMap != null) {
            conn.setTypeMap(typeMap);
        }

        if (holdability != null) {
            conn.setHoldability(holdability);
        }

        if (clientInfo != null) {
            conn.setClientInfo(clientInfo);
        }

        connectedTime = new Date();
    }

    public MultiDataSource getHaDataSource() {
        return haDataSource;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        checkConnection(sql);

        return conn.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(autoCommit);
        } else {
            this.autoCommit = Boolean.valueOf(autoCommit);
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        if (conn != null) {
            return conn.getAutoCommit();
        }

        if (this.autoCommit != null) {
            return this.autoCommit.booleanValue();
        }

        return false;
    }

    @Override
    public void commit() throws SQLException {
        if (conn != null) {
            conn.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        if (conn != null) {
            return conn.isClosed();
        }

        return false;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        if (conn != null) {
            return conn.getMetaData();
        }

        throw new SQLException("connection not init");
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        if (conn != null) {
            conn.setReadOnly(readOnly);
        } else {
            this.readOnly = Boolean.valueOf(readOnly);
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        if (conn != null) {
            return conn.isReadOnly();
        }

        if (this.readOnly != null) {
            return this.readOnly.booleanValue();
        }

        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        if (conn != null) {
            conn.setCatalog(catalog);
        } else {
            this.catalog = catalog;
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        if (conn != null) {
            return conn.getCatalog();
        }

        return this.catalog;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        if (conn != null) {
            conn.setTransactionIsolation(level);
        } else {
            this.transactionLeval = level;
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        if (conn != null) {
            return conn.getTransactionIsolation();
        }

        if (this.transactionLeval != null) {
            return this.transactionLeval;
        }

        return TRANSACTION_NONE;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        if (conn != null) {
            return conn.getWarnings();
        }

        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        if (conn != null) {
            conn.clearWarnings();
        }
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        if (conn != null) {
            return conn.getTypeMap();
        }

        return typeMap;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        if (conn != null) {
            conn.setTypeMap(map);
        } else {
            this.typeMap = map;
        }
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        if (conn != null) {
            conn.setHoldability(holdability);
        } else {
            this.holdability = holdability;
        }
    }

    @Override
    public int getHoldability() throws SQLException {
        if (conn != null) {
            return conn.getHoldability();
        }

        if (this.holdability != null) {
            return this.holdability;
        }

        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        if (conn != null) {
            return conn.setSavepoint();
        }

        throw new SQLException("connection not init");
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        if (conn != null) {
            return conn.setSavepoint(name);
        }

        throw new SQLException("connection not init");
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        if (conn != null) {
            conn.rollback(savepoint);
        }

        throw new SQLException("connection not init");
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        if (conn != null) {
            conn.releaseSavepoint(savepoint);
        }

        throw new SQLException("connection not init");
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        long stmtId = haDataSource.createStatementId();
        MultiDataSourceStatement stmt = new MultiDataSourceStatement(this, stmtId, resultSetType, resultSetConcurrency);
        return stmt;
    }

    @Override
    public Statement createStatement() throws SQLException {
        long stmtId = haDataSource.createStatementId();
        MultiDataSourceStatement stmt = new MultiDataSourceStatement(this, stmtId);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkConnection(sql);

        PreparedStatement stmt = conn.prepareStatement(sql);
        long stmtId = haDataSource.createStatementId();

        return new PreparedStatementProxyImpl(this, stmt, sql, stmtId);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        checkConnection(sql);

        CallableStatement stmt = conn.prepareCall(sql);
        long stmtId = haDataSource.createStatementId();

        return new CallableStatementProxyImpl(this, stmt, sql, stmtId);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkConnection(sql);

        PreparedStatement stmt = conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
        long stmtId = haDataSource.createStatementId();

        return new PreparedStatementProxyImpl(this, stmt, sql, stmtId);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkConnection(sql);

        CallableStatement stmt = conn.prepareCall(sql, resultSetType, resultSetConcurrency);
        long stmtId = haDataSource.createStatementId();

        return new CallableStatementProxyImpl(this, stmt, sql, stmtId);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        long stmtId = haDataSource.createStatementId();
        MultiDataSourceStatement stmt = new MultiDataSourceStatement(this, stmtId, resultSetType, resultSetConcurrency, resultSetHoldability);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkConnection(sql);

        PreparedStatement stmt = conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        long stmtId = haDataSource.createStatementId();

        return new PreparedStatementProxyImpl(this, stmt, sql, stmtId);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkConnection(sql);

        CallableStatement stmt = conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        long stmtId = haDataSource.createStatementId();

        return new CallableStatementProxyImpl(this, stmt, sql, stmtId);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        checkConnection(sql);

        PreparedStatement stmt = conn.prepareStatement(sql, autoGeneratedKeys);
        long stmtId = haDataSource.createStatementId();

        return new PreparedStatementProxyImpl(this, stmt, sql, stmtId);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        checkConnection(sql);

        PreparedStatement stmt = conn.prepareStatement(sql, columnIndexes);
        long stmtId = haDataSource.createStatementId();

        return new PreparedStatementProxyImpl(this, stmt, sql, stmtId);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        checkConnection(sql);

        PreparedStatement stmt = conn.prepareStatement(sql, columnNames);
        long stmtId = haDataSource.createStatementId();

        return new PreparedStatementProxyImpl(this, stmt, sql, stmtId);
    }

    @Override
    public Clob createClob() throws SQLException {
        if (conn != null) {
            return conn.createClob();
        }

        throw new SQLException("connection not init");
    }

    @Override
    public Blob createBlob() throws SQLException {
        if (conn != null) {
            return conn.createBlob();
        }

        throw new SQLException("connection not init");
    }

    @Override
    public NClob createNClob() throws SQLException {
        if (conn != null) {
            return conn.createNClob();
        }

        throw new SQLException("connection not init");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        if (conn != null) {
            return conn.createSQLXML();
        }

        throw new SQLException("connection not init");
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        if (conn != null) {
            return conn.isValid(timeout);
        }

        return true;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        if (conn != null) {
            conn.setClientInfo(name, value);
            return;
        }

        if (clientInfo == null) {
            clientInfo = new Properties();
        }
        clientInfo.setProperty(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        if (conn != null) {
            conn.setClientInfo(properties);
            return;
        }

        this.clientInfo = properties;
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        if (conn != null) {
            return conn.getClientInfo(name);
        }

        if (clientInfo == null) {
            return null;
        }

        return clientInfo.getProperty(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        if (conn != null) {
            return conn.getClientInfo();
        }

        return clientInfo;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        if (conn != null) {
            return conn.createArrayOf(typeName, elements);
        }

        throw new SQLException("connection not init");
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        if (conn != null) {
            return conn.createStruct(typeName, attributes);
        }

        throw new SQLException("connection not init");
    }

    // /////

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Object getRawObject() {
        return conn;
    }

    @Override
    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        return attributes;
    }

    @Override
    public Connection getConnectionRaw() {
        return conn;
    }

    @Override
    public Properties getProperties() {
        return haDataSource.getProperties();
    }

    @Override
    public DataSourceProxy getDirectDataSource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getConnectedTime() {
        return connectedTime;
    }

}
