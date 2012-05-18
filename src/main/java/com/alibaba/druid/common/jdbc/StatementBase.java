package com.alibaba.druid.common.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class StatementBase implements Statement {

    private Connection connection;
    private int        fetchDirection;
    private int        fetchSize;
    private int        resultSetType;
    private int        resultSetConcurrency;
    private int        resultSetHoldability;

    public StatementBase(Connection connection){
        super();
        this.connection = connection;
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    protected void checkOpen() throws SQLException {

    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        checkOpen();

        this.fetchDirection = direction;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        checkOpen();
        return fetchDirection;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        checkOpen();
        this.fetchSize = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        checkOpen();
        return fetchSize;
    }

    public int getResultSetType() throws SQLException {
        checkOpen();

        return resultSetType;
    }

    public void setResultSetType(int resultType) {
        this.resultSetType = resultType;
    }

    public void setResultSetConcurrency(int resultSetConcurrency) {
        this.resultSetConcurrency = resultSetConcurrency;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        checkOpen();
        return resultSetConcurrency;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        checkOpen();
        return resultSetHoldability;
    }

    public void setResultSetHoldability(int resultSetHoldability) {
        this.resultSetHoldability = resultSetHoldability;
    }
}
