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
package com.alibaba.druid.proxy.jdbc;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class ClobProxyImpl implements ClobProxy {

    protected final Clob            clob;
    protected final ConnectionProxy connection;

    protected final DataSourceProxy dataSource;

    public ClobProxyImpl(DataSourceProxy dataSource, ConnectionProxy connection, Clob clob){
        if (clob == null) {
            throw new IllegalArgumentException("clob is null");
        }
        
        this.dataSource = dataSource;
        this.connection = connection;
        this.clob = clob;
    }

    public FilterChain createChain() {
        return new FilterChainImpl(dataSource);
    }

    public ConnectionProxy getConnectionWrapper() {
        return this.connection;
    }

    @Override
    public Clob getRawClob() {
        return clob;
    }

    @Override
    public void free() throws SQLException {
        createChain().clob_free(this);
    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        return createChain().clob_getAsciiStream(this);
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        return createChain().clob_getCharacterStream(this);
    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        return createChain().clob_getCharacterStream(this, pos, length);
    }

    @Override
    public String getSubString(long pos, int length) throws SQLException {
        return createChain().clob_getSubString(this, pos, length);
    }

    @Override
    public long length() throws SQLException {
        return createChain().clob_length(this);
    }

    @Override
    public long position(String searchstr, long start) throws SQLException {
        return createChain().clob_position(this, searchstr, start);
    }

    @Override
    public long position(Clob searchstr, long start) throws SQLException {
        return createChain().clob_position(this, searchstr, start);
    }

    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        return createChain().clob_setAsciiStream(this, pos);
    }

    @Override
    public Writer setCharacterStream(long pos) throws SQLException {
        return createChain().clob_setCharacterStream(this, pos);
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        return createChain().clob_setString(this, pos, str);
    }

    @Override
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        return createChain().clob_setString(this, pos, str, offset, len);
    }

    @Override
    public void truncate(long len) throws SQLException {
        createChain().clob_truncate(this, len);
    }

    public String toString() {
        return clob.toString();
    }

}
