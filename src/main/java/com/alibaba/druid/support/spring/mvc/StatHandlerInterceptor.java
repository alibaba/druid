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
package com.alibaba.druid.support.spring.mvc;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.filter.stat.StatFilterContext;
import com.alibaba.druid.support.http.AbstractWebStatImpl;
import com.alibaba.druid.support.http.stat.WebAppStat;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.http.stat.WebRequestStat;
import com.alibaba.druid.support.http.stat.WebSessionStat;
import com.alibaba.druid.support.http.stat.WebURIStat;
import com.alibaba.druid.support.profile.ProfileEntryKey;
import com.alibaba.druid.support.profile.ProfileEntryReqStat;
import com.alibaba.druid.support.profile.Profiler;
import com.alibaba.druid.util.DruidWebUtils;

public class StatHandlerInterceptor extends AbstractWebStatImpl implements HandlerInterceptor, InitializingBean, DisposableBean {

    public StatHandlerInterceptor(){

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final WebAppStat webAppStat = getWebAppStat(request);
        String requestURI = getRequestURI(request);

        long startNano = System.nanoTime();
        long startMillis = System.currentTimeMillis();

        WebRequestStat requestStat = new WebRequestStat(startNano, startMillis);
        WebRequestStat.set(requestStat);

        WebSessionStat sessionStat = getSessionStat(request);
        webAppStat.beforeInvoke();

        WebURIStat uriStat = webAppStat.getURIStat(requestURI, false);

        if (uriStat == null) {
            int index = requestURI.indexOf(";jsessionid=");
            if (index != -1) {
                requestURI = requestURI.substring(0, index);
                uriStat = webAppStat.getURIStat(requestURI, false);
            }
        }

        if (isProfileEnable()) {
            Profiler.initLocal();
            Profiler.enter(requestURI, Profiler.PROFILE_TYPE_WEB);
        }

        // 第一次访问时，uriStat这里为null，是为了防止404攻击。
        if (uriStat != null) {
            uriStat.beforeInvoke();
        }

        // 第一次访问时，sessionId为null，如果缺省sessionCreate=false，sessionStat就为null。
        if (sessionStat != null) {
            sessionStat.beforeInvoke();
        }

        return true;
    }

    public WebAppStat getWebAppStat(HttpServletRequest request) {
        if (webAppStat != null) {
            return webAppStat;
        }

        ServletContext context = request.getSession().getServletContext();
        String contextPath = DruidWebUtils.getContextPath(context);
        webAppStat = WebAppStatManager.getInstance().getWebAppStat(contextPath);
        return webAppStat;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception error) throws Exception {
        WebRequestStat requestStat = WebRequestStat.current();
        long endNano = System.nanoTime();
        requestStat.setEndNano(endNano);

        WebSessionStat sessionStat = getSessionStat(request);
        WebURIStat uriStat = WebURIStat.current();

        long nanos = endNano - requestStat.getStartNano();
        webAppStat.afterInvoke(null, nanos);

        if (sessionStat == null) {
            sessionStat = getSessionStat(request);
            if (sessionStat != null) {
                sessionStat.beforeInvoke(); // 补偿
            }
        }

        if (sessionStat != null) {
            sessionStat.afterInvoke(error, nanos);
            sessionStat.setPrincipal(getPrincipal(request));
        }

        if (uriStat != null) {
            uriStat.afterInvoke(error, nanos);
        }

        WebRequestStat.set(null);

        if (isProfileEnable()) {
            Profiler.release(nanos);

            Map<ProfileEntryKey, ProfileEntryReqStat> requestStatsMap = Profiler.getStatsMap();
            if (uriStat != null) {
                uriStat.getProfiletat().record(requestStatsMap);
            }
            Profiler.removeLocal();
        }
    }

    public String getRequestURI(HttpServletRequest request) {
        return (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        StatFilterContext.getInstance().addContextListener(statFilterContextListener);
    }

    @Override
    public void destroy() throws Exception {
        StatFilterContext.getInstance().removeContextListener(statFilterContextListener);
    }
}
