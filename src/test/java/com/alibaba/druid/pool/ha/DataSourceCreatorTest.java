package com.alibaba.druid.pool.ha;

import org.junit.Test;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.Assert.*;

public class DataSourceCreatorTest {
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

}