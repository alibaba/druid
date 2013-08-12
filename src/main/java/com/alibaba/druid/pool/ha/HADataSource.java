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
package com.alibaba.druid.pool.ha;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.WrapperAdapter;

public class HADataSource extends WrapperAdapter implements DataSource {

    private int             loginTimeSeconds;

    protected PrintWriter   logWriter = new PrintWriter(System.out);

    private DruidDataSource master;
    private DruidDataSource second;

    public DruidDataSource getMaster() {
        return master;
    }

    public void setMaster(DruidDataSource master) {
        this.master = master;
    }

    public DruidDataSource getSecond() {
        return second;
    }

    public void setSecond(DruidDataSource second) {
        this.second = second;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeSeconds = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeSeconds;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

}
