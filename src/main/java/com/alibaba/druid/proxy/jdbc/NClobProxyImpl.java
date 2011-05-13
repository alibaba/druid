package com.alibaba.druid.proxy.jdbc;

import java.sql.NClob;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;

public class NClobProxyImpl extends ClobProxyImpl implements NClobProxy {

    private final NClob nclob;

    public NClobProxyImpl(DataSourceProxy dataSource, ConnectionProxy connection, NClob clob){
        super(dataSource, connection, clob);
        this.nclob = clob;
    }

    public FilterChain createChain() {
        return new FilterChainImpl(dataSource);
    }

    @Override
    public NClob getRawNClob() {
        return nclob;
    }
}
