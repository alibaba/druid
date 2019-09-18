package com.alibaba.druid.pool.ha;

import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DataSourceCreatorTest {
    @Test
    public void testNameList() {
        String file = "/com/alibaba/druid/pool/ha/ha-with-prefix-datasource.properties";
        DataSourceCreator creator = new DataSourceCreator(file, "prefix1");
        List<String> list = creator.getNameList();
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals("prefix1.foo", list.get(0));

        creator = new DataSourceCreator(file);
        list = creator.getNameList();
        assertEquals(3, list.size());
        for (String k : list) {
            assertNotEquals("bar", k);
        }
    }

    @Test
    public void createMap() throws Exception {
        HighAvailableDataSource highAvailableDataSource = new HighAvailableDataSource();
        String file = "/com/alibaba/druid/pool/ha/ha-datasource.properties";
        DataSourceCreator creator = new DataSourceCreator(file);
        Map<String, DataSource> map = creator.createMap(highAvailableDataSource);
        assertEquals(2, map.size());
        assertNotNull(map.get("foo"));
        assertNotNull(map.get("bar"));
    }

    @Test
    public void createMapWithPrefix() throws Exception {
        HighAvailableDataSource highAvailableDataSource = new HighAvailableDataSource();
        String file = "/com/alibaba/druid/pool/ha/ha-with-prefix-datasource.properties";
        DataSourceCreator creator = new DataSourceCreator(file, "prefix1");
        Map<String, DataSource> map = creator.createMap(highAvailableDataSource);
        assertEquals(1, map.size());
        assertNotNull(map.get("prefix1.foo"));
        assertNull(map.get("prefix2.foo"));

        creator = new DataSourceCreator(file, "prefix2");
        map = creator.createMap(highAvailableDataSource);
        assertEquals(1, map.size());
        assertNull(map.get("prefix1.foo"));
        assertNotNull(map.get("prefix2.foo"));
    }

}