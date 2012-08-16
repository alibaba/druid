package com.alibaba.druid.util;

import java.util.Properties;

import javax.security.auth.callback.PasswordCallback;

public class DruidPasswordCallback extends PasswordCallback {

    private static final long serialVersionUID = 1L;

    private String            url;

    private Properties        properties;

    public DruidPasswordCallback(){
        this("druidDataSouce password", false);
    }

    public DruidPasswordCallback(String prompt, boolean echoOn){
        super(prompt, echoOn);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
