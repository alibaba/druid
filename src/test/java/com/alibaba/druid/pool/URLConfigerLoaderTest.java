package com.alibaba.druid.pool;

import junit.framework.TestCase;

import com.alibaba.druid.pool.ha.config.URLConnectionConfigLoader;


public class URLConfigerLoaderTest extends TestCase {
    public void test_0 () throws Exception {
        String url = "http://www.baidu.com/";
        
        URLConnectionConfigLoader loader = new URLConnectionConfigLoader(url);
        loader.load();
    }
}
