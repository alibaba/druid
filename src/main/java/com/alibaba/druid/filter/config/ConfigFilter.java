package com.alibaba.druid.filter.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
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

public class ConfigFilter extends FilterAdapter {

    public final static String URL_PREFIX                         = "druid-configFile=";
    public final static String SYS_PROP_CONFIG_FILE               = "druid.config.file";
    public final static String SYS_PROP_CONFIG_KEY                = "druid.config.key";
    public final static String SYS_PROP_CONFIG_ENCRYPTED_PASSWORD = "druid.config.encryptedPassword";
    public final static String DEFAULT_ALGORITHM                  = "RSA";

    private final static Log   LOG                                = LogFactory.getLog(ConfigFilter.class);

    private String             file;

    private Cipher             cipher;
    private String             algorithm;
    private String             key;
    private String             encryptedPassword;

    public ConfigFilter(){
        this.setAlgorithm(DEFAULT_ALGORITHM);

        {
            String property = System.getProperty(SYS_PROP_CONFIG_KEY);
            if (property != null && property.length() != 0) {
                this.setKey(property);
            }
        }
        {
            String property = System.getProperty(SYS_PROP_CONFIG_ENCRYPTED_PASSWORD);
            if (property != null && property.length() != 0) {
                this.setEncryptedPassword(property);
            }
        }
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        try {
            this.cipher = Cipher.getInstance(algorithm);
        } catch (Exception e) {
            throw new IllegalArgumentException("illegal algorithm", e);
        }
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;

        byte[] bytes = Base64.base64ToByteArray(key);

        try {
            if (cipher.getAlgorithm().startsWith("RSA")) {
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
                cipher.init(Cipher.DECRYPT_MODE, publicKey);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("illegal key", e);
        }
    }

    public void init(DataSourceProxy dataSourceProxy) {
        if (!(dataSourceProxy instanceof DruidDataSource)) {
            LOG.error("ConfigLoader only support DruidDataSource");
        }

        DruidDataSource dataSource = (DruidDataSource) dataSourceProxy;

        Properties properties = loadConfig(dataSource);

        if (encryptedPassword != null) {
            try {
                byte[] passwordBytes = Base64.base64ToByteArray(encryptedPassword);
                byte[] decryptedBytes = cipher.doFinal(passwordBytes);
                String decryptedPassword = new String(decryptedBytes, "ISO-8859-1");
                dataSource.setPassword(decryptedPassword);
            } catch (Exception e) {
                LOG.error("decrypt password error", e);
            }
        }

        if (properties == null) {
            if (encryptedPassword == null) {
                LOG.error("load config error, return null");
            }
            return;
        }

        try {
            if (this.cipher != null) {
                String password = properties.getProperty(DruidDataSourceFactory.PROP_PASSWORD);
                if (password != null && password.length() != 0) {
                    byte[] passwordBytes = Base64.base64ToByteArray(password);
                    byte[] decryptedBytes = cipher.doFinal(passwordBytes);

                    String decryptedPassword = new String(decryptedBytes, "ISO-8859-1");
                    properties.put(DruidDataSourceFactory.PROP_PASSWORD, decryptedPassword);
                }
            }
        } catch (Exception e) {
            LOG.error("decrypt password error", e);
        }

        try {
            DruidDataSourceFactory.config(dataSource, properties);
        } catch (Exception e) {
            LOG.error("config dataSource error", e);
        }
    }

    public Properties loadConfig(DruidDataSource dataSource) {
        String filePath = this.file;

        // jdbc:druid-config:
        if (file == null && dataSource.getUrl() != null && dataSource.getUrl().startsWith(ConfigFilter.URL_PREFIX)) {
            filePath = dataSource.getUrl().substring(ConfigFilter.URL_PREFIX.length());
        }

        if (filePath == null) {
            filePath = System.getProperty(SYS_PROP_CONFIG_FILE);
        }

        if (filePath == null) {
            if (this.encryptedPassword == null) {
                LOG.error("load config error, file is null");
            }
            return null;
        }

        Properties properties = new Properties();

        InputStream inStream = null;
        try {
            boolean xml = false;
            if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }

}
