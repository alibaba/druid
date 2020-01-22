package com.alibaba.druid.pool.ha.node;

import org.junit.Test;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FileNodeListenerTest {
    @Test
    public void testUpdate() throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);
        String file = "/com/alibaba/druid/pool/ha/ha-with-prefix-datasource.properties";
        FileNodeListener listener = new FileNodeListener();
        listener.setFile(file);
        listener.setPrefix("prefix1");
        listener.setObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                cdl.countDown();
                assertTrue(o instanceof FileNodeListener);
                assertTrue(arg instanceof NodeEvent[]);
                NodeEvent[] events = (NodeEvent[]) arg;
                assertEquals(1, events.length);
                assertEquals(NodeEventTypeEnum.ADD, events[0].getType());
                assertEquals("prefix1.foo", events[0].getNodeName());
                assertEquals("jdbc:derby:memory:foo1;create=true", events[0].getUrl());
            }
        });
        listener.init();
        listener.update();
        assertTrue(cdl.await(100, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testRefresh_emptyPropertiesMatch() {
        String file = "/com/alibaba/druid/pool/ha/ha-with-prefix-datasource.properties";
        FileNodeListener listener = new FileNodeListener();
        listener.setFile(file);
        listener.setPrefix("prefix3");
        List<NodeEvent> list = listener.refresh();

        assertTrue(listener.getProperties().isEmpty());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testRefresh() {
        String file = "/com/alibaba/druid/pool/ha/ha-with-prefix-datasource.properties";
        FileNodeListener listener = new FileNodeListener();
        listener.setFile(file);
        listener.setPrefix("prefix1");
        List<NodeEvent> list = listener.refresh();

        Properties properties = listener.getProperties();
        assertEquals(3, properties.size());
        assertEquals("jdbc:derby:memory:foo1;create=true", properties.getProperty("prefix1.foo.url"));

        assertEquals(1, list.size());
        NodeEvent event = list.get(0);
        assertEquals(NodeEventTypeEnum.ADD, event.getType());
        assertEquals("prefix1.foo", event.getNodeName());
        assertEquals("jdbc:derby:memory:foo1;create=true", event.getUrl());
    }
}