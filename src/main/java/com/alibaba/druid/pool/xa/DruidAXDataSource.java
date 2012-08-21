package com.alibaba.druid.pool.xa;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAException;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.MySqlUtils;
import com.alibaba.druid.util.OracleUtils;
import com.alibaba.druid.util.PGUtils;

public class DruidAXDataSource extends DruidDataSource implements XADataSource {

    private final static Log  LOG              = LogFactory.getLog(DruidAXDataSource.class);

    private static final long serialVersionUID = 1L;

    @Override
    public XAConnection getXAConnection() throws SQLException {
        DruidPooledConnection conn = this.getConnection();

        Connection physicalConn = conn.unwrap(Connection.class);

        XAConnection rawXAConnection;
        if (JdbcUtils.ORACLE.equals(dbType)) {
            try {
                rawXAConnection = OracleUtils.OracleXAConnection(physicalConn);
            } catch (XAException xae) {
                LOG.error("create xaConnection error", xae);
                return null;
            }
        } else if (JdbcUtils.MYSQL.equals(dbType)) {
            rawXAConnection = MySqlUtils.createXAConnection(physicalConn);
        } else if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            rawXAConnection = PGUtils.createXAConnection(physicalConn);
        } else {
            throw new SQLException("xa not support dbType : " + this.dbType);
        }

        return new DruidPooledXAConnection(conn, rawXAConnection);
    }

    @Override
    public XAConnection getXAConnection(String user, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported by DruidDataSource");
    }

}
