package com.alibaba.druid.pool.ha.node;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import org.apache.curator.test.TestingServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ZookeeperNodeListenerTest {
    private final static Log LOG = LogFactory.getLog(ZookeeperNodeListenerTest.class);
    private static TestingServer server;
    private final String PATH = "/ha-druid-datasource";

    @BeforeClass
    public static void init() throws Exception {
        server = new TestingServer();
    }

    @AfterClass
    public static void destroy() throws Exception {
        server.close();
    }

    @Test
    public void testHaWithZk() throws Exception {
        // 1. Init HighAvailableDataSource
        ZookeeperNodeListener listener = new ZookeeperNodeListener();
        listener.setZkConnectString(server.getConnectString());
        listener.setPrefix("foo");
        listener.setPath(PATH);
        listener.setUrlTemplate("jdbc:derby:memory:${database};create=true");
        HighAvailableDataSource dataSource = new HighAvailableDataSource();
        dataSource.setNodeListener(listener);
        dataSource.setPoolPurgeIntervalSeconds(10);
        dataSource.init();
        assertTrue(dataSource.getDataSourceMap().isEmpty());

        // 2. Register one Node
        ZookeeperNodeRegister register = registerNodeAndReturnRegister();
        ZookeeperNodeRegister register2 = registerAnotherNodeAndReturnRegister();

        Thread.sleep(3000); // Wait for the Node to be created.
        assertFalse(dataSource.getDataSourceMap().isEmpty());
        assertEquals(2, dataSource.getAvailableDataSourceMap().size());
        DruidDataSource ds = (DruidDataSource) dataSource.getAvailableDataSourceMap().get("foo.test-foo");
        assertEquals("jdbc:derby:memory:foo;create=true", ds.getUrl());
        assertNotNull(dataSource.getConnection());

        // 3. Delete one Node
        register.destroy();
        boolean checkBlackList = false;
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            if (dataSource.isInBlackList("foo.test-foo")) {
                assertEquals(1, dataSource.getAvailableDataSourceMap().size());
                assertFalse(dataSource.getDataSourceMap().isEmpty());
                assertNotNull(dataSource.getConnection());
                checkBlackList = true;
                break;
            }
        }
        assertTrue(checkBlackList);
        register2.destroy();
        Thread.sleep(3000); // Wait for the destory to be finished
        dataSource.destroy();
    }

    @Test
    public void testAddOneNode() throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1);
        ZookeeperNodeListener listener = new ZookeeperNodeListener();
        listener.setZkConnectString(server.getConnectString());
        listener.setPath(PATH);
        listener.setUrlTemplate("jdbc:mysql://${host}:${port}/foo");
        listener.setPrefix("foo");
        listener.setObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                NodeEvent[] events = (NodeEvent[]) arg;
                assertEquals(1, events.length);
                NodeEvent event = events[0];
                LOG.info("NodeEvent received: " + event);
                if (NodeEventTypeEnum.ADD == event.getType()) {
                    assertEquals("sa", event.getUsername());
                    assertEquals("", event.getPassword());
                    assertEquals("foo.test-foo", event.getNodeName());
                    assertEquals("jdbc:mysql://127.0.0.1:1234/foo",
                            event.getUrl());
                    cdl.countDown();
                }
            }
        });
        listener.init();

        ZookeeperNodeRegister register = registerNodeAndReturnRegister();
        Thread.sleep(2000); // Wait for the Node to be created.

        cdl.await(10, TimeUnit.SECONDS);
        Properties properties = listener.getProperties();
        assertEquals(6, properties.size());
        assertEquals("sa", properties.getProperty("foo.test-foo.username"));
        assertEquals("", properties.getProperty("foo.test-foo.password"));
        assertEquals("127.0.0.1", properties.getProperty("foo.test-foo.host"));
        assertEquals("1234", properties.getProperty("foo.test-foo.port"));
        assertEquals("jdbc:mysql://127.0.0.1:1234/foo",
                properties.getProperty("foo.test-foo.url"));
        register.destroy();
        listener.destroy();
    }

    @Test
    public void testRemoveOneNode() throws Exception {
        ZookeeperNodeRegister register = registerNodeAndReturnRegister();
        Thread.sleep(2000); // Wait for the Node to be created.

        final CountDownLatch addCDL = new CountDownLatch(1);
        final CountDownLatch removeCDL = new CountDownLatch(1);
        ZookeeperNodeListener listener = new ZookeeperNodeListener();
        listener.setZkConnectString(server.getConnectString());
        listener.setPath(PATH);
        listener.setUrlTemplate("jdbc:mysql://${host}:${port}/foo");
        listener.setPrefix("foo");
        listener.setObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                NodeEvent[] events = (NodeEvent[]) arg;
                assertEquals(1, events.length);
                NodeEvent event = events[0];
                LOG.info("NodeEvent received: " + event);
                if (NodeEventTypeEnum.DELETE == event.getType()) {
                    assertEquals("sa", event.getUsername());
                    assertEquals("", event.getPassword());
                    assertEquals("foo.test-foo", event.getNodeName());
                    assertEquals("jdbc:mysql://127.0.0.1:1234/foo",
                            event.getUrl());
                    removeCDL.countDown();
                } else {
                    addCDL.countDown();
                }
            }
        });
        listener.init();
        listener.update();
        addCDL.await(10, TimeUnit.SECONDS);
        Properties properties = listener.getProperties();
        assertEquals(6, properties.size());

        register.destroy();
        removeCDL.await(10, TimeUnit.SECONDS);
        assertTrue(properties.isEmpty());
        listener.destroy();
    }

    private ZookeeperNodeRegister registerNodeAndReturnRegister() {
        ZookeeperNodeRegister register = new ZookeeperNodeRegister();
        register.setZkConnectString(server.getConnectString());
        register.setPath(PATH);
        register.init();

        List<ZookeeperNodeInfo> payload = new ArrayList<ZookeeperNodeInfo>();
        ZookeeperNodeInfo node = new ZookeeperNodeInfo();
        node.setPrefix("foo");
        node.setHost("127.0.0.1");
        node.setPort(1234);
        node.setDatabase("foo");
        node.setUsername("sa");
        node.setPassword("");
        payload.add(node);
        register.register("test-foo", payload);
        return register;
    }

    private ZookeeperNodeRegister registerAnotherNodeAndReturnRegister() {
        ZookeeperNodeRegister register = new ZookeeperNodeRegister();
        register.setZkConnectString(server.getConnectString());
        register.setPath(PATH);
        register.init();

        List<ZookeeperNodeInfo> payload = new ArrayList<ZookeeperNodeInfo>();
        ZookeeperNodeInfo node = new ZookeeperNodeInfo();
        node.setPrefix("bar");
        node.setHost("127.0.0.1");
        node.setPort(5678);
        node.setDatabase("bar");
        node.setUsername("sa");
        node.setPassword("");
        payload.add(node);
        register.register("test-bar", payload);
        return register;
    }

    @Test
    public void testInitFailure() {
        ZookeeperNodeListener listener = new ZookeeperNodeListener();
        try {
            listener.init();
            fail("zkClient is null and zkConnectString is null");
        } catch (DruidRuntimeException e) {
        }
        listener.setZkConnectString("127.0.0.1:2181");
        try {
            listener.init();
            fail("path is null");
        } catch (DruidRuntimeException e) {
        }
        listener.setPath(PATH);
        try {
            listener.init();
            fail("urlTemplate is null");
        } catch (DruidRuntimeException e) {
        }
    }
}
