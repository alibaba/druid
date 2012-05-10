package com.alibaba.druid.http;

import junit.framework.TestCase;

public class HttpServerTest2 extends TestCase {

    public void test_httpServer() throws Exception {
        HttpServer server = new HttpServer();
        server.start();
        server.join();
    }
}
