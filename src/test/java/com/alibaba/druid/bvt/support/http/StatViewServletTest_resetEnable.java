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
package com.alibaba.druid.bvt.support.http;

import org.junit.Assert;
import junit.framework.TestCase;

import org.springframework.mock.web.MockServletConfig;

import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.http.StatViewServlet;

public class StatViewServletTest_resetEnable extends TestCase {

    protected void setUp() throws Exception {
        DruidStatService.getInstance().setResetEnable(true);
    }

    protected void tearDown() throws Exception {
        DruidStatService.getInstance().setResetEnable(true);
    }

    public void test_resetEnable_none() throws Exception {
        Assert.assertTrue(DruidStatService.getInstance().isResetEnable());

        MockServletConfig servletConfig = new MockServletConfig();

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertTrue(DruidStatService.getInstance().isResetEnable());
    }

    public void test_resetEnable_true() throws Exception {
        Assert.assertTrue(DruidStatService.getInstance().isResetEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, "true");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertTrue(DruidStatService.getInstance().isResetEnable());
    }

    public void test_resetEnable_empty() throws Exception {
        Assert.assertTrue(DruidStatService.getInstance().isResetEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, "");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertTrue(DruidStatService.getInstance().isResetEnable());
    }

    public void test_resetEnable_false() throws Exception {
        Assert.assertTrue(DruidStatService.getInstance().isResetEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, "false");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertFalse(DruidStatService.getInstance().isResetEnable());
    }

    public void test_resetEnable_error() throws Exception {
        Assert.assertTrue(DruidStatService.getInstance().isResetEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, "xxx");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertFalse(DruidStatService.getInstance().isResetEnable());
    }
}
