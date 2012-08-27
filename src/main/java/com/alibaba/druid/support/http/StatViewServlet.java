/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.http.util.IPAddress;
import com.alibaba.druid.support.http.util.IPRange;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.IOUtils;
import com.alibaba.druid.util.StringUtils;

/**
 * 注意：避免直接调用Druid相关对象例如DruidDataSource等，相关调用要到DruidStatManagerFacade里用反射实现
 * 
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class StatViewServlet extends HttpServlet {

    private final static Log    LOG                         = LogFactory.getLog(StatViewServlet.class);

    private static final long   serialVersionUID            = 1L;

    public static final String  PARAM_NAME_RESET_ENABLE     = "resetEnable";
    public static final String  PARAM_NAME_ALLOW            = "allow";
    public static final String  PARAM_NAME_DENY             = "deny";

    public static final String  PARAM_NAME_USERNAME         = "loginUsername";
    public static final String  PARAM_NAME_PASSWORD         = "loginPassword";

    public static final String  SESSION_USER_KEY            = "druid-user";

    private final static String RESOURCE_PATH               = "support/http/resources";
    private final static String TEMPLATE_PAGE_RESOURCE_PATH = RESOURCE_PATH + "/template.html";

    private DruidStatService    statService                 = DruidStatService.getInstance();

    public String               templatePage;

    private List<IPRange>       allowList                   = new ArrayList<IPRange>();
    private List<IPRange>       denyList                    = new ArrayList<IPRange>();

    private String              username                    = null;
    private String              password                    = null;

    public void init() throws ServletException {
        initAuthEnv();

        try {
            templatePage = IOUtils.readFromResource(TEMPLATE_PAGE_RESOURCE_PATH);

        } catch (IOException e) {
            throw new ServletException("error read templatePage:" + TEMPLATE_PAGE_RESOURCE_PATH, e);
        }

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

        try {
            String param = getInitParameter(PARAM_NAME_ALLOW);
            if (param != null && param.trim().length() != 0) {
                param = param.trim();
                String[] items = param.split(",");

                for (String item : items) {
                    if (item == null || item.length() == 0) {
                        continue;
                    }

                    IPRange ipRange = new IPRange(item);
                    allowList.add(ipRange);
                }
            }
        } catch (Exception e) {
            String msg = "initParameter config error, allow : " + getInitParameter(PARAM_NAME_ALLOW);
            LOG.error(msg, e);
        }

        try {
            String param = getInitParameter(PARAM_NAME_DENY);
            if (param != null && param.trim().length() != 0) {
                param = param.trim();
                String[] items = param.split(",");

                for (String item : items) {
                    if (item == null || item.length() == 0) {
                        continue;
                    }

                    IPRange ipRange = new IPRange(item);
                    denyList.add(ipRange);
                }
            }
        } catch (Exception e) {
            String msg = "initParameter config error, deny : " + getInitParameter(PARAM_NAME_DENY);
            LOG.error(msg, e);
        }
    }

    private void initAuthEnv() {
        String paramUserName = getInitParameter(PARAM_NAME_USERNAME);
        if (!StringUtils.isEmpty(paramUserName)) {
            this.username = paramUserName;
        }

        String paramPassword = getInitParameter(PARAM_NAME_PASSWORD);
        if (!StringUtils.isEmpty(paramPassword)) {
            this.password = paramPassword;
        }
    }

    public boolean isRequireAuth() {
        return this.username != null;
    }

    public boolean isPermittedRequest(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();
        return isPermittedRequest(remoteAddress);
    }

    public boolean isPermittedRequest(String remoteAddress) {
        boolean ipV6 = remoteAddress != null && remoteAddress.indexOf(':') != -1;

        if (ipV6) {
            if (denyList.size() == 0 && allowList.size() == 0) {
                return true;
            }
        }

        IPAddress ipAddress = new IPAddress(remoteAddress);

        for (IPRange range : denyList) {
            if (range.isIPAddressInRange(ipAddress)) {
                return false;
            }
        }

        if (allowList.size() > 0) {
            for (IPRange range : allowList) {
                if (range.isIPAddressInRange(ipAddress)) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        response.setCharacterEncoding("utf-8");

        if (contextPath == null) { // root context
            contextPath = "";
        }
        String uri = contextPath + servletPath;
        String path = requestURI.substring(contextPath.length() + servletPath.length());

        if (!isPermittedRequest(request)) {
            path = "/nopermit.html";
            returnResourceFile(path, uri, response);
            return;
        }

        if ("/submitLogin".equals(path)) {
            String _username = request.getParameter(PARAM_NAME_USERNAME);
            String _password = request.getParameter(PARAM_NAME_PASSWORD);
            if (username.equals(_username) && password.equals(_password)) {
                request.getSession().setAttribute(SESSION_USER_KEY, username);
                response.getWriter().print("success");
            } else {
                response.getWriter().print("error");
            }
            return;
        }

        if (isRequireAuth()
            && session.getAttribute(SESSION_USER_KEY) == null
            && !("/login.html".equals(path) || path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/img"))) {
            if (contextPath == null || contextPath.equals("") || contextPath.equals("/")) {
                response.sendRedirect("/login.html");
            } else {
                response.sendRedirect("login.html");
            }
            return;
        }

        if ("".equals(path)) {
            if (contextPath == null || contextPath.equals("") || contextPath.equals("/")) {
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

        if (path.indexOf(".json") >= 0) {
            String fullUrl = path;
            if (request.getQueryString() != null && request.getQueryString().length() > 0) {
                fullUrl += "?" + request.getQueryString();
            }
            response.getWriter().print(statService.service(fullUrl));
            return;
        }

        // find file in resources path
        returnResourceFile(path, uri, response);
    }

    private void returnResourceFile(String fileName, String uri, HttpServletResponse response) throws ServletException,
                                                                                              IOException {
        if (fileName.endsWith(".jpg")) {
            byte[] bytes = IOUtils.readByteArrayFromResource(RESOURCE_PATH + fileName);
            if (bytes != null) {
                response.getOutputStream().write(bytes);
            }
            
            return;
        }

        String text = IOUtils.readFromResource(RESOURCE_PATH + fileName);
        if (text == null) {
            response.sendRedirect(uri + "/index.html");
            return;
        }
        if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
        } else if (fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        response.getWriter().write(text);
    }

}
