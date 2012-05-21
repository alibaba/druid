package com.alibaba.druid.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class ConnectionBase implements Connection {

    private boolean               autoCommit = true;
    private String                catalog;
    private int                   transactionIsolation;
    private int                   holdability;
    private Map<String, Class<?>> typeMap    = new HashMap<String, Class<?>>();
    private SQLWarning            warings;
    private boolean               readOnly;

    private String                url;
    private Properties            info;

    public ConnectionBase(String url, Properties info){
        this.url = url;
        this.info = info;
    }

    public String getUrl() {
        return url;
    }

    public Properties getConnectProperties() {
        return info;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return autoCommit;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.catalog = catalog;
    }

    @Override
    public String getCatalog() throws SQLException {
        return catalog;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.transactionIsolation = level;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return transactionIsolation;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return warings;
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.warings = null;
    }

    public void setWarings(SQLWarning warings) {
        this.warings = warings;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return typeMap;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.typeMap = map;
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.holdability = holdability;
    }

    @Override
    public int getHoldability() {
        return holdability;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return readOnly;
    }
}
