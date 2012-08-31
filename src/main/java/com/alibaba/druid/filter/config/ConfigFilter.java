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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.Properties;

import javax.crypto.Cipher;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.Base64;
import com.alibaba.druid.util.JdbcUtils;

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
 * #RSA解密, Key不指定, 使用默认的
 * config.decrypt=RSA
 * config.decrypt.key=密钥字符串
 * config.decrypt.keyFile=密钥文件路径
 * config.decrypt.x509File=证书路径
 * 
 * </pre>
 * @author Jonas Yang
 */
public class ConfigFilter extends FilterAdapter {

    private static Log         LOG                       = LogFactory.getLog(ConfigFilter.class);

    public static final String CONFIG_FILE               = "config.file";

    public static final String CONFIG_DECRYPT            = "config.decrypt";

    public static final String SYS_PROP_CONFIG_FILE      = "druid.config.file";

    public static final String KEY                       = "config.decrypt.key";
    public static final String KEY_FILE                  = "config.decrypt.keyFile";
    public static final String X509_FILE                 = "config.decrypt.x509File";

    public static final String DEFAULT_PUBLIC_KEY_STRING = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKHGwq7q2RmwuRgKxBypQHw0mYu4BQZ3eMsTrdK8E6igRcxsobUC7uT0SoxIjl1WveWniCASejoQtn/BY6hVKWsCAwEAAQ==";

    public ConfigFilter(){
    }

    public void init(DataSourceProxy dataSourceProxy) {
        if (!(dataSourceProxy instanceof DruidDataSource)) {
            LOG.error("ConfigLoader only support DruidDataSource");
        }

        DruidDataSource dataSource = (DruidDataSource) dataSourceProxy;
        Properties connectinProperties = dataSource.getConnectProperties();

        Properties configFileProperties = loadPropertyFromConfigFile(connectinProperties);

        // 判断是否需要解密，如果需要就进行解密行动
        boolean decrypt = isDecrypt(connectinProperties, configFileProperties);

        if (configFileProperties == null) {
            if (decrypt) {
                decrypt(dataSource, null);
            }
            return;
        }

        if (decrypt) {
            decrypt(dataSource, configFileProperties);
        }

        try {
            DruidDataSourceFactory.config(dataSource, configFileProperties);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Config DataSource error.", e);
        }
    }

    public boolean isDecrypt(Properties connectinProperties, Properties configFileProperties) {
        boolean decrypt = false;

        String decrypterId = connectinProperties.getProperty(CONFIG_DECRYPT);
        if (configFileProperties != null && (decrypterId == null || decrypterId.length() == 0)) {
            decrypterId = configFileProperties.getProperty(CONFIG_DECRYPT);
        }

        if ("true".equals(decrypterId)) {
            decrypt = true;
        }

        return decrypt;
    }

    Properties loadPropertyFromConfigFile(Properties connectinProperties) {
        String configFile = connectinProperties.getProperty(CONFIG_FILE);

        if (configFile == null) {
            configFile = System.getProperty(SYS_PROP_CONFIG_FILE);
        }

        if (configFile != null && configFile.length() > 0) {
            if (LOG.isInfoEnabled()) {
                LOG.info("DruidDataSource Config File load from : " + configFile);
            }

            Properties info = loadConfig(configFile);

            if (info == null) {
                throw new IllegalArgumentException("Cannot load remote config file from the [config.file=" + configFile
                                                   + "].");
            }

            return info;
        }

        return null;
    }

