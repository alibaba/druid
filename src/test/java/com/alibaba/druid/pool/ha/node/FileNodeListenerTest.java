package com.alibaba.druid.pool.ha.node;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileNodeListenerTest {
    @Test
    public void testHaWithPropertiesFile() throws Exception {
        // init a properties file
        String folder = System.getProperty("java.io.tmpdir");
        File file = new File(folder + "/ha.properties");
        file.deleteOnExit();

        Properties properties = new Properties();
        properties.setProperty("bar.username", "sa");
        properties.setProperty("bar.password", "");
        properties.setProperty("bar.url", "jdbc:derby:memory:bar;create=true");
        writePropertiesFile(file, properties);

        // init HighAvailableDataSource
        FileNodeListener listener = new FileNodeListener();
        listener.setFile(file.getAbsolutePath());
        listener.setPrefix("foo");
        listener.setIntervalSeconds(1);
        HighAvailableDataSource dataSource = new HighAvailableDataSource();
        dataSource.setPoolPurgeIntervalSeconds(5);
        dataSource.setDataSourceFile(file.getAbsolutePath());
        dataSource.setNodeListener(listener);
        dataSource.init();

        assertTrue(dataSource.getDataSourceMap().isEmpty());

        // Add one valid DataSource
        properties.setProperty("foo.username", "sa");
        properties.setProperty("foo.password", "");
        properties.setProperty("foo.url", "jdbc:derby:memory:foo;create=true");
        writePropertiesFile(file, properties);
        Thread.sleep(6000);

        assertEquals(1, dataSource.getAvailableDataSourceMap().size());
        DruidDataSource foo = (DruidDataSource) dataSource.getAvailableDataSourceMap().get("foo");
        assertEquals("jdbc:derby:memory:foo;create=true", foo.getUrl());

        // try to emove all but we have to keep one left
        writePropertiesFile(file, new Properties());

        Thread.sleep(6000);
        assertEquals(1, dataSource.getAvailableDataSourceMap().size());

        dataSource.destroy();
    }

    private void writePropertiesFile(File file, Properties properties) throws IOException {
        FileWriter writer = new FileWriter(file);
        properties.store(writer, "");
        writer.close();
    }

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
        listener.setPrefix("prefix0");
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