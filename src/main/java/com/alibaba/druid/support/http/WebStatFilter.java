package com.alibaba.druid.support.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
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
import com.alibaba.druid.support.spring.stat.SpringMethodStat;
import com.alibaba.druid.util.DruidWebUtils;
import com.alibaba.druid.util.PatternMatcher;
import com.alibaba.druid.util.ServletPathMatcher;

/**
 * 用于配置Web和Druid数据源之间的管理关联监控统计
 * 
 * @author wenshao <szujobs@htomail.com>
 * @author Zhangming Qi <qizhanming@gmail.com>
 */
public class WebStatFilter implements Filter {

    private final static Log             LOG                               = LogFactory.getLog(WebStatFilter.class);

    public final static String           PARAM_NAME_SESSION_STAT_ENABLE    = "sessionStatEnable";
    public final static String           PARAM_NAME_SESSION_STAT_MAX_COUNT = "sessionStatMaxCount";
    public static final String           PARAM_NAME_EXCLUSIONS             = "exclusions";
    public static final String           PARAM_NAME_PRINCIPAL_SESSION_NAME = "principalSessionName";
    public static final String           PARAM_NAME_PRINCIPAL_COOKIE_NAME  = "principalCookieName";

    public final static int              DEFAULT_MAX_STAT_SESSION_COUNT    = 1000 * 100;

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

    private String                       principalSessionName;
    private String                       principalCookieName;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                             ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        StatHttpServletResponseWrapper responseWrapper = new StatHttpServletResponseWrapper(httpResponse);

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
        webAppStat.beforeInvoke();

        WebURIStat uriStat = webAppStat.getURIStat(requestURI, false);

        // 第一次访问时，uriStat这里为null，是为了防止404攻击。
        if (uriStat != null) {
            uriStat.beforeInvoke();
        }

        // 第一次访问时，sessionId为null，如果缺省sessionCreate=false，sessionStat就为null。
        if (sessionStat != null) {
            sessionStat.beforeInvoke();
            sessionStat.setLastAccessTimeMillis(startMillis);
        }

        Throwable error = null;
        try {
            chain.doFilter(request, responseWrapper);
        } catch (IOException e) {
            error = e;
            throw e;
        } catch (ServletException e) {
            error = e;
            throw e;
        } catch (RuntimeException e) {
            error = e;
            throw e;
        } catch (Error e) {
            error = e;
            throw e;
        } finally {
            long endNano = System.nanoTime();
            requestStat.setEndNano(endNano);

            long nanos = endNano - startNano;
            webAppStat.afterInvoke(error, nanos);

            if (sessionStat == null) {
                sessionStat = getSessionStat(httpRequest);
                if (sessionStat != null) {
                    sessionStat.beforeInvoke(); // 补偿
                }
            }

            if (sessionStat != null) {
                sessionStat.afterInvoke(error, nanos);
                sessionStat.setPrincipal(getPrincipal(httpRequest));
            }

            if (uriStat == null) {
                int status = responseWrapper.getStatus();
                if (status == HttpServletResponse.SC_NOT_FOUND) {
                    String errorUrl = contextPath + "error_" + status;
                    uriStat = webAppStat.getURIStat(errorUrl, true);
                } else {
                    uriStat = webAppStat.getURIStat(requestURI, true);
                }

                if (uriStat != null) {
                    uriStat.beforeInvoke(); // 补偿调用
                }
            }

            if (uriStat != null) {
                uriStat.afterInvoke(error, nanos);
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

    public String getPrincipal(HttpServletRequest httpRequest) {
        if (principalSessionName != null) {
            HttpSession session = httpRequest.getSession(createSession);
            if (session == null) {
                return null;
            }

            Object sessionValue = session.getAttribute(principalSessionName);

            if (sessionValue == null) {
                return null;
            }

            return sessionValue.toString();
        }

        if (principalCookieName != null) {
            for (Cookie cookie : httpRequest.getCookies()) {
                if (principalCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
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

    public String getPrincipalSessionName() {
        return principalSessionName;
    }

    public String getPrincipalCookieName() {
        return principalCookieName;
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
            String param = config.getInitParameter(PARAM_NAME_PRINCIPAL_SESSION_NAME);
            if (param != null) {
                param = param.trim();
                if (param.length() != 0) {
                    this.principalSessionName = param;
                }
            }
        }

        {
            String param = config.getInitParameter(PARAM_NAME_PRINCIPAL_COOKIE_NAME);
            if (param != null) {
                param = param.trim();
                if (param.length() != 0) {
                    this.principalCookieName = param;
                }
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
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.addJdbcUpdateCount(updateCount);
            }

            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.addJdbcUpdateCount(updateCount);
            }
        }

        @Override
        public void addFetchRowCount(int fetchRowCount) {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.addJdbcFetchRowCount(fetchRowCount);
            }

            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.addJdbcFetchRowCount(fetchRowCount);
            }
        }

        @Override
        public void executeBefore(String sql, boolean inTransaction) {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.incrementJdbcExecuteCount();
            }

            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.incrementJdbcExecuteCount();
            }
        }

        @Override
        public void executeAfter(String sql, long nanos, Throwable error) {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.addJdbcExecuteTimeNano(nanos);
            }

            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.addJdbcExecuteTimeNano(nanos);
            }
        }

        @Override
        public void commit() {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.incrementJdbcCommitCount();
            }

            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.incrementJdbcCommitCount();
            }
        }

        @Override
        public void rollback() {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.incrementJdbcRollbackCount();
            }

            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.incrementJdbcRollbackCount();
            }
        }
    }

    public final static class StatHttpServletResponseWrapper extends HttpServletResponseWrapper implements HttpServletResponse {

        private int status;

        public StatHttpServletResponseWrapper(HttpServletResponse response){
            super(response);
        }

        public void setStatus(int statusCode) {
            super.setStatus(statusCode);
            this.status = statusCode;
        }

        public void setStatus(int statusCode, String statusMessage) {
            super.setStatus(statusCode, statusMessage);
            this.status = statusCode;
        }

        public void sendError(int statusCode, String statusMessage) throws IOException {
            super.sendError(statusCode, statusMessage);
            this.status = statusCode;
        }

        public void sendError(int statusCode) throws IOException {
            super.sendError(statusCode);
            this.status = statusCode;
        }

        public int getStatus() {
            return status;
        }

        public PrintWriter getWriter() throws IOException {
            return super.getWriter();
        }
    }
}
