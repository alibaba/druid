package com.alibaba.druid.bvt.support.http;

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

public class WebStatFilterTest extends TestCase {

    public void test_sessionStatDisable() throws Exception {
        MockServletContext servletContext = new MockServletContext();

        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
        filterConfig.addInitParameter(WebStatFilter.PARAM_NAME_SESSION_STAT_ENABLE, "false");

        WebStatFilter filter = new WebStatFilter();

        Assert.assertNull(filter.getWebAppStat());

        filter.init(filterConfig);
        
        WebAppStat appStat = filter.getWebAppStat();

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
    }
}
