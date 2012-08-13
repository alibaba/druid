package com.alibaba.druid.support.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.druid.filter.stat.StatFilterContext;
import com.alibaba.druid.filter.stat.StatFilterContextListenerAdapter;
import com.alibaba.druid.support.http.stat.WebAppStat;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.http.stat.WebRequestStat;
import com.alibaba.druid.support.http.stat.WebSessionStat;
import com.alibaba.druid.support.http.stat.WebURIStat;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.DruidWebUtils;
import com.alibaba.druid.util.PatternMatcher;
import com.alibaba.druid.util.ServletPathMatcher;

public class WebStatFilter implements Filter {

    public final static String           PARAM_NAME_SESSION_STAT_ENABLE    = "sessionStatEnable";
    public final static String           PARAM_NAME_SESSION_STAT_MAX_COUNT = "sessionStatMaxCount";
    public static final String           PARAM_NAME_EXCLUSIONS             = "exclusions";

    public final static int              DEFAULT_MAX_STAT_SESSION_COUNT    = 1000 * 100;

    private final static Log             LOG                               = LogFactory.getLog(WebStatFilter.class);

    private WebAppStat                   webAppStat                        = null;
    private WebStatFilterContextListener statFilterContextListener         = new WebStatFilterContextListener();
    /**
     * PatternMatcher used in determining which paths to react to for a given request.
     */
    protected PatternMatcher             pathMatcher                       = new ServletPathMatcher();

    private Set<String>                  excludesPattern;

    private boolean                      sessionStatEnable                 = true;
    private int                          sessionStatMaxCount               = DEFAULT_MAX_STAT_SESSION_COUNT;
    private boolean                      createSession                     = false;

    private String                       contextPath;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                             ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        final String requestURI = getRequestURI(httpRequest);

        if (isExclusion(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        long startNano = System.nanoTime();
        long startMillis = System.currentTimeMillis();

        WebRequestStat requestStat = new WebRequestStat(startNano);
        WebRequestStat.set(requestStat);
        WebSessionStat sessionStat = getSessionStat(httpRequest);
        webAppStat.beforeInvoke(requestURI);

        // 第一次访问时，sessionId为null，如果缺省sessionCreate=false，sessionStat就为null。
        if (sessionStat != null) {
            sessionStat.beforeInvoke();
            sessionStat.setLastAccessTimeMillis(startMillis);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            long endNano = System.nanoTime();
            requestStat.setEndNano(endNano);

            long nanoSpan = endNano - startNano;
            webAppStat.afterInvoke(nanoSpan);

            if (sessionStat != null) {
                sessionStat.afterInvoke(nanoSpan);
            } else {
                sessionStat = getSessionStat(httpRequest);
                if (sessionStat != null) {
                    sessionStat.setLastAccessTimeMillis(startMillis);
                    sessionStat.reacord(nanoSpan);
                    sessionStat.incrementRequestCount();
                }
            }

            WebRequestStat.set(null);
        }
    }

    public WebSessionStat getSessionStat(HttpServletRequest request) {
        if (!isSessionStatEnable()) {
            return null;
        }

        WebSessionStat sessionStat = null;
        String sessionId = getSessionId(request);
        if (sessionId != null) {
            sessionStat = webAppStat.getSessionStat(sessionId, true);
        }

        if (sessionStat != null) {
            if (sessionStat.getCreateTimeMillis() == -1L) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    sessionStat.setCreateTimeMillis(session.getCreationTime());
                } else {
                    sessionStat.setCreateTimeMillis(System.currentTimeMillis());
                }
            }

            String ip = DruidWebUtils.getRemoteAddr(request);

            int addressCount = sessionStat.getRemoteAddresses().size();
            if (addressCount < 10) {
                sessionStat.addRemoteAddress(ip);
            } else {
                if (!sessionStat.getRemoteAddress().contains(ip)) {
                    LOG.error("sessoin ip change too many");
                }
            }
        }

