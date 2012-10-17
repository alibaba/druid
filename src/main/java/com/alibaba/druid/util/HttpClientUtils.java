package com.alibaba.druid.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class HttpClientUtils {

    private final static Log LOG = LogFactory.getLog(HttpClientUtils.class);

    public static boolean post(String serverUrl, String data, long timeout) {
        StringBuilder responseBuilder = null;
        BufferedReader reader = null;
        OutputStreamWriter wr = null;

        URL url;
        try {
            url = new URL(serverUrl);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(1000 * 5);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("");
            wr.flush();

            // Get the response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            responseBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line + "\n");
            }
            wr.close();
            reader.close();

            System.out.println(responseBuilder.toString());
        } catch (IOException e) {
            LOG.error("", e);
        }

        return false;
    }

    public static void main(String args[]) {
        post("http://www.alibaba.com", "", 6000);
    }

}
