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
package com.alibaba.druid.bvt.stat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.stat.spring.UserService;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.JdbcUtils;

public class DruidStatServiceTest extends TestCase {

    private DruidDataSource dataSource;

    @Override
    protected void setUp() throws Exception {
        DruidStatService.getInstance().setResetEnable(true); 
        // DruidStatService is singleton, reset all for other testcase.
        DruidStatService.getInstance().service("/reset-all.json");
        // clear web app.
        WebAppStatManager.getInstance().getWebAppStatSet().clear();

        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("mergeStat");
        dataSource.setTestOnBorrow(false);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_statService_getSqlList() throws Exception {
        String sql = "select 1";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();
        stmt.close();

        conn.close();

        String result = DruidStatService.getInstance().service("/sql.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);

        List<Map<String, Object>> sqlList = (List<Map<String, Object>>) resultMap.get("Content");

        assertTrue(sqlList.size() > 0);

        Map<String, Object> sqlStat = sqlList.get(0);

        Assert.assertEquals(0, sqlStat.get("RunningCount"));
        Assert.assertEquals(1, sqlStat.get("ExecuteCount"));
        Assert.assertEquals(1, sqlStat.get("FetchRowCount"));
        Assert.assertEquals(0, sqlStat.get("EffectedRowCount"));
    }

    public void test_statService_getSqlById() throws Exception {
        String sql = "select 1";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();
        stmt.close();

        conn.close();

        String result = DruidStatService.getInstance().service("/sql-"
                                                                       + dataSource.getSqlStatMap().values().iterator().next().getId()
                                                                       + ".json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);

        Map<String, Object> sqlStat = (Map<String, Object>) resultMap.get("Content");

        Assert.assertEquals(0, sqlStat.get("RunningCount"));
        Assert.assertEquals(1, sqlStat.get("ExecuteCount"));
        Assert.assertEquals(1, sqlStat.get("FetchRowCount"));
        Assert.assertEquals(0, sqlStat.get("EffectedRowCount"));

        String result2 = DruidStatService.getInstance().service("/sql-" + Integer.MAX_VALUE + ".json");
        resultMap = (Map<String, Object>) JSONUtils.parse(result2);
        Assert.assertNull(resultMap.get("Content"));
    }

    public void test_statService_getDataSourceList() throws Exception {
        {
            DruidStatService.getInstance().service("/reset-all.json");
        }
        
        String sql = "select 1";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();
        stmt.close();

        conn.close();

        String result = DruidStatService.getInstance().service("/datasource.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);

        List<Map<String, Object>> dataSourceList = (List<Map<String, Object>>) resultMap.get("Content");

        Assert.assertTrue(dataSourceList.size() > 0);

        Map<String, Object> dataSourceStat = dataSourceList.get(0);

//        Assert.assertEquals(1, dataSourceStat.get("PoolingCount"));
//        Assert.assertEquals(0, dataSourceStat.get("ActiveCount"));
    }

    public void test_statService_getDataSourceIdList() throws Exception {
        String sql = "select 1";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();
        stmt.close();
        conn.close();

//        String result = DruidStatService.getInstance().service("/datasource.json");
//        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
//        List<Map<String, Object>> dataSourceList = (List<Map<String, Object>>) resultMap.get("Content");
//        Map<String, Object> dataSourceStat = dataSourceList.get(0);
////        assertThat(dataSourceStat, is(not(nullValue())));
//        int id = (Integer) dataSourceStat.get("Identity");
//
//        String resultId = DruidStatService.getInstance().service("/datasource-" + id + ".json");
//        Map<String, Object> resultIdMap = (Map<String, Object>) JSONUtils.parse(resultId);
//        Map<String, Object> dataSourceIdStat = (Map<String, Object>) resultIdMap.get("Content");
//        assertThat((Integer) dataSourceIdStat.get("PoolingCount"), equalTo(1));
    }

    /**
     * Test basic.json request
     * 
     * @throws Exception
     */
    public void test_statService_getBasic() throws Exception {
        String result = DruidStatService.getInstance().service("/basic.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        Map<String, Object> contentMap = (Map<String, Object>) resultMap.get("Content");

        assertThat(contentMap.get("Version"), is(not(nullValue())));
        assertThat(contentMap.get("Drivers"), is(not(nullValue())));
        assertThat((Boolean) contentMap.get("ResetEnable"), is(true));
    }

    public void test_statService_getActiveConnectionStackTrace() throws Exception {
        String sql = "select 1";
        dataSource.setRemoveAbandoned(true);// initiative close connection.
        dataSource.setRemoveAbandonedTimeout(Integer.MAX_VALUE);
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();

        String result = DruidStatService.getInstance().service("/activeConnectionStackTrace.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        List<String> contentList = (List<String>) resultMap.get("Content");
        Object first = contentList.get(0);

        assertThat(first, is(not(nullValue())));
        // close connection at last.
        stmt.close();
        conn.close();
    }

//    public void test_statService_getActiveConnectionStackTraceId() throws Exception {
//        String sql = "select 1";
//        dataSource.setRemoveAbandoned(true);
//        dataSource.setRemoveAbandonedTimeout(Integer.MAX_VALUE);
//        Connection conn = dataSource.getConnection();
//
//        PreparedStatement stmt = conn.prepareStatement(sql);
//        ResultSet rs = stmt.executeQuery();
//        rs.next();
//        rs.close();
//        // get data source.
//        String dsResult = DruidStatService.getInstance().service("/datasource.json");
//        Map<String, Object> dsResultMap = (Map<String, Object>) JSONUtils.parse(dsResult);
//        List<Map<String, Object>> dataSourceList = (List<Map<String, Object>>) dsResultMap.get("Content");
//        Map<String, Object> dataSourceStat = dataSourceList.get(0);
//        assertThat(dataSourceStat, is(not(nullValue())));
//        // get data source id.
//        int id = (Integer) dataSourceStat.get("Identity");
//
//        String result = DruidStatService.getInstance().service("/activeConnectionStackTrace-" + id + ".json");
//        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
//        List<String> contentList = (List<String>) resultMap.get("Content");
//
//        assertThat(contentList.get(0), is(not(nullValue())));
//        stmt.close();
//        conn.close();
//    }

    public void test_statService_returnJSONActiveConnectionStackTrace() throws Exception {
        String result = DruidStatService.getInstance().service("/activeConnectionStackTrace-1.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        assertThat((Integer) resultMap.get("ResultCode"), equalTo(-1));
    }

    public void test_statService_getWebURIList() throws Exception {
        String uri = "/";
        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
        WebStatFilter filter = new WebStatFilter();
        filter.init(filterConfig);
        // first request test
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, response, chain);

        String result = DruidStatService.getInstance().service("/weburi.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        List<Map<String, Object>> webURIList = (List<Map<String, Object>>) resultMap.get("Content");
        assertThat(webURIList.size(), equalTo(1));
        Map<String, Object> webURI = webURIList.get(0);
        assertThat((String) webURI.get("URI"), equalTo(uri));
        assertThat((Integer) webURI.get("RequestCount"), equalTo(1));

        // second request test
        MockHttpServletRequest request2 = new MockHttpServletRequest("GET", uri);
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        MockFilterChain chain2 = new MockFilterChain();
        filter.doFilter(request2, response2, chain2);

        result = DruidStatService.getInstance().service("/weburi.json");
        resultMap = (Map<String, Object>) JSONUtils.parse(result);
        webURIList = (List<Map<String, Object>>) resultMap.get("Content");
        assertThat(webURIList.size(), equalTo(1));
        webURI = webURIList.get(0);
        assertThat((String) webURI.get("URI"), equalTo(uri));
        assertThat((Integer) webURI.get("RequestCount"), equalTo(2));
        filter.destroy();
    }

    public void test_statService_getWebURIById() throws Exception {
        String uri = "/";
        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
        WebStatFilter filter = new WebStatFilter();
        filter.init(filterConfig);
        // first request test
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, response, chain);

        String result = DruidStatService.getInstance().service("/weburi-" + uri + ".json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        Map<String, Object> contentMap = (Map<String, Object>) resultMap.get("Content");
        assertThat((Integer) contentMap.get("RequestCount"), equalTo(1));
        // second request test
        MockHttpServletRequest request2 = new MockHttpServletRequest("GET", uri);
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        MockFilterChain chain2 = new MockFilterChain();
        filter.doFilter(request2, response2, chain2);

        result = DruidStatService.getInstance().service("/weburi-" + uri + ".json");
        resultMap = (Map<String, Object>) JSONUtils.parse(result);
        contentMap = (Map<String, Object>) resultMap.get("Content");
        assertThat((Integer) contentMap.get("RequestCount"), equalTo(2));
        filter.destroy();
    }

    public void test_statService_getWebApp() throws Exception {
        String uri = "/";
        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
        WebStatFilter filter = new WebStatFilter();
        filter.init(filterConfig);
        // first request test
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, response, chain);

        String result = DruidStatService.getInstance().service("/webapp.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        List<Map<String, Object>> contentList = (List<Map<String, Object>>) resultMap.get("Content");
        assertThat(contentList.size(), equalTo(1));
        Map<String, Object> contentMap = contentList.get(0);
        assertThat((Integer) contentMap.get("RequestCount"), equalTo(1));
        // second request test
        MockHttpServletRequest request2 = new MockHttpServletRequest("GET", uri);
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        MockFilterChain chain2 = new MockFilterChain();
        filter.doFilter(request2, response2, chain2);

        result = DruidStatService.getInstance().service("/webapp.json");
        resultMap = (Map<String, Object>) JSONUtils.parse(result);
        contentList = (List<Map<String, Object>>) resultMap.get("Content");
        assertThat(contentList.size(), equalTo(1));
        contentMap = contentList.get(0);
        assertThat((Integer) contentMap.get("RequestCount"), equalTo(2));
        filter.destroy();
    }

    public void test_statService_getWebSession() throws Exception {
        String uri = "/";
        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);

        WebStatFilter filter = new WebStatFilter();
        filter.init(filterConfig);
        // first request test
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        String sessionId = session.getId();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, response, chain);

        String result = DruidStatService.getInstance().service("/websession.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        List<Map<String, Object>> contentList = (List<Map<String, Object>>) resultMap.get("Content");
        assertThat(contentList.size(), equalTo(1));
        Map<String, Object> contentMap = contentList.get(0);
        assertThat((String) contentMap.get("SESSIONID"), equalTo(sessionId));
        // second request test
        MockHttpServletRequest request2 = new MockHttpServletRequest("GET", uri);
        MockHttpSession session2 = new MockHttpSession();
        request2.setSession(session2);
        String sessionId2 = session2.getId();
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        MockFilterChain chain2 = new MockFilterChain();
        filter.doFilter(request2, response2, chain2);

        result = DruidStatService.getInstance().service("/websession.json");
        resultMap = (Map<String, Object>) JSONUtils.parse(result);
        contentList = (List<Map<String, Object>>) resultMap.get("Content");
        assertThat(contentList.size(), equalTo(2));
        contentMap = contentList.get(1);
        assertThat((String) contentMap.get("SESSIONID"), equalTo(sessionId2));
        filter.destroy();
    }

    public void test_statService_getSpring() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                                                                        "classpath:com/alibaba/druid/stat/spring-config-stat.xml");
        UserService userService = (UserService) context.getBean("userService");
        userService.save();
        String result = DruidStatService.getInstance().service("/spring.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        List<Map<String, Object>> contentList = (List<Map<String, Object>>) resultMap.get("Content");
        assertThat(contentList.size(), equalTo(1));
        Map<String, Object> contentMap = contentList.get(0);
        assertThat((String) contentMap.get("Class"), is(not(nullValue())));
        assertThat((Integer) contentMap.get("ExecuteCount"), equalTo(1));
        // second test
        userService.save();
        result = DruidStatService.getInstance().service("/spring.json");
        resultMap = (Map<String, Object>) JSONUtils.parse(result);
        contentList = (List<Map<String, Object>>) resultMap.get("Content");
        assertThat(contentList.size(), equalTo(1));
        contentMap = contentList.get(0);
        assertThat((String) contentMap.get("Class"), is(not(nullValue())));
        assertThat((Integer) contentMap.get("ExecuteCount"), equalTo(2));
    }

    public void test_statService_getSpringDetail() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                                                                        "classpath:com/alibaba/druid/stat/spring-config-stat.xml");
        UserService userService = (UserService) context.getBean("userService");
        userService.save();
        String result = DruidStatService.getInstance().service("/spring-detail.json?class=com.alibaba.druid.stat.spring.UserService&method=save()");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        Map<String, Object> contentMap = (Map<String, Object>) resultMap.get("Content");
        assertThat((Integer) contentMap.get("ExecuteCount"), equalTo(1));
        // second test
        userService.save();
        result = DruidStatService.getInstance().service("/spring-detail.json?class=com.alibaba.druid.stat.spring.UserService&method=save()");
        resultMap = (Map<String, Object>) JSONUtils.parse(result);
        contentMap = (Map<String, Object>) resultMap.get("Content");
        assertThat((Integer) contentMap.get("ExecuteCount"), equalTo(2));
    }

    public void test_statService_getResetAll() throws Exception {

        // data source mock
        String sql = "select 1";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();
        stmt.close();
        conn.close();

        String resultSQL = DruidStatService.getInstance().service("/sql.json");
        Map<String, Object> resultSQLMap = (Map<String, Object>) JSONUtils.parse(resultSQL);
        List<Map<String, Object>> sqlList = (List<Map<String, Object>>) resultSQLMap.get("Content");
        assertThat(sqlList.size(), equalTo(1));
        Map<String, Object> sqlStat = sqlList.get(0);
        assertThat((Integer) sqlStat.get("RunningCount"), equalTo(0));
        // http request mock
        String uri = "/";
        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);

        WebStatFilter filter = new WebStatFilter();
        filter.init(filterConfig);
        // first request test
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        String sessionId = session.getId();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, response, chain);

        String resultWebSession = DruidStatService.getInstance().service("/websession.json");
        Map<String, Object> resultWebSessionMap = (Map<String, Object>) JSONUtils.parse(resultWebSession);
        List<Map<String, Object>> contentWebSessionList = (List<Map<String, Object>>) resultWebSessionMap.get("Content");
        assertThat(contentWebSessionList.size(), equalTo(1));
        Map<String, Object> contentWebSessionMap = contentWebSessionList.get(0);
        assertThat((String) contentWebSessionMap.get("SESSIONID"), equalTo(sessionId));

        // spring mock
        ApplicationContext context = new ClassPathXmlApplicationContext(
                                                                        "classpath:com/alibaba/druid/stat/spring-config-stat.xml");
        UserService userService = (UserService) context.getBean("userService");
        userService.save();
        String resultSpring = DruidStatService.getInstance().service("/spring.json");
        Map<String, Object> resultSpringMap = (Map<String, Object>) JSONUtils.parse(resultSpring);
        List<Map<String, Object>> contentSpringList = (List<Map<String, Object>>) resultSpringMap.get("Content");
        assertThat(contentSpringList.size(), equalTo(1));
        Map<String, Object> contentMap = contentSpringList.get(0);
        assertThat((String) contentMap.get("Class"), is(not(nullValue())));
        assertThat((Integer) contentMap.get("ExecuteCount"), equalTo(1));
        // reset all test
        String result = DruidStatService.getInstance().service("/reset-all.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        assertThat(resultMap.get("content"), is(nullValue()));
        // assert sql
        resultSQL = DruidStatService.getInstance().service("/sql.json");
        resultSQLMap = (Map<String, Object>) JSONUtils.parse(resultSQL);
        sqlList = (List<Map<String, Object>>) resultSQLMap.get("Content");
        assertThat(sqlList, is(nullValue()));
        // assert web session
        resultWebSession = DruidStatService.getInstance().service("/websession.json");
        resultWebSessionMap = (Map<String, Object>) JSONUtils.parse(resultWebSession);
        contentWebSessionList = (List<Map<String, Object>>) resultWebSessionMap.get("Content");
        assertThat(contentWebSessionList, is(nullValue()));
        // assert spring
        resultSpring = DruidStatService.getInstance().service("/spring.json");
        resultSpringMap = (Map<String, Object>) JSONUtils.parse(resultSpring);
        contentSpringList = (List<Map<String, Object>>) resultSpringMap.get("Content");
        assertThat(contentSpringList, is(nullValue()));
    }

    public void test_statService() throws Exception {
        String result = DruidStatService.getInstance().service("/bad.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        assertThat((Integer) resultMap.get("ResultCode"), equalTo(-1));
    }

    public void test_statService_getParameters() throws Exception {
        String url = "?x=a&y=b";
        Map<String, String> parameters = DruidStatService.getParameters(url);
        assertThat(parameters.get("x"), equalTo("a"));
        assertThat(parameters.get("y"), equalTo("b"));
        url = null;
        parameters = DruidStatService.getParameters(url);
        assertThat(parameters.isEmpty(), is(true));
        url = "/%E4%B8%AD%E6%96%87";
        parameters = DruidStatService.getParameters(url);
        assertThat(parameters.isEmpty(), is(true));
    }
}
