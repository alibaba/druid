package com.alibaba.druid.pool.ha.config;

import java.net.URL;

public class URLConnectionConfigLoader {

    private final URL configServerURL;

    public URLConnectionConfigLoader(URL configServerURL){
        this.configServerURL = configServerURL;
    }

    public URL getConfigServerURL() {
        return configServerURL;
    }

}
