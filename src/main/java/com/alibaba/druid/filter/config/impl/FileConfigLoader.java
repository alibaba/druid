package com.alibaba.druid.filter.config.impl;

import com.alibaba.druid.filter.config.ConfigLoader;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * <pre>
 * 通过指定的配置文件路径, 加载该配置. 使用使用NFS共享文件等情况.
 * DruidDataSource的connectionProperties必须是 <b>config.file=file:</b> 开头.
 * 注意: OSGI下的跨bundle获取资源, 没有测试过
 * </pre>
 * @author Jonas Yang
 */
public class FileConfigLoader implements ConfigLoader {

    private static      Log   LOG                                 = LogFactory.getLog(FileConfigLoader.class);

    public static final String PROTOCOL_PREFIX                    = "file:";
    public static final String SYS_PROP_CONFIG_FILE               = "druid.config.file";

    @Override
    public String getId() {
        return PROTOCOL_PREFIX;
    }

    /**
     * 如果没有找到配置文件, 返回<code>null</code>
     * @param protocol
     * @return 如果有找到配置文件返回<code>null</code>
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
            String filePath = getFilePath(protocol);
            File file = new File(filePath);

            if (file.exists()) {
                inStream = new FileInputStream(file);
            } else {
                inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
            }

            if (inStream == null) {
                LOG.error("load config file error, file : " + protocol);
                return null;
            }

            if (isXml(filePath)) {
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

    String getFilePath(String protocol) {
        String filePath = null;

        if(protocol == null) {
            filePath = System.getProperty(SYS_PROP_CONFIG_FILE);
        } else {
            filePath = protocol.substring(PROTOCOL_PREFIX.length());
        }

        return filePath;
    }

    boolean isXml(String filePath) {
        return filePath.endsWith(".xml");
    }

    @Override
    public boolean isSupported(String protocol) {
        if (protocol == null) {
            return System.getProperty(SYS_PROP_CONFIG_FILE) != null;
        }

        return protocol.toLowerCase().startsWith(PROTOCOL_PREFIX);
    }
}
