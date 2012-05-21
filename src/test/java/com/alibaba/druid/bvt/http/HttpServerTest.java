package com.alibaba.druid.bvt.http;

import com.alibaba.druid.support.http.HttpServer;

import junit.framework.TestCase;

public class HttpServerTest extends TestCase {
    public void test_httpServer() throws Exception {
        HttpServer server = new HttpServer();
        server.start();
        server.stop();
    }
}
