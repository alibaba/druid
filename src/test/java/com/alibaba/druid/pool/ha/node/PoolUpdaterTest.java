package com.alibaba.druid.pool.ha.node;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.pool.ha.MockDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PoolUpdaterTest {
    private HighAvailableDataSource haDataSource;
    private PoolUpdater updater;

    @Before
    public void setUp() throws Exception {
        haDataSource = new HighAvailableDataSource();
        Map<String, DataSource> map = new HashMap<String, DataSource>();
        haDataSource.setDataSourceMap(map);

        updater = new PoolUpdater(haDataSource);
        updater.init();
    }

    @After
    public void tearDown() throws Exception {
        haDataSource.destroy();
        haDataSource = null;
        updater = null;
    }

    @Test
    public void testUpdate() {
        haDataSource.getDataSourceMap().put("foo", new MockDataSource("foo"));
        haDataSource.getDataSourceMap().put("bar", new MockDataSource("bar"));

        NodeEvent event = new NodeEvent();
        event.setNodeName("foo");
        event.setType(NodeEventTypeEnum.DELETE);

        updater.update(new FileNodeListener(), new NodeEvent[] { event });
        validateDeleteNode();

        event = new NodeEvent();
        event.setNodeName("foo");
        event.setType(NodeEventTypeEnum.ADD);
        updater.update(new FileNodeListener(), new NodeEvent[] { event });
        assertFalse(updater.getNodesToDel().contains("foo"));
        assertFalse(haDataSource.isInBlackList("foo"));
    }

    @Test
    public void testRemoveDataSources() {
        String url = "jdbc:derby:memory:foo;create=true";
        String name = "foo";
        addNode(url, name);
        DruidDataSource ds = (DruidDataSource) haDataSource.getDataSourceMap().get(name);

        updater.getNodesToDel().add(name);
        haDataSource.addBlackList(name);

        updater.removeDataSources();
        assertTrue(haDataSource.getDataSourceMap().isEmpty());
        assertFalse(haDataSource.isInBlackList(name));
        assertTrue(ds.isClosed());
    }

    @Test
    public void testRemoveDataSources_nodeNotExisted() {
        DataSource ds = new MockDataSource("foo");
        haDataSource.getDataSourceMap().put("foo", ds);
        updater.getNodesToDel().add("bar");
        updater.removeDataSources();
        assertFalse(updater.getNodesToDel().contains("bar"));
        assertEquals(1, haDataSource.getDataSourceMap().size());
    }

    @Test
    public void testRemoveDataSources_notDruid() {
        DataSource ds = new MockDataSource("foo");
        haDataSource.getDataSourceMap().put("foo", ds);
        updater.getNodesToDel().add("foo");
        updater.removeDataSources();
        assertFalse(updater.getNodesToDel().contains("foo"));
        assertTrue(haDataSource.getDataSourceMap().isEmpty());
    }

    @Test
    public void testUpdate_onlyOneLeftToRemove() {
        haDataSource.getDataSourceMap().put("foo", new MockDataSource("foo"));

        NodeEvent event = new NodeEvent();
        event.setNodeName("foo");
        event.setType(NodeEventTypeEnum.DELETE);

        updater.update(new FileNodeListener(), new NodeEvent[] { event });
        assertTrue(haDataSource.getDataSourceMap().containsKey("foo"));
    }

    @Test
    public void testAddNode() {
        String url = "jdbc:derby:memory:foo;create=true";
        String name = "foo";
        addNode(url, name);
        String targetUrl = ((DruidDataSource) haDataSource.getDataSourceMap().get(name)).getUrl();
        assertEquals(1, haDataSource.getDataSourceMap().size());
        assertEquals(url, targetUrl);
    }

    @Test
    public void testAddNode_alreadyExisted() {
        DataSource ds = new MockDataSource("foo");
        haDataSource.getDataSourceMap().put("foo", ds);
        updater.getNodesToDel().add("foo");

        addNode("foo_url", "foo");

        assertFalse(updater.getNodesToDel().contains("foo"));
        assertFalse(haDataSource.isInBlackList("foo"));
        assertEquals(ds, haDataSource.getDataSourceMap().get("foo"));
    }

    @Test
    public void testDeleteNode_notExisted() {
        NodeEvent event = new NodeEvent();
        event.setNodeName("foo");
        event.setType(NodeEventTypeEnum.DELETE);
        updater.deleteNode(event);
        assertTrue(updater.getNodesToDel().isEmpty());
        assertFalse(haDataSource.isInBlackList("foo"));
    }

    @Test
    public void testDeleteNode() {
        haDataSource.getDataSourceMap().put("foo", new MockDataSource("foo"));
        haDataSource.getDataSourceMap().put("bar", new MockDataSource("bar"));

        NodeEvent event = new NodeEvent();
        event.setNodeName("foo");
        event.setType(NodeEventTypeEnum.DELETE);
        updater.deleteNode(event);
        validateDeleteNode();
    }

    private void validateDeleteNode() {
        assertEquals(1, updater.getNodesToDel().size());
        assertTrue(updater.getNodesToDel().contains("foo"));
        assertTrue(haDataSource.isInBlackList("foo"));
    }

    private void addNode(String url, String name) {
        NodeEvent event = new NodeEvent();
        event.setNodeName(name);
        event.setUrl(url);
        event.setType(NodeEventTypeEnum.ADD);
        updater.addNode(event);
    }
}