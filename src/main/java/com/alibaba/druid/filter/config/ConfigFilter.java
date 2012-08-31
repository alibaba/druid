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
package com.alibaba.druid.filter.config;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.support.security.decryptor.DecryptException;
import com.alibaba.druid.support.security.decryptor.Decrypter;
import com.alibaba.druid.support.security.decryptor.DecrypterFactory;
import com.alibaba.druid.support.security.decryptor.SensitiveParameters;

import java.sql.SQLException;
import java.util.Properties;

/**
 * <pre>
 * 这个类主要是负责两个事情, 解密, 和下载远程的配置文件
 * [解密]
 *
 * DruidDataSource dataSource = new DruidDataSource();
 * //dataSource.setXXX 其他设置
 * //下面两步很重要
 * //启用config filter
 * dataSource.setFilters("config");
 * //使用RSA解密(使用默认密钥）
 * dataSource.setConnectionPropertise("config.decrypt=RSA");
 * dataSource.setPassword("加密的密文");
 *
 * [远程配置文件]
 * DruidDataSource dataSource = new DruidDataSource();
 * //下面两步很重要
 * //启用config filter
 * dataSource.setFilters("config");
 * //使用RSA解密(使用默认密钥）
 * dataSource.setConnectionPropertise("config.file=http://localhost:8080/remote.propreties;");
 *
 * [Spring的配置解密]
 *
 * &lt;bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close"&gt;
 *     &lt;property name="password" value="加密的密文" /&gt;
 *     &lt;!-- 其他的属性设置 --&gt;
 *     &lt;property name="filters" value="config" /&gt;
 *     &lt;property name="connectionProperties" value="config.decrypt=RSA" /&gt;
 * &lt;/bean&gt;
 *
 * [Spring的配置远程配置文件]
 *
 * &lt;bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close"&gt;
 *     &lt;property name="filters" value="config" /&gt;
 *     &lt;property name="connectionProperties" value="config.file=http://localhost:8080/remote.propreties; /&gt;
 * &lt;/bean&gt;
 *
 * [使用系统属性配置远程文件]
 * java -Ddruid.config.file=file:/home/test/my.properties ...
 *
 * 远程配置文件格式:
 * 1. 其他的属性KEY请查看 @see com.alibaba.druid.pool.DruidDataSourceFactory
 * 2. config filter 相关设置:
 * #远程文件路径
 * config.file=http://xxxxx(http://开头或者file:开头)
 *
 * #AES解密, key不指定, 使用默认的
 * config.decrypt=AES
 * config.decrypt.key=abcdfeg
 *
 * #RSA解密, Key不指定, 使用默认的
 * config.decrypt=RSA
 * config.decrypt.key=密钥字符串
 * config.decrypt.keyFile=密钥文件路径
 * config.decrypt.x509File=证书路径
 *
 * </pre>
 */
public class ConfigFilter extends FilterAdapter {

    private static Log             log                                = LogFactory.getLog(ConfigFilter.class);

    public static final String     CONFIG_FILE                        = "config.file";

    public static final String     CONFIG_DECRYPT                     = "config.decrypt";

    public static final String     SYS_PROP_CONFIG_FILE               = "druid.config.file";

    public ConfigFilter(){
    }

    public void init(DataSourceProxy dataSourceProxy) {
        if (!(dataSourceProxy instanceof DruidDataSource)) {
            log.error("ConfigLoader only support DruidDataSource");
        }

        Properties info = null;
        DruidDataSource dataSource = (DruidDataSource) dataSourceProxy;
        Properties connectinProperties = dataSource.getConnectProperties();

        //获取远程配置文件
        String protocol = connectinProperties.getProperty(CONFIG_FILE);

        //如果系统参数有指定
        if (protocol == null) {
            protocol = System.getProperty(SYS_PROP_CONFIG_FILE);
        }

        if (protocol != null && protocol.length() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("Config file will be load from [" + protocol + "].");
            }

            //获取配置文件内容
            ConfigLoader configLoader = ConfigLoaderFactory.getConfigLoader(protocol);

            if (configLoader == null) {
                throw new IllegalArgumentException("Druid doesn't support the [config.file=" + protocol + "] to load remote config file.");
            }

            info = configLoader.loadConfig(protocol);

            if (info == null) {
                throw new IllegalArgumentException("Cannot load remote config file from the [config.file=" + protocol + "].");
            }
        } else {
            info = new Properties();
            if (log.isDebugEnabled()) {
                log.debug("No remote config file.");
            }
        }

        //判断是否需要解密，如果需要就进行解密行动
        String decrypterId = info.getProperty(CONFIG_DECRYPT);
        boolean isRemotedSecurityConfig = true;

        if (decrypterId == null) {
            decrypterId = connectinProperties.getProperty(CONFIG_DECRYPT);
            isRemotedSecurityConfig = false;

            if (log.isDebugEnabled()) {
                log.debug("Get decrypt method " + decrypterId + " from local config.");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Get decrypt method " + decrypterId + " from remote config.");
            }
        }

        if (decrypterId != null) {
            if (log.isDebugEnabled()) {
                log.debug("Use [" + decrypterId + "] to decrypt sensitive parameters.");
            }

            try {
                Decrypter decrypter = DecrypterFactory.getDecrypter(decrypterId);
                if (decrypter == null) {
                    throw new IllegalArgumentException("Druid doesn't support the decrypter [config.decrypt=" + decrypterId + "].");
                }

                Properties securityInfo = isRemotedSecurityConfig
                        ? info
                        : connectinProperties;

                SensitiveParameters originalParameter = getSensitiveParameters(dataSource, info);
                SensitiveParameters parameters = decrypter.decrypt(originalParameter, securityInfo);

                info.setProperty(DruidDataSourceFactory.PROP_URL, parameters.getUrl());
                info.setProperty(DruidDataSourceFactory.PROP_USERNAME, parameters.getUsername());
                info.setProperty(DruidDataSourceFactory.PROP_PASSWORD, parameters.getPassword());
            } catch (DecryptException e) {
                throw new IllegalArgumentException("Failed to decrypt.", e);
            }

        } else {
            if (log.isDebugEnabled()) {
                log.debug("No decrypt.");
            }
        }

        //没有任何配置
        if (info.size() == 0) {
            if (log.isDebugEnabled()) {
                log.debug("No config.");
            }
            return;
        }

        try {
            DruidDataSourceFactory.config(dataSource, info);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Config DataSource error.", e);
        }
    }

    /**
     * 如果配置文件中没有值， 就取默认值
     * @param dataSource
     * @param info
     * @return
     */
    SensitiveParameters getSensitiveParameters(DruidDataSource dataSource, Properties info) {
        String username = info.getProperty(DruidDataSourceFactory.PROP_USERNAME);
        String password = info.getProperty(DruidDataSourceFactory.PROP_PASSWORD);
        String url = info.getProperty(DruidDataSourceFactory.PROP_URL);

        if (username == null) {
            username = dataSource.getUsername();
        }

        if (password == null) {
            password = dataSource.getPassword();
        }

        if (url == null) {
            url = dataSource.getUrl();
        }

        return new SensitiveParameters(url, username, password);
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj.getClass().equals(ConfigFilter.class);
    }
}
