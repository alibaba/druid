/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.util;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author sandzhang[sandzhangtoo@gmail.com]
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

            wr.write(data);
            wr.flush();

            if (LOG.isDebugEnabled()) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                responseBuilder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line).append("\n");
                }
                LOG.debug(responseBuilder.toString());
            }
        } catch (IOException e) {
            LOG.error("", e);
        } finally {

            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException e) {
                    LOG.error("close error", e);
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.error("close error", e);
                }
            }

        }

        return false;
    }

    public static void main(String args[]) {
        post("http://www.alibaba.com/trade/search", "fsb=y&IndexArea=product_en&CatId=&SearchText=test", 6000);
    }

}
