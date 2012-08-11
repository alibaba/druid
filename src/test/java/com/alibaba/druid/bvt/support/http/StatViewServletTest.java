package com.alibaba.druid.bvt.support.http;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.ServerSocket;
import java.util.Map;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.VERSION;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

/**
 * Test StatViewServlet' json test.
 * 
 * @author Zhanming
 */
public class StatViewServletTest {

    private final static String contextPath = "/";
    private final static String servletPatternPrefix   = "/druid";
    private Server              server;
    private int                 port;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {}
    
    @Before
    public void setUp() throws Exception {
        // get a port
        ServerSocket s = new ServerSocket(0);
        port = s.getLocalPort();
        s.close();

        server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setWelcomeFiles(new String[] { "index.html" });
        resourceHandler.setResourceBase(".");

        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setContextPath(contextPath);
        // http://localhost:8080/druid/*
        contextHandler.addServlet(new ServletHolder(new StatViewServlet()), servletPatternPrefix + "/*");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, contextHandler, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();
        // in test case, should not use join method.
        // server.join();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void test_basic() throws Exception {
        WebConversation wc = new WebConversation();
        String url = "http://localhost:" + port + contextPath + servletPatternPrefix + "/basic.json";
        WebResponse wr = wc.getResponse(url);
        String jsonString = wr.getText();
        JSONObject json = JSON.parseObject(jsonString);
        
        int resultCode = json.getInteger("ResultCode");
        assertThat(1, equalTo(resultCode));
        
        Map<String, Object> dataMap = json.getJSONObject("Content");
        assertThat(VERSION.getVersionNumber(), equalTo(dataMap.get("Version")));
    }
//    
//    public void test_sql() throws Exception {
//        
//    }

    /**
     * Config an embedding Jetty Server with XML file. It's the same as setUp().
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Resource statViewServerXml = Resource.newSystemResource("jetty/StatViewServer.xml");
        XmlConfiguration configuration = new XmlConfiguration(statViewServerXml.getInputStream());
        Server server = (Server) configuration.configure();
        server.start();
        server.join();
    }
}
