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

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.http.StatViewServlet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletConfig;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StatViewServletTest_classpathEnable {
    @BeforeEach
    protected void setUp() throws Exception {
        DruidStatService.getInstance().setClasspathEnable(true);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        DruidStatService.getInstance().setClasspathEnable(true);
    }

    @Test
    public void test_classpathEnable_none() throws Exception {
        assertTrue(DruidStatService.getInstance().isClasspathEnable());

        MockServletConfig servletConfig = new MockServletConfig();

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        assertTrue(DruidStatService.getInstance().isClasspathEnable());
        Map<String, Object> basicStat = DruidStatManagerFacade.getInstance().returnJSONBasicStat();
        assertTrue((Boolean) basicStat.get("ClasspathEnable"));
        assertNotNull(basicStat.get("JavaClassPath"));
    }

    @Test
    public void test_classpathEnable_true() throws Exception {
        assertTrue(DruidStatService.getInstance().isClasspathEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_CLASSPATH_ENABLE, "true");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        assertTrue(DruidStatService.getInstance().isClasspathEnable());
        Map<String, Object> basicStat = DruidStatManagerFacade.getInstance().returnJSONBasicStat();
        assertTrue((Boolean) basicStat.get("ClasspathEnable"));
        assertNotNull(basicStat.get("JavaClassPath"));
    }

    @Test
    public void test_classpathEnable_empty() throws Exception {
        assertTrue(DruidStatService.getInstance().isClasspathEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_CLASSPATH_ENABLE, "");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        assertTrue(DruidStatService.getInstance().isClasspathEnable());
    }

    @Test
    public void test_classpathEnable_false() throws Exception {
        assertTrue(DruidStatService.getInstance().isClasspathEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_CLASSPATH_ENABLE, "false");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        assertFalse(DruidStatService.getInstance().isClasspathEnable());
        Map<String, Object> basicStat = DruidStatManagerFacade.getInstance().returnJSONBasicStat();
        assertFalse((Boolean) basicStat.get("ClasspathEnable"));
        assertFalse(basicStat.containsKey("JavaClassPath"));
    }

    @Test
    public void test_classpathEnable_error() throws Exception {
        assertTrue(DruidStatService.getInstance().isClasspathEnable());

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.addInitParameter(StatViewServlet.PARAM_NAME_CLASSPATH_ENABLE, "xxx");

        StatViewServlet servlet = new StatViewServlet();
        servlet.init(servletConfig);

        assertFalse(DruidStatService.getInstance().isClasspathEnable());
        Map<String, Object> basicStat = DruidStatManagerFacade.getInstance().returnJSONBasicStat();
        assertFalse(basicStat.containsKey("JavaClassPath"));
    }
}
