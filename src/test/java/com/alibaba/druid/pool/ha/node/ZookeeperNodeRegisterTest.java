package com.alibaba.druid.pool.ha.node;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ZookeeperNodeRegisterTest {
    private final static Log LOG = LogFactory.getLog(ZookeeperNodeRegisterTest.class);
    private static TestingServer server;
    private final String PATH = "/ha-druid-datasource";
    private ZookeeperNodeRegister register;

    @BeforeClass
    public static void init() throws Exception {
        server = new TestingServer();
    }

    @AfterClass
    public static void destroy() throws Exception {
        server.close();
    }

    @Before
    public void setUp() throws Exception {
        register = new ZookeeperNodeRegister();
        register.setZkConnectString(server.getConnectString());
        register.setPath(PATH);
        register.init();
    }

    @After
    public void tearDown() throws Exception {
        register.destroy();
        register.getClient().close();
    }

    @Test
    public void testRegister() throws Exception {
        List<ZookeeperNodeInfo> payload = new ArrayList<ZookeeperNodeInfo>();
        ZookeeperNodeInfo node1 = new ZookeeperNodeInfo();
        node1.setPrefix("foo");
        node1.setHost("127.0.0.1");
        node1.setPort(1234);
        node1.setDatabase("foo_db");
        node1.setUsername("foo");
        node1.setPassword("password");
        payload.add(node1);

        ZookeeperNodeInfo node2 = new ZookeeperNodeInfo();
        node2.setPrefix("bar");
        node2.setHost("127.0.0.1");
        node2.setPort(5678);
        node2.setUsername("bar");
        node2.setPassword("password");
        payload.add(node2);

        assertFalse(register.register("test-foo", null));
        assertTrue(register.register("test-foo", payload));
        assertFalse(register.register("test-foo", payload));

        Thread.sleep(1000); // Wait for the node to be created.

        CuratorFramework client = register.getClient();
        List<String> children = client.getChildren().forPath(PATH);
        assertFalse(children.isEmpty());
        assertEquals(1, children.size());
        assertEquals("test-foo", children.get(0));

        byte[] bytes = client.getData().forPath(PATH + "/test-foo");
        Properties properties = new Properties();
        String str = new String(bytes);
        LOG.info("ZK Data: " + str);
        properties.load(new StringReader(str));
        validateNodeProperties(node1, properties);
        validateNodeProperties(node2, properties);
        assertTrue(properties.containsKey("foo.database"));
        assertFalse(properties.containsKey("bar.database"));
        assertEquals("foo_db", properties.getProperty("foo.database"));
    }

    private void validateNodeProperties(ZookeeperNodeInfo node, Properties properties) {
        assertTrue(properties.containsKey(node.getPrefix() + "host"));
        assertTrue(properties.containsKey(node.getPrefix() + "port"));
        assertTrue(properties.containsKey(node.getPrefix() + "username"));
        assertTrue(properties.containsKey(node.getPrefix() + "password"));

        assertEquals(node.getHost(), properties.getProperty(node.getPrefix() + "host"));
        assertEquals(node.getPort().toString(), properties.getProperty(node.getPrefix() + "port"));
        assertEquals(node.getUsername(), properties.getProperty(node.getPrefix() + "username"));
        assertEquals(node.getPassword(), properties.getProperty(node.getPrefix() + "password"));
    }
}