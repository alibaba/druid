package com.alibaba.druid.pool.ha.node;

import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NodeEventTest {

    @Test
    public void testGetEventListFromProperties_emptyProperties() {
        Properties p1 = new Properties();
        Properties p2 = new Properties();

        List<NodeEvent> list = NodeEvent.getEventsByDiffProperties(p1, p2);
        assertTrue(list.isEmpty());

        p1.setProperty("foo.url", "foo_url");
        p1.setProperty("foo.username", "foo_username");
        p1.setProperty("foo.password", "foo_password");

        list = NodeEvent.getEventsByDiffProperties(p1, p2);
        assertEquals(1, list.size());
        NodeEvent event = list.get(0);
        assertEquals(NodeEventTypeEnum.DELETE, event.getType());
        assertEquals("foo", event.getNodeName());
        assertEquals("foo_url", event.getUrl());
        assertEquals("foo_username", event.getUsername());
        assertEquals("foo_password", event.getPassword());

        list = NodeEvent.getEventsByDiffProperties(p2, p1);
        assertEquals(1, list.size());
        event = list.get(0);
        assertEquals(NodeEventTypeEnum.ADD, event.getType());
        assertEquals("foo", event.getNodeName());
        assertEquals("foo_url", event.getUrl());
        assertEquals("foo_username", event.getUsername());
        assertEquals("foo_password", event.getPassword());
    }
}