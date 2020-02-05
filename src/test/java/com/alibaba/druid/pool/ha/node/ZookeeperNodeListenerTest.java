package com.alibaba.druid.pool.ha.node;

import com.alibaba.druid.DruidRuntimeException;
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
    public void testAddOneNode() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        ZookeeperNodeListener listener = new ZookeeperNodeListener();
        listener.setZkConnectString(server.getConnectString());
        listener.setPath(PATH);
        listener.setUrlTemplate("jdbc:mysql://${host}:${port}/foo");
        listener.setDataPrefix("foo");
        listener.setObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                NodeEvent[] events = (NodeEvent[]) arg;
                assertEquals(1, events.length);
                NodeEvent event = events[0];
                LOG.info("NodeEvent received: " + event);
                if (NodeEventTypeEnum.ADD == event.getType()) {
                    assertEquals("foo", event.getUsername());
                    assertEquals("password", event.getPassword());
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
        assertEquals(5, properties.size());
        assertEquals("foo", properties.getProperty("foo.test-foo.username"));
        assertEquals("password", properties.getProperty("foo.test-foo.password"));
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

        CountDownLatch addCDL = new CountDownLatch(1);
        CountDownLatch removeCDL = new CountDownLatch(1);
        ZookeeperNodeListener listener = new ZookeeperNodeListener();
        listener.setZkConnectString(server.getConnectString());
        listener.setPath(PATH);
        listener.setUrlTemplate("jdbc:mysql://${host}:${port}/foo");
        listener.setDataPrefix("foo");
        listener.setObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                NodeEvent[] events = (NodeEvent[]) arg;
                assertEquals(1, events.length);
                NodeEvent event = events[0];
                LOG.info("NodeEvent received: " + event);
                if (NodeEventTypeEnum.DELETE == event.getType()) {
                    assertEquals("foo", event.getUsername());
                    assertEquals("password", event.getPassword());
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
        assertEquals(5, properties.size());

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
        node.setUsername("foo");
        node.setPassword("password");
        payload.add(node);
        register.register("test-foo", payload);
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
