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
package com.alibaba.druid.bvt.support.http;

import junit.framework.TestCase;

import org.junit.Assert;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.http.stat.WebAppStat;

public class WebStatFilterTest2 extends TestCase {

    public void test_lru() throws Exception {
        MockServletContext servletContext = new MockServletContext();

        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
        filterConfig.addInitParameter(WebStatFilter.PARAM_NAME_SESSION_STAT_MAX_COUNT, "3");

        WebStatFilter filter = new WebStatFilter();

        filter.init(filterConfig);

        WebAppStat appStat = filter.getWebAppStat();

        Assert.assertEquals(3, filter.getSessionStatMaxCount());
        Assert.assertEquals(0, appStat.getSessionStatDataList().size());

        final MockHttpSession session_0 = new MockHttpSession(servletContext);
        final MockHttpSession session_1 = new MockHttpSession(servletContext);
        final MockHttpSession session_2 = new MockHttpSession(servletContext);
        final MockHttpSession session_3 = new MockHttpSession(servletContext);
        final MockHttpSession session_4 = new MockHttpSession(servletContext);

        // 第一个session请求2次
        {
            Assert.assertNull(appStat.getSessionStat(session_0.getId()));
            
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain() {

                public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response) {
                    ((MockHttpServletRequest) request).setSession(session_0);
                };
            };

            filter.doFilter(request, response, chain);

            Assert.assertEquals(1, appStat.getSessionStatDataList().size());
            Assert.assertEquals(1, appStat.getSessionStat(session_0.getId()).getRequestCount());
            
            Assert.assertTrue(appStat.getSessionStat(session_0.getId()).getLastAccessTimeMillis() > 0);
        }
        {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain() {
                
                public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response) {
                    ((MockHttpServletRequest) request).setSession(session_0);
                };
            };
            
            filter.doFilter(request, response, chain);
            
            Assert.assertEquals(1, appStat.getSessionStatDataList().size());
            Assert.assertEquals(2, appStat.getSessionStat(session_0.getId()).getRequestCount());
        }
        
        
        // 第2个sesion请求1次
        {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain() {

                public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response) {
                    ((MockHttpServletRequest) request).setSession(session_1);
                };
            };

            filter.doFilter(request, response, chain);

            Assert.assertEquals(2, appStat.getSessionStatDataList().size());
            Assert.assertEquals(2, appStat.getSessionStat(session_0.getId()).getRequestCount());
            Assert.assertEquals(1, appStat.getSessionStat(session_1.getId()).getRequestCount());
        }
        
        // 第3个sesion请求1次
        {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain() {
                
                public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response) {
                    ((MockHttpServletRequest) request).setSession(session_2);
                };
            };
            
            filter.doFilter(request, response, chain);
            
            Assert.assertEquals(3, appStat.getSessionStatDataList().size());
            Assert.assertEquals(2, appStat.getSessionStat(session_0.getId()).getRequestCount());
            Assert.assertEquals(1, appStat.getSessionStat(session_1.getId()).getRequestCount());
            Assert.assertEquals(1, appStat.getSessionStat(session_2.getId()).getRequestCount());
        }
        
        // 第4个sesion请求1次
        {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain() {
                
                public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response) {
                    ((MockHttpServletRequest) request).setSession(session_3);
                };
            };
            
            filter.doFilter(request, response, chain);
            
            Assert.assertEquals(3, appStat.getSessionStatDataList().size());
            Assert.assertNull(appStat.getSessionStat(session_0.getId()));
            Assert.assertEquals(1, appStat.getSessionStat(session_1.getId()).getRequestCount());
            Assert.assertEquals(1, appStat.getSessionStat(session_2.getId()).getRequestCount());
            Assert.assertEquals(1, appStat.getSessionStat(session_3.getId()).getRequestCount());
        }
        
        // 第5个sesion请求1次
        {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain() {
                
                public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response) {
                    ((MockHttpServletRequest) request).setSession(session_4);
                };
            };
            
            filter.doFilter(request, response, chain);
            
            Assert.assertEquals(3, appStat.getSessionStatDataList().size());
            Assert.assertNull(appStat.getSessionStat(session_0.getId()));
            Assert.assertNull(appStat.getSessionStat(session_1.getId()));
            Assert.assertEquals(1, appStat.getSessionStat(session_2.getId()).getRequestCount());
            Assert.assertEquals(1, appStat.getSessionStat(session_3.getId()).getRequestCount());
            Assert.assertEquals(1, appStat.getSessionStat(session_4.getId()).getRequestCount());
        }
    }
}
