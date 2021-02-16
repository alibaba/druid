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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.druid.filter.stat.StatFilterContextListenerAdapter;
import com.alibaba.druid.support.http.stat.WebAppStat;
import com.alibaba.druid.support.http.stat.WebRequestStat;
import com.alibaba.druid.support.http.stat.WebSessionStat;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.DruidWebUtils;

public class AbstractWebStatImpl {
    private final static Log LOG                                          = LogFactory.getLog(AbstractWebStatImpl.class);

    public final static int                DEFAULT_MAX_STAT_SESSION_COUNT = 1000 * 1;

    protected WebAppStat                   webAppStat                     = null;

    protected boolean                      sessionStatEnable              = true;
    protected int                          sessionStatMaxCount            = DEFAULT_MAX_STAT_SESSION_COUNT;
    protected boolean                      createSession                  = false;
    protected boolean                      profileEnable                  = false;

    protected String                       contextPath;

    protected String                       principalSessionName;
    protected String                       principalCookieName;
    protected String                       realIpHeader;

    protected WebStatFilterContextListener statFilterContextListener      = new WebStatFilterContextListener();

    public boolean isSessionStatEnable() {
        return sessionStatEnable;
    }

    public void setSessionStatEnable(boolean sessionStatEnable) {
        this.sessionStatEnable = sessionStatEnable;
    }

    public boolean isProfileEnable() {
        return profileEnable;
    }

    public void setProfileEnable(boolean profileEnable) {
        this.profileEnable = profileEnable;
    }

    public String getContextPath() {
        return contextPath;
    }

    public int getSessionStatMaxCount() {
        return sessionStatMaxCount;
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
            long currentMillis = System.currentTimeMillis();

            String userAgent = request.getHeader("user-agent");

            if (sessionStat.getCreateTimeMillis() == -1L) {
                HttpSession session = request.getSession(false);

                if (session != null) {
                    sessionStat.setCreateTimeMillis(session.getCreationTime());
                } else {
                    sessionStat.setCreateTimeMillis(currentMillis);
                }

                webAppStat.computeUserAgent(userAgent);
                webAppStat.incrementSessionCount();
            }

            sessionStat.setUserAgent(userAgent);

            String ip = getRemoteAddress(request);

            sessionStat.addRemoteAddress(ip);
        }

        return sessionStat;
    }

    protected String getRemoteAddress(HttpServletRequest request) {
        String ip = null;
        if (this.realIpHeader != null && this.realIpHeader.length() != 0) {
            ip = request.getHeader(realIpHeader);
        }
        if (ip == null || ip.length() == 0) {
            ip = DruidWebUtils.getRemoteAddr(request);
        }
        return ip;
    }

    public String getSessionId(HttpServletRequest httpRequest) {
        String sessionId = null;

        HttpSession session = httpRequest.getSession(createSession);
        if (session != null) {
            sessionId = session.getId();
        } else {
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("JSESSIONID")) {
                        sessionId = cookie.getValue();
                        break;
                    }
                }

                if (sessionId == null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("JWT-SESSION")) {
                            sessionId = cookie.getValue();
                            break;
                        }
                    }
                }
            }
        }

        return sessionId;
    }

    public String getPrincipal(HttpServletRequest httpRequest) {
        if (principalSessionName != null) {
            HttpSession session = httpRequest.getSession(createSession);
            if (session == null) {
                return null;
            }

            Object sessionValue = null;

            try {
                sessionValue = session.getAttribute(principalSessionName);
            } catch (Exception ex) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("session.getAttribute error", ex);
                }
            }

            if (sessionValue == null) {
                return null;
            }

            return sessionValue.toString();
        }

        if (principalCookieName != null && httpRequest.getCookies() != null) {
            for (Cookie cookie : httpRequest.getCookies()) {
                if (principalCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public class WebStatFilterContextListener extends StatFilterContextListenerAdapter {

        @Override
        public void addUpdateCount(int updateCount) {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.addJdbcUpdateCount(updateCount);
            }
        }

        @Override
        public void addFetchRowCount(int fetchRowCount) {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.addJdbcFetchRowCount(fetchRowCount);
            }
        }

        @Override
        public void executeBefore(String sql, boolean inTransaction) {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.incrementJdbcExecuteCount();
            }
        }

        @Override
        public void executeAfter(String sql, long nanos, Throwable error) {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.addJdbcExecuteTimeNano(nanos);
                if (error != null) {
                    reqStat.incrementJdbcExecuteErrorCount();
                }
            }
        }

        @Override
        public void commit() {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.incrementJdbcCommitCount();
            }
        }

        @Override
        public void rollback() {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.incrementJdbcRollbackCount();
            }
        }

        @Override
        public void pool_connect() {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.incrementJdbcPoolConnectCount();
            }
        }

        @Override
        public void pool_close(long nanos) {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.incrementJdbcPoolCloseCount();
            }
        }

        @Override
        public void resultSet_open() {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.incrementJdbcResultSetOpenCount();
            }
        }

        @Override
        public void resultSet_close(long nanos) {
            WebRequestStat reqStat = WebRequestStat.current();
            if (reqStat != null) {
                reqStat.incrementJdbcResultSetCloseCount();
            }
        }
    }
}
