package com.alibaba.druid.admin.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author linchtech
 * @date 2020-09-16 16:12
 **/
public class HttpUtil {

    public static <T> T get(String url, Class<T> resultType) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-type", "application/json");
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
            String result;
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                return JSON.parseObject(result, resultType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