    public void decrypt(DruidDataSource dataSource, Properties info) {

        try {
            String encryptedPassword = null;
            if (info != null) {
                encryptedPassword = info.getProperty(DruidDataSourceFactory.PROP_PASSWORD);
            }

            if (encryptedPassword == null || encryptedPassword.length() == 0) {
                encryptedPassword = dataSource.getConnectProperties().getProperty(DruidDataSourceFactory.PROP_PASSWORD);
            }

            if (encryptedPassword == null || encryptedPassword.length() == 0) {
                encryptedPassword = dataSource.getPassword();
            }

            PublicKey publicKey = getPublicKey(dataSource.getConnectProperties(), info);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            String passwordPlainText = decrypt(cipher, encryptedPassword);

            if (info != null) {
                info.setProperty(DruidDataSourceFactory.PROP_PASSWORD, passwordPlainText);
            } else {
                dataSource.setPassword(passwordPlainText);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decrypt.", e);
        }
    }

    protected String decrypt(Cipher cipher, String cipherString) throws Exception {
        if (cipherString == null || cipherString.length() == 0) {
            return cipherString;
        }

        byte[] cipherBytes = Base64.base64ToByteArray(cipherString);
        byte[] plainBytes = cipher.doFinal(cipherBytes);

        return new String(plainBytes);
    }

    public PublicKey getPublicKey(Properties connectinProperties, Properties configFileProperties) {
        String key = connectinProperties.getProperty(KEY);
        String publicKeyFile = connectinProperties.getProperty(KEY_FILE);
        String x509File = connectinProperties.getProperty(X509_FILE);

        if (connectinProperties != null && (key == null || key.length() == 0)) {
            key = connectinProperties.getProperty(KEY);
        }

        if (connectinProperties != null && (publicKeyFile == null || publicKeyFile.length() == 0)) {
            publicKeyFile = connectinProperties.getProperty(KEY_FILE);
        }

        if (connectinProperties != null && (x509File == null || x509File.length() == 0)) {
            x509File = connectinProperties.getProperty(X509_FILE);
        }

        if (publicKeyFile != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Decrypt by public key file");
            }
            return getPublicKeyByPublicKeyFile(publicKeyFile);
        }

        if (x509File != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Decrypt by X509 file");
            }
            return getPublicKeyByX509(x509File);
        }

        return getPublicKeyByString(key);
    }

    public PublicKey getPublicKeyByX509(String x509File) {
        if (x509File == null || x509File.length() == 0) {
            return getPublicKeyByString(null);
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(x509File);

            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate cer = factory.generateCertificate(in);
            return cer.getPublicKey();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        } finally {
            JdbcUtils.close(in);
        }
    }

    public PublicKey getPublicKeyByPublicKeyFile(String publicKeyFile) {
        if (publicKeyFile == null || publicKeyFile.length() == 0) {
            return getPublicKeyByString(null);
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(publicKeyFile);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len = 0;
            byte[] b = new byte[512 / 8];
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }

            byte[] publicKeyBytes = out.toByteArray();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        } finally {
            JdbcUtils.close(in);
        }
    }

    public PublicKey getPublicKeyByString(String publicKeyString) {
        if (publicKeyString == null || publicKeyString.length() == 0) {
            publicKeyString = DEFAULT_PUBLIC_KEY_STRING;
        }

        try {
            byte[] publicKeyBytes = Base64.base64ToByteArray(publicKeyString);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        }
    }

    public Properties loadConfig(String filePath) {
        Properties properties = new Properties();

        InputStream inStream = null;
        try {
            boolean xml = false;
            if (filePath.startsWith("http://") || filePath.startsWith("https://") || filePath.startsWith("file:/")) {
                URL url = new URL(filePath);
                inStream = url.openStream();

                xml = url.getPath().endsWith(".xml");
            } else {
                File file = new File(filePath);
                if (file.exists()) {
                    inStream = new FileInputStream(file);
                } else {
                    inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
                }

                xml = filePath.endsWith(".xml");
            }

            if (inStream == null) {
                LOG.error("load config file error, file : " + filePath);
                return null;
            }

            if (xml) {
                properties.loadFromXML(inStream);
            } else {
                properties.load(inStream);
            }

            return properties;
        } catch (Exception ex) {
            LOG.error("load config file error, file : " + filePath, ex);
            return null;
        } finally {
            JdbcUtils.close(inStream);
        }
    }
}
