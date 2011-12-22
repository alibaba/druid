package com.alibaba.druid.pool.ha.config;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import com.alibaba.druid.util.JdbcUtils;

public abstract class URLConnectionConfigLoader implements ConfigLoader {

    private URL    url;
    private int    connectTimeout = 1000 * 3;
    private int    readTimeout;

    protected String responseMessage;

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

    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public void load() throws SQLException {
        if (url == null) {
            throw new IllegalStateException("configServerURL is null");
        }

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

                handleResponseMessage();
            } catch (Exception e) {
                throw new SQLException("load config error, url : " + url.toString(), e);
            } finally {
                JdbcUtils.close(reader);
                conn.disconnect();
            }
        }
    }

    protected abstract void handleResponseMessage() throws SQLException;

}
