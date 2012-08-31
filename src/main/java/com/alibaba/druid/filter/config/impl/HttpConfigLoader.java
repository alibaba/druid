package com.alibaba.druid.filter.config.impl;

import com.alibaba.druid.filter.config.ConfigLoader;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * <pre>
 * 通过HTTP方式获取资源文件.
 * DruidDataSource的connectionProperties必须是 <b>config.file=http://</b> 开头.
 * </pre>
 * @author Jonas Yang
 */
public class HttpConfigLoader implements ConfigLoader {
    private static      Log    LOG                                = LogFactory.getLog(HttpConfigLoader.class);

    public final static String PROTOCOL_PREFIX                    = "http://";

    @Override
    public String getId() {
        return PROTOCOL_PREFIX;
    }

    /**
     * 如果没有找到资源文件, 返回<code>null</code>
     * @param protocol
     * @return 如果没有找到资源文件, 返回<code>null</code>
     */
    public Properties loadConfig(String protocol) {
        if (!isSupported(protocol)) {
            throw new IllegalArgumentException("The protocol [" + protocol + "] is not accepted. Protocol must start with [" + PROTOCOL_PREFIX + "].");
        }

        if(LOG.isDebugEnabled()) {
            LOG.debug("Load config file[" + protocol + "] by [" + getId() + "]");
        }

        Properties properties = new Properties();

        InputStream inStream = null;
        try {
            URL url = getUrl(protocol);
            inStream = url.openStream();

            if (inStream == null) {
                LOG.error("load config file error, file : " + protocol);
                return null;
            }

            if (isXml(url)) {
                properties.loadFromXML(inStream);
            } else {
                properties.load(inStream);
            }

            return properties;
        } catch (Exception ex) {
            LOG.error("load config file error, file : " + protocol, ex);
            return null;
        } finally {
            JdbcUtils.close(inStream);
        }
    }

    URL getUrl(String protocol) throws MalformedURLException {
        return new URL(protocol.substring(PROTOCOL_PREFIX.length() - 7));
    }

    boolean isXml(URL url) {
        return url.getPath().endsWith(".xml");

    }

    @Override
    public boolean isSupported(String protocol) {
        if (protocol == null) {
            return false;
        }

        return protocol.toLowerCase().startsWith(PROTOCOL_PREFIX);
    }
}
