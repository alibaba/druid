package com.alibaba.druid.filter.url;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * FilterChain to create new connection with placeholder in URL
 *
 * @author DigitalSonic
 */
public class ConnectionConnectFilterChainImpl extends FilterChainImpl {
    private final static Log LOG = LogFactory.getLog(ConnectionConnectFilterChainImpl.class);

    public ConnectionConnectFilterChainImpl(FilterChain filterChain) {
        this(filterChain.getDataSource(), filterChain.getPos());
    }

    public ConnectionConnectFilterChainImpl(DataSourceProxy dataSource, int pos) {
        super(dataSource, pos);
    }

    @Override
    public ConnectionProxy connection_connect(Properties info) throws SQLException {
        if (this.pos < getFilterSize()) {
            return getFilters().get(pos++).connection_connect(this, info);
        }

        DataSourceProxy dataSource = getDataSource();
        Driver driver = dataSource.getRawDriver();
        String url = dataSource.getRawJdbcUrl();

        url = processUrl(url);

        Connection nativeConnection = driver.connect(url, info);

        if (nativeConnection == null) {
            return null;
        }

        return new ConnectionProxyImpl(dataSource, nativeConnection, info, dataSource.createConnectionId());
    }

    protected String processUrl(String url) {
        if (url == null || (url != null &&
                (url.indexOf("${") == -1 || url.indexOf("}") == -1))) {
            return url;
        }

        String processedUrl = url;
        String placeHolder = url.substring(url.indexOf("${") + 2, url.indexOf("}"));
        String value = HostAndPortHolder.getInstance().get(placeHolder);
        if (value != null) {
            LOG.info("PlaceHolder ${" + placeHolder + "} is found in url, it will be replace by " + value);
            processedUrl = url.replace("${" + placeHolder + "}", value);
        } else {
            LOG.info("PlaceHolder ${" + placeHolder + "} is found in url, but no value is bind.");
        }
        return processedUrl;
    }
}
