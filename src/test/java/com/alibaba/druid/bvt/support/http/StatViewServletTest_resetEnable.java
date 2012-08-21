package com.alibaba.druid.bvt.support.http;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.springframework.mock.web.MockServletConfig;

import com.alibaba.druid.stat.DruidStatJSONService;
import com.alibaba.druid.support.http.StatViewServlet;

public class StatViewServletTest_resetEnable extends TestCase {

    protected void setUp() throws Exception {
        DruidStatJSONService.getInstance().setResetEnable(true);
    }

    protected void tearDown() throws Exception {
        DruidStatJSONService.getInstance().setResetEnable(true);
    }

    public void test_resetEnable_none() throws Exception {
        Assert.assertTrue(DruidStatJSONService.getInstance().isResetEnable());

        MockServletConfig servletConfig = new MockServletConfig();

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertTrue(DruidStatJSONService.getInstance().isResetEnable());
    }

    public void test_resetEnable_true() throws Exception {
        Assert.assertTrue(DruidStatJSONService.getInstance().isResetEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, "true");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertTrue(DruidStatJSONService.getInstance().isResetEnable());
    }

    public void test_resetEnable_empty() throws Exception {
        Assert.assertTrue(DruidStatJSONService.getInstance().isResetEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, "");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertTrue(DruidStatJSONService.getInstance().isResetEnable());
    }

    public void test_resetEnable_false() throws Exception {
        Assert.assertTrue(DruidStatJSONService.getInstance().isResetEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, "false");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertFalse(DruidStatJSONService.getInstance().isResetEnable());
    }

    public void test_resetEnable_error() throws Exception {
        Assert.assertTrue(DruidStatJSONService.getInstance().isResetEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, "xxx");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        Assert.assertFalse(DruidStatJSONService.getInstance().isResetEnable());
    }
}
