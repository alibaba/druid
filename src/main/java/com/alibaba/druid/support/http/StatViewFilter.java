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

import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.http.util.IPRange;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.alibaba.druid.support.http.ResourceServlet.*;

public class StatViewFilter implements Filter {
    public final static String PARAM_NAME_PATH = "path";
    private final static Log LOG = LogFactory.getLog(StatViewFilter.class);
    private String servletPath = "/druid";
    private String resourcePath = "support/http/resources";

    private ResourceHandler handler;
    private DruidStatService statService = DruidStatService.getInstance();

    @Override
    public void init(FilterConfig config) throws ServletException {
        if (config == null) {
            return;
        }

        String path = config.getInitParameter(PARAM_NAME_PATH);
        if (path != null && !path.isEmpty()) {
            this.servletPath = path;
        }

        handler = new ResourceHandler(resourcePath);

        String paramUserName = config.getInitParameter(PARAM_NAME_USERNAME);
        if (!StringUtils.isEmpty(paramUserName)) {
            handler.username = paramUserName;
        }

        String paramPassword = config.getInitParameter(PARAM_NAME_PASSWORD);
        if (!StringUtils.isEmpty(paramPassword)) {
            handler.password = paramPassword;
        }

        String paramRemoteAddressHeader = config.getInitParameter(PARAM_REMOTE_ADDR);
        if (!StringUtils.isEmpty(paramRemoteAddressHeader)) {
            handler.remoteAddressHeader = paramRemoteAddressHeader;
        }

        try {
            String param = config.getInitParameter(PARAM_NAME_ALLOW);
            if (param != null && param.trim().length() != 0) {
                param = param.trim();
                String[] items = param.split(",");

                for (String item : items) {
                    if (item == null || item.length() == 0) {
                        continue;
                    }

                    IPRange ipRange = new IPRange(item);
                    handler.allowList.add(ipRange);
                }
            }
        } catch (Exception e) {
            String msg = "initParameter config error, allow : " + config.getInitParameter(PARAM_NAME_ALLOW);
            LOG.error(msg, e);
        }

        try {
            String param = config.getInitParameter(PARAM_NAME_DENY);
            if (param != null && param.trim().length() != 0) {
                param = param.trim();
                String[] items = param.split(",");

                for (String item : items) {
                    if (item == null || item.length() == 0) {
                        continue;
                    }

                    IPRange ipRange = new IPRange(item);
                    handler.denyList.add(ipRange);
                }
            }
        } catch (Exception e) {
            String msg = "initParameter config error, deny : " + config.getInitParameter(PARAM_NAME_DENY);
            LOG.error(msg, e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;
        String contextPath = ((HttpServletRequest) request).getContextPath();

        String requestURI = httpReq.getRequestURI();
        if (!contextPath.equals("")) {
            requestURI = requestURI.substring(((HttpServletRequest) request).getContextPath().length());
        }
        if (requestURI.equals(servletPath)) {
            httpResp.sendRedirect(httpReq.getRequestURI() + '/');
        }

        handler.service(httpReq, httpResp, servletPath, new ResourceServlet.ProcessCallback() {
            @Override
            public String process(String url) {
                return statService.service(url);
            }
        });
    }

    @Override
    public void destroy() {

    }
}
