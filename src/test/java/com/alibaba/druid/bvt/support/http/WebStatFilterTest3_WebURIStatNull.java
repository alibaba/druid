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

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import com.alibaba.druid.filter.stat.StatFilterContext;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.http.stat.WebAppStat;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.http.stat.WebURIStat;

public class WebStatFilterTest3_WebURIStatNull extends TestCase {

    public void test_sessionStatDisable() throws Exception {
        MockServletContext servletContext = new MockServletContext();

        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
        filterConfig.addInitParameter(WebStatFilter.PARAM_NAME_SESSION_STAT_ENABLE, "false");

        WebStatFilter filter = new WebStatFilter();
        WebAppStat appStat = new WebAppStat() {
            public WebURIStat getURIStat(String uri, boolean create) {
                return null;
            }
        };
        filter.setWebAppStat(appStat);
        filter.setProfileEnable(true);

        Assert.assertNotNull(filter.getWebAppStat());

        filter.init(filterConfig);
        
        Assert.assertSame(appStat, filter.getWebAppStat());

        Assert.assertFalse(filter.isSessionStatEnable());
        Assert.assertTrue(WebAppStatManager.getInstance().getWebAppStatSet().contains(appStat));
        Assert.assertTrue(StatFilterContext.getInstance().getListeners().contains(filter.getStatFilterContextListener()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        Assert.assertNull(filter.getSessionStat(request));

        filter.doFilter(request, response, chain);

        Assert.assertEquals(0, appStat.getSessionStatDataList().size());

        filter.destroy();
        
        Assert.assertFalse(WebAppStatManager.getInstance().getWebAppStatSet().contains(appStat));
        Assert.assertFalse(StatFilterContext.getInstance().getListeners().contains(filter.getStatFilterContextListener()));
        
        Map<String, Object> statData = appStat.getStatData();
        Assert.assertEquals(1L, statData.get("RequestCount"));
    }
}
