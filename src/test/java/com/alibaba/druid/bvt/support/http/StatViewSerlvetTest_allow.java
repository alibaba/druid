/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.support.http;

import org.junit.Assert;
import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletConfig;

import com.alibaba.druid.support.http.StatViewServlet;

public class StatViewSerlvetTest_allow extends TestCase {

    public void test_allow() throws Exception {
        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_ALLOW, "128.242.127.2");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("128.242.127.2");
        Assert.assertTrue(servlet.isPermittedRequest(request));

        Assert.assertFalse(servlet.isPermittedRequest("128.242.127.3"));
    }

    public void test_allow_1() throws Exception {
        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_ALLOW, "128.242.127.2,xx");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("128.242.127.2");
        Assert.assertTrue(servlet.isPermittedRequest(request));

        Assert.assertFalse(servlet.isPermittedRequest("128.242.127.3"));
    }

    public void test_allow_2() throws Exception {
        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_ALLOW, "128.242.127.2,,, ");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("128.242.127.2");
        Assert.assertTrue(servlet.isPermittedRequest(request));

        Assert.assertFalse(servlet.isPermittedRequest("128.242.127.3"));
    }

    public void test_allow_3() throws Exception {
        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_ALLOW, "128.242.127.2/24");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        for (int i = 1; i <= 255; ++i) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRemoteAddr("128.242.127." + i);
            Assert.assertTrue(servlet.isPermittedRequest(request));
        }
        
        Assert.assertFalse(servlet.isPermittedRequest("128.242.128.1"));
    }
    
    public void test_allow_4() throws Exception {
        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_ALLOW, "128.242.127.2/24");
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_DENY, "128.242.127.4");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertTrue(servlet.isPermittedRequest("128.242.127.1"));
        Assert.assertTrue(servlet.isPermittedRequest("128.242.127.2"));
        Assert.assertTrue(servlet.isPermittedRequest("128.242.127.3"));
        Assert.assertFalse(servlet.isPermittedRequest("128.242.127.4"));
        Assert.assertTrue(servlet.isPermittedRequest("128.242.127.5"));
    }
}
