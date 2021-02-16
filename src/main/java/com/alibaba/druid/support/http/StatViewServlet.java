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
package com.alibaba.druid.support.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * 注意：避免直接调用Druid相关对象例如DruidDataSource等，相关调用要到DruidStatManagerFacade里用反射实现
 * 
 * @author sandzhang[sandzhangtoo@gmail.com]
 */
public class StatViewServlet extends ResourceServlet {

    private final static Log      LOG                     = LogFactory.getLog(StatViewServlet.class);

    private static final long     serialVersionUID        = 1L;

    public static final String    PARAM_NAME_RESET_ENABLE = "resetEnable";

    public static final String    PARAM_NAME_JMX_URL      = "jmxUrl";
    public static final String    PARAM_NAME_JMX_USERNAME = "jmxUsername";
    public static final String    PARAM_NAME_JMX_PASSWORD = "jmxPassword";

    private DruidStatService      statService             = DruidStatService.getInstance();

    /** web.xml中配置的jmx的连接地址 */
    private String                jmxUrl                  = null;
    /** web.xml中配置的jmx的用户名 */
    private String                jmxUsername             = null;
    /** web.xml中配置的jmx的密码 */
    private String                jmxPassword             = null;
    private MBeanServerConnection conn                    = null;

    public StatViewServlet(){
        super("support/http/resources");
    }

    public void init() throws ServletException {
        super.init();

        try {
            String param = getInitParameter(PARAM_NAME_RESET_ENABLE);
            if (param != null && param.trim().length() != 0) {
                param = param.trim();
                boolean resetEnable = Boolean.parseBoolean(param);
                statService.setResetEnable(resetEnable);
            }
        } catch (Exception e) {
            String msg = "initParameter config error, resetEnable : " + getInitParameter(PARAM_NAME_RESET_ENABLE);
            LOG.error(msg, e);
        }

        // 获取jmx的连接配置信息
        String param = readInitParam(PARAM_NAME_JMX_URL);
        if (param != null) {
            jmxUrl = param;
            jmxUsername = readInitParam(PARAM_NAME_JMX_USERNAME);
            jmxPassword = readInitParam(PARAM_NAME_JMX_PASSWORD);
            try {
                initJmxConn();
            } catch (IOException e) {
                LOG.error("init jmx connection error", e);
            }
        }

    }

    /**
     * 读取servlet中的配置参数.
     * 
     * @param key 配置参数名
     * @return 配置参数值，如果不存在当前配置参数，或者为配置参数长度为0，将返回null
     */
    private String readInitParam(String key) {
        String value = null;
        try {
            String param = getInitParameter(key);
            if (param != null) {
                param = param.trim();
                if (param.length() > 0) {
                    value = param;
                }
            }
        } catch (Exception e) {
            String msg = "initParameter config [" + key + "] error";
            LOG.warn(msg, e);
        }
        return value;
    }

    /**
     * 初始化jmx连接
     * 
     * @throws IOException
     */
    private void initJmxConn() throws IOException {
        if (jmxUrl != null) {
            JMXServiceURL url = new JMXServiceURL(jmxUrl);
            Map<String, String[]> env = null;
            if (jmxUsername != null) {
                env = new HashMap<String, String[]>();
                String[] credentials = new String[] { jmxUsername, jmxPassword };
                env.put(JMXConnector.CREDENTIALS, credentials);
            }
            JMXConnector jmxc = JMXConnectorFactory.connect(url, env);
            conn = jmxc.getMBeanServerConnection();
        }
    }

    /**
     * 根据指定的url来获取jmx服务返回的内容.
     * 
     * @param connetion jmx连接
     * @param url url内容
     * @return the jmx返回的内容
     * @throws Exception the exception
     */
    private String getJmxResult(MBeanServerConnection connetion, String url) throws Exception {
        ObjectName name = new ObjectName(DruidStatService.MBEAN_NAME);

        String result = (String) conn.invoke(name, "service", new String[] { url },
                                             new String[] { String.class.getName() });
        return result;
    }

    /**
     * 程序首先判断是否存在jmx连接地址，如果不存在，则直接调用本地的duird服务； 如果存在，则调用远程jmx服务。在进行jmx通信，首先判断一下jmx连接是否已经建立成功，如果已经
     * 建立成功，则直接进行通信，如果之前没有成功建立，则会尝试重新建立一遍。.
     * 
     * @param url 要连接的服务地址
     * @return 调用服务后返回的json字符串
     */
    protected String process(String url) {
        String resp = null;
        if (jmxUrl == null) {
            resp = statService.service(url);
        } else {
            if (conn == null) {// 连接在初始化时创建失败
                try {// 尝试重新连接
                    initJmxConn();
                } catch (IOException e) {
                    LOG.error("init jmx connection error", e);
                    resp = DruidStatService.returnJSONResult(DruidStatService.RESULT_CODE_ERROR,
                                                             "init jmx connection error" + e.getMessage());
                }
                if (conn != null) {// 连接成功
                    try {
                        resp = getJmxResult(conn, url);
                    } catch (Exception e) {
                        LOG.error("get jmx data error", e);
                        resp = DruidStatService.returnJSONResult(DruidStatService.RESULT_CODE_ERROR, "get data error:"
                                                                                                     + e.getMessage());
                    }
                }
            } else {// 连接成功
                try {
                    resp = getJmxResult(conn, url);
                } catch (Exception e) {
                    LOG.error("get jmx data error", e);
                    resp = DruidStatService.returnJSONResult(DruidStatService.RESULT_CODE_ERROR,
                                                             "get data error" + e.getMessage());
                }
            }
        }
        return resp;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        response.setCharacterEncoding("utf-8");

        if (contextPath == null) { // root context
            contextPath = "";
        }
        String uri = contextPath + servletPath;
        String path = requestURI.substring(contextPath.length() + servletPath.length());

        if ("".equals(path)) {
            if (contextPath.equals("") || contextPath.equals("/")) {
                response.sendRedirect("/druid/index.html");
            } else {
                response.sendRedirect("druid/index.html");
            }
            return;
        }

        if ("/".equals(path)) {
            response.sendRedirect("index.html");
            return;
        }

        super.service(request, response);
    }

}
