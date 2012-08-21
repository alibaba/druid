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
import com.alibaba.druid.util.H2Utils;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.MySqlUtils;
import com.alibaba.druid.util.OracleUtils;
import com.alibaba.druid.util.PGUtils;

public class DruidXADataSource extends DruidDataSource implements XADataSource {

    private final static Log  LOG              = LogFactory.getLog(DruidXADataSource.class);

    private static final long serialVersionUID = 1L;

    private Object            h2Factory        = null;

    @Override
    public XAConnection getXAConnection() throws SQLException {
        DruidPooledConnection conn = this.getConnection();

        Connection physicalConn = conn.unwrap(Connection.class);

        XAConnection rawXAConnection = createPhysicalXAConnetion(physicalConn);

        return new DruidPooledXAConnection(conn, rawXAConnection);
    }

    protected void initCheck() throws SQLException {
        super.initCheck();

        if (JdbcUtils.H2.equals(this.dbType)) {
            h2Factory = H2Utils.createJdbcDataSourceFactory();
        }
    }

    private XAConnection createPhysicalXAConnetion(Connection physicalConn) throws SQLException {
        if (JdbcUtils.ORACLE.equals(dbType)) {
            try {
                return OracleUtils.OracleXAConnection(physicalConn);
            } catch (XAException xae) {
                LOG.error("create xaConnection error", xae);
                return null;
            }
        }

        if (JdbcUtils.MYSQL.equals(dbType)) {
            return MySqlUtils.createXAConnection(physicalConn);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return PGUtils.createXAConnection(physicalConn);
        }

        if (JdbcUtils.H2.equals(dbType)) {
            return H2Utils.createXAConnection(h2Factory, physicalConn);
        }

        if (JdbcUtils.JTDS.equals(dbType)) {
            return new JtdsXAConnection(physicalConn);
        }

        throw new SQLException("xa not support dbType : " + this.dbType);
    }

    @Override
    public XAConnection getXAConnection(String user, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported by DruidDataSource");
    }

}
