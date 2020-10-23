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
package com.alibaba.druid.pool.xa;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAException;

import com.alibaba.druid.DbType;
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

        XAConnection rawXAConnection = createPhysicalXAConnection(physicalConn);

        return new DruidPooledXAConnection(conn, rawXAConnection);
    }

    protected void initCheck() throws SQLException {
        super.initCheck();

        DbType dbType = DbType.of(this.dbTypeName);
        if (JdbcUtils.H2.equals(dbType)) {
            h2Factory = H2Utils.createJdbcDataSourceFactory();
        }
    }

    private XAConnection createPhysicalXAConnection(Connection physicalConn) throws SQLException {
        DbType dbType = DbType.of(this.dbTypeName);

        if (dbType == null) {
            throw new SQLException("xa not support dbType : " + this.dbTypeName);
        }

        switch (dbType) {
            case oracle:
                try {
                    return OracleUtils.OracleXAConnection(physicalConn);
                } catch (XAException xae) {
                    LOG.error("create xaConnection error", xae);
                    return null;
                }
            case mysql:
            case mariadb:
                return MySqlUtils.createXAConnection(driver, physicalConn);
            case postgresql:
                return PGUtils.createXAConnection(physicalConn);
            case h2:
                return H2Utils.createXAConnection(h2Factory, physicalConn);
            case jtds:
                return new JtdsXAConnection(physicalConn);
            default:
                throw new SQLException("xa not support dbType : " + this.dbTypeName);

        }
    }

    @Override
    public XAConnection getXAConnection(String user, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported by DruidDataSource");
    }

}
