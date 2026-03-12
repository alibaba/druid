package com.alibaba.druid;

import com.alibaba.druid.stat.DruidDataSourceStatManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.management.openmbean.CompositeData;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PoolTestCase {
    @BeforeEach
    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        int size = DruidDataSourceStatManager.getInstance().getDataSourceList().size();
        String errorInfo = null;
        if (size > 0) {
            CompositeData compositeData = (CompositeData) DruidDataSourceStatManager
                    .getInstance()
                    .getDataSourceList()
                    .values()
                    .iterator()
                    .next();
            String name = (String) compositeData.get("Name");
            String url = (String) compositeData.get("URL");
            String initStackTrace = (String) compositeData.get("InitStackTrace");

            errorInfo = "Name " + name + ", URL " + url + ", initStackTrace=" + initStackTrace;
        }
        assertEquals(0, size, errorInfo);
    }
}
