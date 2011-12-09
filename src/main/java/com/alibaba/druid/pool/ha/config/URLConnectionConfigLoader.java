package com.alibaba.druid.pool.ha.config;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson.JSON;

public class URLConnectionConfigLoader implements ConfigLoader {

    private URL url;
    private int connectTimeout = 1000 * 3;
    private int readTimeout;

    public URLConnectionConfigLoader(URL configServerURL){
        this.url = configServerURL;
    }

    public URLConnectionConfigLoader(String url) throws MalformedURLException{
        this.url = new URL(url);
    }

    public URL getURL() {
        return url;
    }

    public void setURL(URL url) {
        this.url = url;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public MultiDataSourceConfig load() {
        if (url == null) {
            throw new IllegalStateException("configServerURL is null");
        }

        String responseMessage;

        {
            HttpURLConnection conn = null;
            Reader reader = null;
            try {
                conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);

                conn.connect();

                reader = new InputStreamReader(conn.getInputStream());
                responseMessage = JdbcUtils.read(reader);
            } catch (Exception e) {
                throw new DruidRuntimeException("load config error, url : " + url.toString());
            } finally {
                JdbcUtils.close(reader);
                conn.disconnect();
            }
        }

        MultiDataSourceConfig config = JSON.parseObject(responseMessage, MultiDataSourceConfig.class);
        return config;
    }

}