        return sessionStat;
    }

    public String getSessionId(HttpServletRequest httpRequest) {
        String sessionId = null;

        HttpSession session = httpRequest.getSession(createSession);
        if (session != null) {
            sessionId = session.getId();
        }

        return sessionId;
    }

    public boolean isExclusion(String requestURI) {
        if (excludesPattern == null) {
            return false;
        }

        if (contextPath != null && requestURI.startsWith(contextPath)) {
            requestURI = requestURI.substring(contextPath.length());
            if (!requestURI.startsWith("/")) {
                requestURI = "/" + requestURI;
            }
        }

        for (String pattern : excludesPattern) {
            if (pathMatcher.matches(pattern, requestURI)) {
                return true;
            }
        }

        return false;
    }

    public String getRequestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        {
            String exclusions = config.getInitParameter(PARAM_NAME_EXCLUSIONS);
            if (exclusions != null && exclusions.trim().length() != 0) {
                excludesPattern = new HashSet<String>(Arrays.asList(exclusions.split("\\s*,\\s*")));
            }
        }
        {
            String param = config.getInitParameter(PARAM_NAME_SESSION_STAT_ENABLE);
            if (param != null && param.trim().length() != 0) {
                param = param.trim();
                if ("true".equals(param)) {
                    this.sessionStatEnable = true;
                } else if ("false".equals(param)) {
                    this.sessionStatEnable = false;
                } else {
                    LOG.error("WebStatFilter Parameter '" + PARAM_NAME_SESSION_STAT_ENABLE + "' config error");
                }
            }
        }
        {
            String param = config.getInitParameter(PARAM_NAME_SESSION_STAT_MAX_COUNT);
            if (param != null && param.trim().length() != 0) {
                param = param.trim();
                try {
                    this.sessionStatMaxCount = Integer.parseInt(param);
                } catch (NumberFormatException e) {
                    LOG.error("WebStatFilter Parameter '" + PARAM_NAME_SESSION_STAT_ENABLE + "' config error", e);
                }
            }
        }

        StatFilterContext.getInstance().addContextListener(statFilterContextListener);

        this.contextPath = DruidWebUtils.getContextPath(config.getServletContext());
        webAppStat = new WebAppStat(contextPath, this.sessionStatMaxCount);

        WebAppStatManager.getInstance().addWebAppStatSet(webAppStat);
    }

    @Override
    public void destroy() {
        StatFilterContext.getInstance().removeContextListener(statFilterContextListener);

        if (webAppStat != null) {
            WebAppStatManager.getInstance().remove(webAppStat);
        }
    }

    public boolean isSessionStatEnable() {
        return sessionStatEnable;
    }

    public void setSessionStatEnable(boolean sessionStatEnable) {
        this.sessionStatEnable = sessionStatEnable;
    }

    public WebAppStat getWebAppStat() {
        return webAppStat;
    }

    public String getContextPath() {
        return contextPath;
    }

    public int getSessionStatMaxCount() {
        return sessionStatMaxCount;
    }

    public WebStatFilterContextListener getStatFilterContextListener() {
        return statFilterContextListener;
    }

    class WebStatFilterContextListener extends StatFilterContextListenerAdapter {

        @Override
        public void addUpdateCount(int updateCount) {
            {
                WebURIStat stat = WebURIStat.current();
                if (stat != null) {
                    stat.addJdbcUpdateCount(updateCount);
                }
            }
            {

                WebRequestStat localStat = WebRequestStat.current();
                if (localStat != null) {
                    localStat.addJdbcUpdateCount(updateCount);
                }
            }
        }

        @Override
        public void addFetchRowCount(int fetchRowCount) {
            {
                WebURIStat stat = WebURIStat.current();
                if (stat != null) {
                    stat.addJdbcFetchRowCount(fetchRowCount);
                }
            }
            {
                WebRequestStat localStat = WebRequestStat.current();
                if (localStat != null) {
                    localStat.addJdbcFetchRowCount(fetchRowCount);
                }
            }
        }

        @Override
        public void executeBefore(String sql, boolean inTransaction) {
            {
                WebURIStat stat = WebURIStat.current();
                if (stat != null) {
                    stat.incrementJdbcExecuteCount();
                }
            }
            {
                WebRequestStat localStat = WebRequestStat.current();
                if (localStat != null) {
                    localStat.incrementJdbcExecuteCount();
                }
            }
        }

        @Override
        public void executeAfter(String sql, long nanoSpan, Throwable error) {
            WebRequestStat localStat = WebRequestStat.current();
            if (localStat != null) {
                localStat.addJdbcExecuteNano(nanoSpan);
            }
        }

        @Override
        public void commit() {
            WebURIStat stat = WebURIStat.current();
            if (stat != null) {
                stat.incrementJdbcCommitCount();
            }
            {
                WebRequestStat localStat = WebRequestStat.current();
                if (localStat != null) {
                    localStat.incrementJdbcCommitCount();
                }
            }
        }

        @Override
        public void rollback() {
            WebURIStat stat = WebURIStat.current();
            if (stat != null) {
                stat.incrementJdbcRollbackCount();
            }

            {
                WebRequestStat localStat = WebRequestStat.current();
                if (localStat != null) {
                    localStat.incrementJdbcRollbackCount();
                }
            }
        }
    }
}
